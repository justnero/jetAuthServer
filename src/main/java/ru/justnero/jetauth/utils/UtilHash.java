package ru.justnero.jetauth.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static ru.justnero.jetauth.utils.UtilLog.*;

public class UtilHash {
    
    public static final DigestPool md5Pool   = new DigestPool("MD5",20,40,10);
    public static final DigestPool sha1Pool  = new DigestPool("SHA-1",20,40,10);
    
    public static String md5(String str) {
        return hash(md5Pool,str.getBytes());
    }
    
    public static String sha1(String str) {
        return hash(sha1Pool,str.getBytes());
    }
    
    public static String sha1(File file) {
        return hash(sha1Pool,file);
    }
    
    public static String md5(File file) {
        return hash(md5Pool,file);
    }
    
    private static String hash(DigestPool pool, File file) {
        StringBuilder hexString = new StringBuilder();
        MessageDigest handler = (MessageDigest) pool.pull();
        handler.reset();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] dataBytes = new byte[1024];
            int nread = 0; 
            while((nread = fis.read(dataBytes)) != -1) {
                handler.update(dataBytes,0,nread);
            }
            byte[] digestBytes = handler.digest();
            for(int i=0;i<digestBytes.length;i++) {
                String hex = Integer.toHexString(0xff & digestBytes[i]);
                if(hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            fis.close();
        } catch (Exception e) {
            try {
                fis.close();
            } catch (Exception ex) {}
            return e.toString();
        } finally {
            pool.put(handler);
        }
        return hexString.toString();
    }
    
    private static String hash(DigestPool pool, byte[] str) {
        MessageDigest handler = (MessageDigest) pool.pull();
        handler.reset();
        handler.update(str); 
        byte byteData[] = handler.digest();
        pool.put(handler);
        return hexString(byteData);
    }
    
    private static String hexString(byte[] byteData) {
        StringBuilder  hexString = new StringBuilder();
        for(int i=0;i<byteData.length;i++) {
            hexString.append(Integer.toString((byteData[i]&0xff)+0x100,16).substring(1));
        }
        return hexString.toString();
    }
    
    public static class DigestPool extends UtilObjectPool<MessageDigest> {
        
        private final String ALGORITHM;
        
        public DigestPool(String algorithm, int minIdle) {
            ALGORITHM = algorithm;
            initialize(minIdle);
        }
        
        public DigestPool(String algorithm, final int minIdle, final int maxIdle, final long validationInterval) {
            ALGORITHM = algorithm;
            initialize(minIdle);
            executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    int size = pool.size();
                    if(size < minIdle) {
                        int sizeToBeAdded = minIdle - size;
                        for(int i=0;i<sizeToBeAdded;i++) {
                            pool.add(create());
                        }
                    } else if(size > maxIdle) {
                        int sizeToBeRemoved = size - maxIdle;
                        for(int i=0;i<sizeToBeRemoved;i++) {
                            pool.poll();
                        }
                    }
                }
            },validationInterval,validationInterval,TimeUnit.SECONDS);
        }

        @Override
        protected MessageDigest create() {
            try { 
                return MessageDigest.getInstance(ALGORITHM);
            } catch(NoSuchAlgorithmException ex) {
                debug(ex);
                return null;
            }
        }
        
    }
    
}
