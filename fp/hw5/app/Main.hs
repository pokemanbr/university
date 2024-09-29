module Main (main) where

import System.Console.Haskeline
    (defaultSettings, getInputLine, outputStrLn, runInputT)
import HW5.Parser (parse)
import HW5.Evaluator (eval)
import HW5.Pretty (prettyValue)
import Control.Monad.IO.Class (MonadIO(liftIO))
import HW5.Action (HIO(runHIO), HiPermission(..))
import Data.Set (fromList)

main :: IO ()
main = runInputT defaultSettings loop
  where
    loop = do
      minput <- getInputLine "hi> "
      case minput of
        Nothing    -> return ()
        Just ""    -> loop
        Just input -> do
          case parse input of
            Left err   -> outputStrLn $ "Parse error: " ++ show err
            Right expr -> do
              let evalResult = eval expr
              res <- liftIO $ runHIO evalResult $ fromList [AllowRead, AllowWrite, AllowTime]
              case res of
                Left err  -> outputStrLn $ "Eval error: " ++ show err
                Right val -> outputStrLn $ show $ prettyValue val
          loop
