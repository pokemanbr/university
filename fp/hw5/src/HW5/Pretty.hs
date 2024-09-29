module HW5.Pretty
  ( prettyValue
  ) where

import Prettyprinter (Doc, Pretty (pretty), viaShow)
import Prettyprinter.Render.Terminal (AnsiStyle)

import HW5.Base (HiValue (..), HiFun (..), HiAction (..))
import Data.Ratio (numerator, denominator)
import Data.Scientific (fromRationalRepetendUnlimited)
import Data.Sequence (Seq)
import Data.ByteString (ByteString, unpack)
import Data.Word (Word8)
import Numeric (showHex)
import Data.Time (UTCTime)
import Data.Map (Map)
import qualified Data.Map as DM
import qualified Data.Foldable as DF
import Data.Foldable (Foldable(fold))

prettyValue :: HiValue -> Doc AnsiStyle
prettyValue (HiValueNumber num)    = prettyNumber num
prettyValue (HiValueFunction fun)  = prettyFunction fun
prettyValue (HiValueBool flag)     = prettyBool flag
prettyValue HiValueNull            = pretty "null"
prettyValue (HiValueString str)    = viaShow str
prettyValue (HiValueList mySeq)    = prettySeq mySeq
prettyValue (HiValueBytes bytes)   = prettyBytes bytes
prettyValue (HiValueAction action) = prettyAction action
prettyValue (HiValueTime time)     = prettyTime time
prettyValue (HiValueDict dict)     = prettyDict dict

prettyBool :: Bool -> Doc AnsiStyle
prettyBool flag = pretty $ if flag then "true" else "false"

prettyNumber :: Rational -> Doc AnsiStyle
prettyNumber num = case (numerator num, denominator num) of
  (n, 1) -> pretty n
  (n, d) -> let (scientific, repetend) = fromRationalRepetendUnlimited num in
            case repetend of
              Just _  -> prettyInfiniteFraction n d
              Nothing -> pretty $ show scientific

prettyInfiniteFraction :: Integer -> Integer -> Doc AnsiStyle
prettyInfiniteFraction num den = pretty $
  let (division, reminder) = quotRem num den in
  if division == 0 then show num ++ "/" ++ show den
                   else (if num >= 0 then "" else "-")
                     ++ show (abs division)
                     ++ (if num > 0 then " + " else " - ")
                     ++ show (abs reminder)
                     ++ "/"
                     ++ show (abs den)

prettyAction :: HiAction -> Doc AnsiStyle
prettyAction action = case action of
  HiActionRead path        -> pretty "read(" <> viaShow path <> pretty ")"
  HiActionWrite path bytes -> pretty "write(" <> viaShow path <> pretty ", " <> prettyBytes bytes <> pretty ")"
  HiActionMkDir path       -> pretty "mkdir(" <> viaShow path <> pretty ")"
  HiActionChDir path       -> pretty "cd(" <> viaShow path <> pretty ")"
  HiActionCwd              -> pretty "cwd"
  HiActionNow              -> pretty "now"
  HiActionRand left right  -> pretty "rand( " <> viaShow left <> pretty ", " <> viaShow right <> pretty " )"
  HiActionEcho text        -> pretty "echo(" <> viaShow text <> pretty ")"

prettyTime :: UTCTime -> Doc AnsiStyle
prettyTime time = pretty "parse-time(\"" <> viaShow time <> pretty "\")"

prettySeq :: Seq HiValue -> Doc AnsiStyle
prettySeq mySeq = pretty "[ " <> prettyList (DF.toList mySeq) <> pretty " ]"

prettyList :: [HiValue] -> Doc AnsiStyle
prettyList list = fold $ drop 1 $ concatMap ((\x -> [pretty ", ", x]) . prettyValue) list

prettyBytes :: ByteString -> Doc AnsiStyle
prettyBytes bytes = pretty "[# " <> prettyBytesList (unpack bytes) <> pretty " #]"

prettyBytesList :: [Word8] -> Doc AnsiStyle
prettyBytesList bytes = fold $ drop 1 $ concatMap (\x -> [pretty " ", pretty (makeHex x)]) bytes

prettyDict :: Map HiValue HiValue -> Doc AnsiStyle
prettyDict dict = pretty "{ " <> prettyDictList (DM.toList dict) <> pretty " }"

prettyDictList :: [(HiValue, HiValue)] -> Doc AnsiStyle
prettyDictList list = fold $ drop 1 $ concatMap (\(key, value) -> [pretty ", ", prettyValue key, pretty ": ",prettyValue value]) list

makeHex :: Word8 -> String
makeHex x = (if x < 16 then "0" else "") ++ showHex x ""

prettyFunction :: HiFun -> Doc AnsiStyle
prettyFunction fun = pretty $ case fun of
  HiFunDiv            -> "div"
  HiFunMul            -> "mul"
  HiFunAdd            -> "add"
  HiFunSub            -> "sub"
  HiFunNot            -> "not"
  HiFunAnd            -> "and"
  HiFunOr             -> "or"
  HiFunLessThan       -> "less-than"
  HiFunGreaterThan    -> "greater-than"
  HiFunEquals         -> "equals"
  HiFunNotLessThan    -> "not-less-than"
  HiFunNotGreaterThan -> "not-greater-than"
  HiFunNotEquals      -> "not-equals"
  HiFunIf             -> "if"
  HiFunLength         -> "length"
  HiFunToUpper        -> "to-upper"
  HiFunToLower        -> "to-lower"
  HiFunReverse        -> "reverse"
  HiFunTrim           -> "trim"
  HiFunList           -> "list"
  HiFunRange          -> "range"
  HiFunFold           -> "fold"
  HiFunPackBytes      -> "pack-bytes"
  HiFunUnpackBytes    -> "unpack-bytes"
  HiFunEncodeUtf8     -> "encode-utf8"
  HiFunDecodeUtf8     -> "decode-utf8"
  HiFunZip            -> "zip"
  HiFunUnzip          -> "unzip"
  HiFunSerialise      -> "serialise"
  HiFunDeserialise    -> "deserialise"
  HiFunRead           -> "read"
  HiFunWrite          -> "write"
  HiFunMkDir          -> "mkdir"
  HiFunChDir          -> "cd"
  HiFunParseTime      -> "parse-time"
  HiFunRand           -> "rand"
  HiFunEcho           -> "echo"
  HiFunCount          -> "count"
  HiFunKeys           -> "keys"
  HiFunValues         -> "values"
  HiFunInvert         -> "invert"