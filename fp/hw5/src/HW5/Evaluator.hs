module HW5.Evaluator
  ( eval
  ) where

import HW5.Base (HiError (..), HiExpr (..), HiValue (..), HiFun (..), HiMonad (runAction), HiAction (..))
import Control.Monad.Except (runExceptT, ExceptT (ExceptT), MonadError (throwError))
import qualified Data.Text as T
import Data.Semigroup (Semigroup(stimes))
import Data.Ratio (denominator, numerator)
import Data.Text (Text)
import qualified Data.Sequence as SQ
import Data.Sequence ((><))
import Data.Foldable (Foldable(toList))
import qualified Data.ByteString as BS
import Data.Word (Word8)
import Data.Text.Encoding (encodeUtf8, decodeUtf8')
import Codec.Compression.Zlib ( compressWith, defaultCompressParams, CompressParams (compressLevel)
                              , bestCompression, decompressWith, defaultDecompressParams )
import Data.ByteString.Lazy (toStrict, fromStrict)
import Codec.Serialise (serialise, deserialiseOrFail)
import Text.Read (readMaybe)
import Data.Time (addUTCTime, diffUTCTime)
import qualified Data.Map as DM
import Data.Map (keys, elems, fromListWith, assocs, Map)
import qualified Data.Sequence as DS
import Data.Maybe (fromMaybe)

eval :: HiMonad m => HiExpr -> m (Either HiError HiValue)
eval = runExceptT . evalHi

evalHi :: HiMonad m => HiExpr -> ExceptT HiError m HiValue
evalHi expr = case expr of
  HiExprValue value    -> return value
  HiExprApply fun args -> evalApply fun args
  HiExprRun run        -> evalRun run
  HiExprDict dict      -> evalExprDict dict

evalApply :: HiMonad m => HiExpr -> [HiExpr] -> ExceptT HiError m HiValue
evalApply fun args = do
  evalFun <- evalHi fun
  evalApplyArgs (makeHiFun evalFun) args

evalRun :: HiMonad m => HiExpr -> ExceptT HiError m HiValue
evalRun run = do
  evaledRun <- evalHi run
  case evaledRun of
    HiValueAction action -> ExceptT $ Right <$> runAction action
    _                    -> throwError HiErrorInvalidFunction

evalExprDict :: HiMonad m => [(HiExpr, HiExpr)] -> ExceptT HiError m HiValue
evalExprDict dict = do
  evaledDict <- evalExprDictFun dict
  return $ HiValueDict $ DM.fromList evaledDict

evalApplyArgs :: HiMonad m => WrapperHiFun -> [HiExpr] -> ExceptT HiError m HiValue
evalApplyArgs fun args = case fun of
  Unary typeFun   -> evalUnary typeFun args
  Binary typeFun  -> evalBinary typeFun args
  Ternary typeFun -> evalTernary typeFun args
  String str      -> evalString str args
  Seq typeSeq     -> evalSeq typeSeq args
  Array list      -> evalArray list args
  Bytes bytes     -> evalBytes bytes args
  Dict dict       -> evalDict dict args
  Unknown         -> throwError HiErrorInvalidFunction

evalUnary :: HiMonad m => Function -> [HiExpr] -> ExceptT HiError m HiValue
evalUnary fun [a] = do
  evalA <- evalHi a
  evalUnaryFun fun evalA
evalUnary _ _     = throwError HiErrorArityMismatch

evalBinary :: HiMonad m => Function -> [HiExpr] -> ExceptT HiError m HiValue
evalBinary And [a, b] = do
  evalA <- evalHi a
  case evalA of
    HiValueBool False -> return evalA
    HiValueNull       -> return evalA
    _                 -> evalHi b
evalBinary Or [a, b] = do
  evalA <- evalHi a
  case evalA of
    HiValueBool False -> evalHi b
    HiValueNull       -> evalHi b
    _                 -> return evalA
evalBinary fun [a, b] = do
  evalA <- evalHi a
  evalB <- evalHi b
  evalBinaryFun fun evalA evalB
evalBinary _ _        = throwError HiErrorArityMismatch

evalTernary :: HiMonad m => Function -> [HiExpr] -> ExceptT HiError m HiValue
evalTernary fun [a, b, c] = do
  evalA <- evalHi a
  evalTernaryFun fun evalA b c
evalTernary _ _           = throwError HiErrorArityMismatch

evalString :: HiMonad m => Text -> [HiExpr] -> ExceptT HiError m HiValue
evalString str [a]    = do
  evalA <- evalHi a
  evalStringFun str [evalA]
evalString str [a, b] = do
  evalA <- evalHi a
  evalB <- evalHi b
  evalStringFun str [evalA, evalB]
evalString _ _        = throwError HiErrorInvalidArgument

evalArray :: HiMonad m => SQ.Seq HiValue -> [HiExpr] -> ExceptT HiError m HiValue
evalArray list [a]    = do
  evalA <- evalHi a
  evalArrayFun list [evalA]
evalArray list [a, b] = do
  evalA <- evalHi a
  evalB <- evalHi b
  evalArrayFun list [evalA, evalB]
evalArray _ _         = throwError HiErrorInvalidArgument

evalSeq :: HiMonad m => Function -> [HiExpr] -> ExceptT HiError m HiValue
evalSeq fun args = do
  evalArgs <- traverse evalHi args
  evalSeqFun fun evalArgs

evalBytes :: HiMonad m => BS.ByteString -> [HiExpr] -> ExceptT HiError m HiValue
evalBytes bytes [a]    = do
  evalA <- evalHi a
  x <- unpackFromBytes bytes
  case x of
    (HiValueList list) -> evalArrayFun list [evalA]
    _                  -> throwError HiErrorInvalidArgument
evalBytes bytes [a, b] = do
  evalA <- evalHi a
  evalB <- evalHi b
  x <- unpackFromBytes bytes
  case x of
    (HiValueList list) -> do
      y <- evalArrayFun list [evalA, evalB]
      case y of
        (HiValueList list1) -> packToBytes $ toList list1
        _                   -> throwError HiErrorInvalidArgument
    _                  -> throwError HiErrorInvalidArgument
evalBytes _ _         = throwError HiErrorInvalidArgument

evalDict :: HiMonad m => Map HiValue HiValue -> [HiExpr] -> ExceptT HiError m HiValue
evalDict dict [a] = do
  evalA <- evalHi a
  return $ fromMaybe HiValueNull $ DM.lookup evalA dict
evalDict _ _      = throwError HiErrorInvalidArgument

evalUnaryFun :: HiMonad m => Function -> HiValue -> ExceptT HiError m HiValue
evalUnaryFun Serialise value = return $ HiValueBytes $ toStrict $ serialise value
evalUnaryFun fun (HiValueList mySeq) = case fun of
  Length  -> return $ HiValueNumber $ fromIntegral $ SQ.length mySeq
  Reverse -> return $ HiValueList $ SQ.reverse mySeq
  Pack    -> packToBytes $ toList mySeq
  Count   -> count id (toList mySeq)
  _       -> throwError HiErrorInvalidArgument
evalUnaryFun fun (HiValueBytes bytes) = case fun of
  Length      -> return $ HiValueNumber $ fromIntegral $ BS.length bytes
  Reverse     -> return $ HiValueBytes $ BS.reverse bytes
  Unpack      -> unpackFromBytes bytes
  Decode      -> return $ case decodeUtf8' bytes of
    Right x -> HiValueString x
    Left _  -> HiValueNull
  Zip         -> return $ HiValueBytes $ toStrict $
    compressWith defaultCompressParams {compressLevel=bestCompression} $ fromStrict bytes
  Unzip       -> return $ HiValueBytes $ toStrict $ decompressWith defaultDecompressParams $ fromStrict bytes
  Deserialise -> return $ case deserialiseOrFail (fromStrict bytes) of
    Right x -> x
    Left _  -> HiValueNull
  Count       -> count (HiValueNumber . toRational) (BS.unpack bytes)
  _           -> throwError HiErrorInvalidArgument
evalUnaryFun fun (HiValueString str) = case fun of
  Length    -> return $ HiValueNumber $ fromIntegral $ T.length str
  ToUpper   -> return $ HiValueString $ T.toUpper str
  ToLower   -> return $ HiValueString $ T.toLower str
  Reverse   -> return $ HiValueString $ T.reverse str
  Trim      -> return $ HiValueString $ T.strip str
  Encode    -> return $ HiValueBytes  $ encodeUtf8 str
  Read      -> return $ HiValueAction $ HiActionRead $ T.unpack str
  MkDir     -> return $ HiValueAction $ HiActionMkDir $ T.unpack str
  ChDir     -> return $ HiValueAction $ HiActionChDir $ T.unpack str
  ParseTime -> return $ maybe HiValueNull HiValueTime $ readMaybe $ T.unpack str
  Echo      -> return $ HiValueAction $ HiActionEcho str
  Count     -> count (HiValueString . T.singleton) (T.unpack str)
  _         -> throwError HiErrorInvalidArgument
evalUnaryFun fun (HiValueBool a) = case (fun, a) of
  (Not, x) -> return $ HiValueBool $ not x
  (_, _)   -> throwError HiErrorInvalidArgument
evalUnaryFun fun (HiValueDict dict) = case fun of
  Keys   -> return $ HiValueList $ DS.fromList $ keys dict
  Values -> return $ HiValueList $ DS.fromList $ elems dict
  Invert -> return $ HiValueDict $ DM.map HiValueList $ fromListWith (<>) [(value, SQ.fromList [key]) | (key, value) <- assocs dict]
  _      -> throwError HiErrorInvalidArgument
evalUnaryFun _ _                 = throwError HiErrorInvalidArgument

evalBinaryFun :: HiMonad m => Function -> HiValue -> HiValue -> ExceptT HiError m HiValue
evalBinaryFun LessThan a b       = return $ HiValueBool $ a < b
evalBinaryFun GreaterThan a b    = return $ HiValueBool $ a > b
evalBinaryFun Equals a b         = return $ HiValueBool $ a == b
evalBinaryFun NotLessThan a b    = return $ HiValueBool $ a >= b
evalBinaryFun NotGreaterThan a b = return $ HiValueBool $ a <= b
evalBinaryFun NotEquals a b      = return $ HiValueBool $ a /= b
evalBinaryFun fun (HiValueString a) (HiValueString b) = case fun of
  Add   -> return $ HiValueString $ a <> b
  Div   -> return $ HiValueString $ a <> T.pack "/" <> b
  Write -> return $ HiValueAction $ HiActionWrite (T.unpack a) (encodeUtf8 b)
  _     -> throwError HiErrorInvalidArgument
evalBinaryFun Add (HiValueList a) (HiValueList b) = return $ HiValueList $ a <> b
evalBinaryFun Add (HiValueBytes a) (HiValueBytes b) = return $ HiValueBytes $ a <> b
evalBinaryFun Mul (HiValueList a) (HiValueNumber b)
  | b <= 0 || denominator b /= 1 = throwError HiErrorInvalidArgument
  | otherwise                    = return $ HiValueList $ stimes (numerator b) a
evalBinaryFun Mul (HiValueString a) (HiValueNumber b)
  | b <= 0 || denominator b /= 1 = throwError HiErrorInvalidArgument
  | otherwise                    = return $ HiValueString $ stimes (numerator b) a
evalBinaryFun Mul (HiValueBytes a) (HiValueNumber b)
  | b <= 0 || denominator b /= 1 = throwError HiErrorInvalidArgument
  | otherwise                    = return $ HiValueBytes $ stimes (numerator b) a
evalBinaryFun Add (HiValueTime time) (HiValueNumber num) =
  return $ HiValueTime $ addUTCTime (fromRational num) time
evalBinaryFun Sub (HiValueTime time1) (HiValueTime time2) =
  return $ HiValueNumber $ toRational $ diffUTCTime time1 time2
evalBinaryFun fun (HiValueNumber a) (HiValueNumber b) = case (fun, a, b) of
  (Add, x, y)  -> return $ HiValueNumber $ x + y
  (Sub, x, y)  -> return $ HiValueNumber $ x - y
  (Mul, x, y)  -> return $ HiValueNumber $ x * y
  (Div, _, 0)  -> throwError HiErrorDivideByZero
  (Div, x, y)  -> return $ HiValueNumber $ x / y
  (Rand, x, y) -> case (processHiValueNumber x, processHiValueNumber y) of
    (Just nx, Just ny) -> return $ HiValueAction $ HiActionRand nx ny
    _                  -> throwError HiErrorInvalidArgument
  (_, _, _)    -> throwError HiErrorInvalidArgument
evalBinaryFun _ _ _ = throwError HiErrorInvalidArgument

evalTernaryFun :: HiMonad m => Function -> HiValue -> HiExpr -> HiExpr -> ExceptT HiError m HiValue
evalTernaryFun If (HiValueBool a) b c = evalHi $ if a then b else c
evalTernaryFun _ _ _ _                = throwError HiErrorInvalidArgument

evalStringFun :: HiMonad m => Text -> [HiValue] -> ExceptT HiError m HiValue
evalStringFun str [HiValueNumber num] = return $ case processHiValueNumber num of
  Just index -> if index >= 0 && index < T.length str
                    then HiValueString $ T.pack [T.index str index]
                    else HiValueNull
  Nothing    -> HiValueNull
evalStringFun str [a, b]              =  case (a, b) of
  (HiValueNumber x, HiValueNumber y) -> return $ case (processHiValueNumber x, processHiValueNumber y) of
    (Just nx, Just ny) -> HiValueString $ getSubstring str nx ny
    (_, _)             -> HiValueNull
  (HiValueNull, HiValueNumber y)     -> return $ case processHiValueNumber y of
    Just ny -> HiValueString $ getSubstring str 0 ny
    _       -> HiValueNull
  (HiValueNumber x, HiValueNull)     -> return $ case processHiValueNumber x of
    Just nx -> HiValueString $ getSubstring str nx $ T.length str
    _       -> HiValueNull
  (_, _)                             -> throwError HiErrorInvalidArgument
evalStringFun _ _ = throwError HiErrorInvalidArgument

evalArrayFun :: HiMonad m => SQ.Seq HiValue -> [HiValue] -> ExceptT HiError m HiValue
evalArrayFun list [HiValueNumber num] = return $ case processHiValueNumber num of
  Just index -> if index >= 0 && index < SQ.length list
                    then SQ.index list index
                    else HiValueNull
  Nothing    -> HiValueNull
evalArrayFun list [a, b] =  case (a, b) of
  (HiValueNumber x, HiValueNumber y) -> return $ case (processHiValueNumber x, processHiValueNumber y) of
    (Just nx, Just ny) -> HiValueList $ getSublist list nx ny
    (_, _)             -> HiValueNull
  (HiValueNull, HiValueNumber y)     -> return $ case processHiValueNumber y of
    Just ny -> HiValueList $ getSublist list 0 ny
    _       -> HiValueNull
  (HiValueNumber x, HiValueNull)     -> return $ case processHiValueNumber x of
    Just nx -> HiValueList $ getSublist list nx $ SQ.length list
    _       -> HiValueNull
  (_, _)                             -> throwError HiErrorInvalidArgument
evalArrayFun _ _ = throwError HiErrorInvalidArgument

evalSeqFun :: HiMonad m => Function -> [HiValue] -> ExceptT HiError m HiValue
evalSeqFun List args                                = return $ HiValueList $ SQ.fromList args
evalSeqFun Range [HiValueNumber l, HiValueNumber r] = return $ HiValueList $ DS.fromList $ map HiValueNumber [l..r]
evalSeqFun Fold [fun, HiValueList list]             = do
  case (makeHiFun fun, toList list) of
    (Binary _, []) -> return HiValueNull
    (Binary f, xs) -> foldl1 (\x y -> do
      evalX <- x
      evalY <- y
      evalApplyArgs (Binary f) [HiExprValue evalX, HiExprValue evalY]) (map return xs)
    (_, _)         -> throwError HiErrorInvalidArgument
evalSeqFun _ _                                      = throwError HiErrorInvalidArgument

evalExprDictFun :: HiMonad m => [(HiExpr, HiExpr)] -> ExceptT HiError m [(HiValue, HiValue)]
evalExprDictFun [] = return []
evalExprDictFun ((key, value) : remaining) = do
  evalKey   <- evalHi key
  evalValue <- evalHi value
  evalRemaining <- evalExprDictFun remaining
  return $ (evalKey, evalValue) : evalRemaining

getSubstring :: Text -> Int -> Int -> Text
getSubstring str a b =
  T.take (nb - na) (T.drop na doubleStr) where
    na = a + if a < 0 then T.length str else 0
    nb = b + if b < 0 then T.length str else 0
    doubleStr = T.concat [str, str]

getSublist :: SQ.Seq HiValue -> Int -> Int -> SQ.Seq HiValue
getSublist list a b = if a >= SQ.length list then SQ.empty else
  SQ.take (nb - na) (SQ.drop na doubleList) where
    na = a + if a < 0 then SQ.length list else 0
    nb = if b > SQ.length list then SQ.length list
                               else b + if b < 0 then SQ.length list else 0
    doubleList = list >< list

packToBytes :: HiMonad m => [HiValue] -> ExceptT HiError m HiValue
packToBytes list = HiValueBytes . BS.pack <$> traverse hiValueToWord8 list

unpackFromBytes :: HiMonad m => BS.ByteString -> ExceptT HiError m HiValue
unpackFromBytes bytes = return $ HiValueList $ DS.fromList $ map (HiValueNumber . toRational) (BS.unpack bytes)

hiValueToWord8 :: HiMonad m => HiValue -> ExceptT HiError m Word8
hiValueToWord8 (HiValueNumber num) = case processHiValueNumber num of
  Just x  -> if 0 <= x && x <= 255 then return $ fromIntegral x
                                   else throwError HiErrorInvalidArgument
  Nothing -> throwError HiErrorInvalidArgument
hiValueToWord8 _                   = throwError HiErrorInvalidArgument

processHiValueNumber :: Rational -> Maybe Int
processHiValueNumber num = if denominator num == 1
                             then Just $ fromInteger $ numerator num
                             else Nothing

count :: Monad m => (t -> HiValue) -> [t] -> m HiValue
count pack list = return $ HiValueDict $ DM.map HiValueNumber $ fromListWith (+) [(pack key, 1) | key <- list]

data Function
  = Add
  | Sub
  | Mul
  | Div
  | Not
  | And
  | Or
  | LessThan
  | GreaterThan
  | Equals
  | NotLessThan
  | NotGreaterThan
  | NotEquals
  | If
  | Length
  | ToUpper
  | ToLower
  | Reverse
  | Trim
  | List
  | Range
  | Fold
  | Pack
  | Unpack
  | Encode
  | Decode
  | Zip
  | Unzip
  | Serialise
  | Deserialise
  | Read
  | Write
  | MkDir
  | ChDir
  | ParseTime
  | Rand
  | Echo
  | Count
  | Keys
  | Values
  | Invert

data WrapperHiFun
  = Unary Function
  | Binary Function
  | Ternary Function
  | String Text
  | Seq Function
  | Array (SQ.Seq HiValue)
  | Bytes BS.ByteString
  | Dict (Map HiValue HiValue)
  | Unknown

makeHiFun :: HiValue -> WrapperHiFun
makeHiFun (HiValueFunction hiFun) = case hiFun of
  HiFunDiv            -> Binary Div
  HiFunMul            -> Binary Mul
  HiFunAdd            -> Binary Add
  HiFunSub            -> Binary Sub
  HiFunNot            -> Unary Not
  HiFunAnd            -> Binary And
  HiFunOr             -> Binary Or
  HiFunLessThan       -> Binary LessThan
  HiFunGreaterThan    -> Binary GreaterThan
  HiFunEquals         -> Binary Equals
  HiFunNotLessThan    -> Binary NotLessThan
  HiFunNotGreaterThan -> Binary NotGreaterThan
  HiFunNotEquals      -> Binary NotEquals
  HiFunIf             -> Ternary If
  HiFunLength         -> Unary Length
  HiFunToUpper        -> Unary ToUpper
  HiFunToLower        -> Unary ToLower
  HiFunReverse        -> Unary Reverse
  HiFunTrim           -> Unary Trim
  HiFunList           -> Seq List
  HiFunRange          -> Seq Range
  HiFunFold           -> Seq Fold
  HiFunPackBytes      -> Unary Pack
  HiFunUnpackBytes    -> Unary Unpack
  HiFunEncodeUtf8     -> Unary Encode
  HiFunDecodeUtf8     -> Unary Decode
  HiFunZip            -> Unary Zip
  HiFunUnzip          -> Unary Unzip
  HiFunSerialise      -> Unary Serialise
  HiFunDeserialise    -> Unary Deserialise
  HiFunRead           -> Unary Read
  HiFunWrite          -> Binary Write
  HiFunMkDir          -> Unary MkDir
  HiFunChDir          -> Unary ChDir
  HiFunParseTime      -> Unary ParseTime
  HiFunRand           -> Binary Rand
  HiFunEcho           -> Unary Echo
  HiFunCount          -> Unary Count
  HiFunKeys           -> Unary Keys
  HiFunValues         -> Unary Values
  HiFunInvert         -> Unary Invert
makeHiFun (HiValueString text) = String text
makeHiFun (HiValueList list)   = Array list
makeHiFun (HiValueBytes bytes) = Bytes bytes
makeHiFun (HiValueDict dict)   = Dict dict
makeHiFun _ = Unknown
