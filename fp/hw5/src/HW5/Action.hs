{-# LANGUAGE InstanceSigs #-}

module HW5.Action
  ( HIO(..)
  , PermissionException(..)
  , HiPermission(..)
  ) where

import Control.Exception (Exception, throwIO)
import Data.Set (Set, member)
import HW5.Base (HiMonad (..), HiAction (..), HiValue (..))
import qualified Data.ByteString as BS
import System.Directory ( createDirectory, setCurrentDirectory, getCurrentDirectory
                        , doesFileExist, listDirectory )
import qualified Data.Text as T
import qualified Control.Monad
import Data.Text.Encoding (decodeUtf8')
import qualified Data.Sequence as SQ
import Data.Time (getCurrentTime)
import System.Random (getStdRandom, uniformR)

data HiPermission
  = AllowRead
  | AllowWrite
  | AllowTime
  deriving (Show, Eq, Ord)

newtype PermissionException
  = PermissionRequired HiPermission
  deriving (Show, Eq, Ord)

instance Exception PermissionException

newtype HIO a = HIO { runHIO :: Set HiPermission -> IO a }

instance Functor HIO where
  fmap :: (a -> b) -> HIO a -> HIO b
  fmap fun a = HIO $ fmap fun . runHIO a

instance Applicative HIO where
  pure :: a -> HIO a
  pure a = HIO $ const $ return a

  (<*>) :: HIO (a -> b) -> HIO a -> HIO b
  p <*> q = Control.Monad.ap p q

instance Monad HIO where
  (>>=) :: HIO a -> (a -> HIO b) -> HIO b
  (HIO set) >>= fun = HIO $ \a -> set a >>= \after -> runHIO (fun after) a

instance HiMonad HIO where
  runAction :: HiAction -> HIO HiValue
  runAction action = case action of
    HiActionRead path -> HIO $ \permissions -> do
      if member AllowRead permissions
        then do
          isFile <- doesFileExist path
          if isFile then do 
            file <- BS.readFile path 
            let decoded = decodeUtf8' file
            return $ case decoded of
              Right x -> HiValueString x
              Left _  -> HiValueBytes file
          else do 
            list <- listDirectory path
            return $ HiValueList $ SQ.fromList $ HiValueString . T.pack <$> list
        else throwIO $ PermissionRequired AllowRead
    HiActionWrite path bytes -> HIO $ \permissions -> do
      if member AllowWrite permissions
        then do
          BS.writeFile path bytes
          return HiValueNull
        else throwIO $ PermissionRequired AllowWrite
    HiActionMkDir path -> HIO $ \permissions -> do
      if member AllowWrite permissions
        then do
          createDirectory path
          return HiValueNull
        else throwIO $ PermissionRequired AllowWrite
    HiActionChDir path -> HIO $ \permissions -> do
      if member AllowRead permissions
        then do
          setCurrentDirectory path
          return HiValueNull
        else throwIO $ PermissionRequired AllowRead
    HiActionCwd -> HIO $ \permissions -> do
      if member AllowRead permissions
        then HiValueString . T.pack <$> getCurrentDirectory
        else throwIO $ PermissionRequired AllowRead
    HiActionNow -> HIO $ \permissions -> do 
      if member AllowTime permissions
        then HiValueTime <$> getCurrentTime
        else throwIO $ PermissionRequired AllowTime
    HiActionRand left right -> HIO $ \_ -> 
      HiValueNumber . toRational <$> getStdRandom (uniformR (left, right))
    HiActionEcho text -> HIO $ \permissions -> do
      if member AllowWrite permissions
        then do 
          print text
          return HiValueNull
        else throwIO $ PermissionRequired AllowWrite
