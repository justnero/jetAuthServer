package ru.justnero.jetauth.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class UtilCommon {
    
    public static char[] charsList;
    
    static {
        StringBuilder tmp = new StringBuilder();
        tmp.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890");
        tmp.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890");
        tmp.append("!@#$%^&*()<>[]{}_+-=,./?");
        tmp.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890");
        tmp.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890");
        charsList = tmp.toString().toCharArray();
        tmp = null;
    }
    
    public static String generateSession() {
        return generateString(51);
    }
    
    public static String generateSalt() {
        return generateString(32);
    }
    
    public static String generateString(int len) {
        Random r = new Random();
        String salt = "";
        for(int i=0;i<len;i++) {
            salt += charsList[r.nextInt(charsList.length)];
        }
        return salt;
    }

    public static OS getPlatform() {
        String osName = System.getProperty("os.name").toLowerCase();
        if(osName.contains("win")) return OS.windows;
        if(osName.contains("mac")) return OS.macos;
        if(osName.contains("solaris")) return OS.solaris;
        if(osName.contains("sunos")) return OS.solaris;
        if(osName.contains("linux")) return OS.linux;
        if(osName.contains("unix")) return OS.linux;
        return OS.unknown;
    }
    
    public static enum OS {
        linux, solaris, windows, macos, unknown;
    }
    
    public static class ValueSorterPair implements Comparator<Pair<String,String>> {
        @Override
        public int compare(Pair<String, String> a,Pair<String, String> b) {
            return a.right.compareToIgnoreCase(b.right);
        }
    }
    
    public static class Pair<L,R> {

        public L left;
        public R right;

        public Pair(L left, R right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public int hashCode() { 
            return left.hashCode() ^ right.hashCode(); 
        }

        @Override
        public boolean equals(Object o) {
            if(o == null || !(o instanceof Pair)) {
                return false;
            }
            Pair pair = (Pair) o;
            return this.left.equals(pair.left) && this.right.equals(pair.right);
        }

    }
    
}
