module HW5.Parser
  ( parse
  ) where

import Data.Void (Void)
import HW5.Base (HiExpr (..), HiFun (..), HiValue (..), HiAction (..))
import Text.Megaparsec.Error (ParseErrorBundle)
import Text.Megaparsec ( Parsec, runParser, MonadParsec (eof, hidden, try, notFollowedBy), skipMany
                       , choice, between, sepBy1, (<|>), manyTill, sepBy, optional, satisfy, many )
import Control.Monad.Combinators.Expr (Operator (InfixL, InfixN, InfixR), makeExprParser)
import qualified Text.Megaparsec.Char.Lexer as L
import Text.Megaparsec.Char (space1, char)
import qualified Data.ByteString as BS
import qualified Data.Text as T
import Data.Word (Word8)
import Control.Monad (void)
import Data.Char (isAlpha, isAlphaNum)

type Parser = Parsec Void String

parse :: String -> Either (ParseErrorBundle String Void) HiExpr
parse = runParser (sc *> pExpr <* eof) "parser"

pExpr :: Parser HiExpr
pExpr = makeExprParser pHiExpr hiTable

pHiExpr :: Parser HiExpr
pHiExpr = do
  term <- pHiTerm
  expr <- pArguments term <|> return term
  sc *> pDot expr <* sc

betweenBrackets :: Parser a -> Parser a
betweenBrackets = between (symbol "(") (symbol ")")

pArguments :: HiExpr -> Parser HiExpr
pArguments expr = do
  args <- betweenBrackets (pExpr `sepBy1` symbol ",")
  let appliedArgs = HiExprApply expr args
  pDot =<< pArguments appliedArgs <|> return appliedArgs

pHiTerm :: Parser HiExpr
pHiTerm = choice [ betweenBrackets pExpr
                 , sc *> pHiExprValue <* sc ]

pRun :: HiExpr -> Parser HiExpr
pRun expr = do
  run <- optional $ char '!'
  case run of
    Just _  -> pDot $ HiExprRun expr
    Nothing -> return expr

pDot :: HiExpr -> Parser HiExpr
pDot expr = do
  application <- optional $ char '.' *> acceptableAfterDot
  case application of
    Just list -> do 
      let appliedExpr = HiExprApply expr $ map (HiExprValue . HiValueString . T.pack) list
      pDot =<< pArguments appliedExpr <|> return appliedExpr
    Nothing   -> pRun expr

  where acceptableAfterDot = ((:) <$> satisfy isAlpha <*> many (satisfy isAlphaNum)) `sepBy1` char '-'


pHiExprValue :: Parser HiExpr
pHiExprValue = (HiExprValue <$> pHiValue) <|> pHiList <|> pHiDict

pHiList :: Parser HiExpr
pHiList = HiExprApply (HiExprValue $ HiValueFunction HiFunList) <$> between (symbol "[") (symbol "]") (pExpr `sepBy` symbol ",")

pHiDict :: Parser HiExpr
pHiDict = HiExprDict <$> between (symbol "{") (symbol "}") (pHiDictPair `sepBy` symbol ",")

pHiDictPair :: Parser (HiExpr, HiExpr)
pHiDictPair = do
  key <- pExpr
  void $ symbol ":"
  value <- pExpr
  return (key, value)

pHiValue :: Parser HiValue
pHiValue = pHiValueNumber <|> pHiValueBool <|> pHiValueNull <|> pHiValueFun <|> pHiValueString <|> pHiValueBytes <|> pHiValueAction

pHiValueNumber :: Parser HiValue
pHiValueNumber = lexeme $ HiValueNumber . toRational <$> L.signed sc L.scientific

pHiValueBool :: Parser HiValue
pHiValueBool = HiValueBool <$> choice [ False <$ symbol "false"
                                      , True  <$ symbol "true" ]

pHiValueNull :: Parser HiValue
pHiValueNull = HiValueNull <$ symbol "null"

pHiValueString :: Parser HiValue
pHiValueString = HiValueString . T.pack <$> (char '\"' *> manyTill L.charLiteral (char '\"'))

pHiValueBytes :: Parser HiValue
pHiValueBytes = HiValueBytes . BS.pack <$> (symbol "[#" *> manyTill pHiBytes (symbol "#]"))

pHiBytes :: Parser Word8
pHiBytes = do
  first <- pHiByte
  second <- pHiByte
  return $ 16 * first + second

pHiByte :: Parser Word8
pHiByte = choice [ 0  <$ symbol "0"
                 , 1  <$ symbol "1"
                 , 2  <$ symbol "2"
                 , 3  <$ symbol "3"
                 , 4  <$ symbol "4"
                 , 5  <$ symbol "5"
                 , 6  <$ symbol "6"
                 , 7  <$ symbol "7"
                 , 8  <$ symbol "8"
                 , 9  <$ symbol "9"
                 , 10 <$ symbol "a"
                 , 11 <$ symbol "b"
                 , 12 <$ symbol "c"
                 , 13 <$ symbol "d"
                 , 14 <$ symbol "e"
                 , 15 <$ symbol "f" ]

pHiValueAction :: Parser HiValue
pHiValueAction = HiValueAction <$> choice [ HiActionCwd <$ symbol "cwd"
                                          , HiActionNow <$ symbol "now" ]

pHiValueFun :: Parser HiValue
pHiValueFun =
  HiValueFunction <$> choice [ HiFunDiv            <$ symbol "div"
                             , HiFunMul            <$ symbol "mul"
                             , HiFunAdd            <$ symbol "add"
                             , HiFunSub            <$ symbol "sub"
                             , HiFunAnd            <$ symbol "and"
                             , HiFunOr             <$ symbol "or"
                             , HiFunLessThan       <$ symbol "less-than"
                             , HiFunGreaterThan    <$ symbol "greater-than"
                             , HiFunEquals         <$ symbol "equals"
                             , HiFunNotLessThan    <$ symbol "not-less-than"
                             , HiFunNotGreaterThan <$ symbol "not-greater-than"
                             , HiFunNotEquals      <$ symbol "not-equals"
                             , HiFunNot            <$ symbol "not"
                             , HiFunIf             <$ symbol "if"
                             , HiFunLength         <$ symbol "length"
                             , HiFunToUpper        <$ symbol "to-upper"
                             , HiFunToLower        <$ symbol "to-lower"
                             , HiFunReverse        <$ symbol "reverse"
                             , HiFunTrim           <$ symbol "trim"
                             , HiFunList           <$ symbol "list"
                             , HiFunRange          <$ symbol "range"
                             , HiFunFold           <$ symbol "fold"
                             , HiFunPackBytes      <$ symbol "pack-bytes"
                             , HiFunUnpackBytes    <$ symbol "unpack-bytes"
                             , HiFunEncodeUtf8     <$ symbol "encode-utf8"
                             , HiFunDecodeUtf8     <$ symbol "decode-utf8"
                             , HiFunZip            <$ symbol "zip"
                             , HiFunUnzip          <$ symbol "unzip"
                             , HiFunSerialise      <$ symbol "serialise"
                             , HiFunDeserialise    <$ symbol "deserialise"
                             , HiFunRead           <$ symbol "read"
                             , HiFunWrite          <$ symbol "write"
                             , HiFunMkDir          <$ symbol "mkdir"
                             , HiFunChDir          <$ symbol "cd"
                             , HiFunParseTime      <$ symbol "parse-time"
                             , HiFunRand           <$ symbol "rand"
                             , HiFunEcho           <$ symbol "echo"
                             , HiFunCount          <$ symbol "count"
                             , HiFunKeys           <$ symbol "keys"
                             , HiFunValues         <$ symbol "values"
                             , HiFunInvert         <$ symbol "invert" ]

hiTable :: [[Operator Parser HiExpr]]
hiTable = [ [ pBinaryL              "*"     HiFunMul
            , pBinaryNotFollowedBy  "/" "=" HiFunDiv ]

          , [ pBinaryL              "+"     HiFunAdd
            , pBinaryL              "-"     HiFunSub ]

          , [ pBinaryN              "=="    HiFunEquals
            , pBinaryN              "/="    HiFunNotEquals
            , pBinaryN              "<="    HiFunNotGreaterThan
            , pBinaryN              ">="    HiFunNotLessThan
            , pBinaryN              "<"     HiFunLessThan
            , pBinaryN              ">"     HiFunGreaterThan ]

          , [ pBinaryR              "&&"    HiFunAnd ]
          , [ pBinaryR              "||"    HiFunOr ] ]

applyHiFun :: HiFun -> HiExpr -> HiExpr -> HiExpr
applyHiFun hiFun a b = HiExprApply (HiExprValue (HiValueFunction hiFun)) [a, b]

sc :: Parser ()
sc = skipMany $ hidden space1

symbol :: String -> Parser String
symbol = L.symbol sc

lexeme :: Parser a -> Parser a
lexeme = L.lexeme sc

pBinaryL :: String -> HiFun -> Operator Parser HiExpr
pBinaryL name hiFun = InfixL (applyHiFun hiFun <$ symbol name)

pBinaryN :: String -> HiFun -> Operator Parser HiExpr
pBinaryN name hiFun = InfixN (applyHiFun hiFun <$ symbol name)

pBinaryR :: String -> HiFun -> Operator Parser HiExpr
pBinaryR name hiFun = InfixR (applyHiFun hiFun <$ symbol name)

pBinaryNotFollowedBy :: String -> String -> HiFun -> Operator Parser HiExpr
pBinaryNotFollowedBy name notName hiFun =
  InfixL (applyHiFun hiFun <$ (lexeme . try) (symbol name <* notFollowedBy (symbol notName)))
