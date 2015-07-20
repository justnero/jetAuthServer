package ru.justnero.jetauth.utils;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Scanner;

public class UtilLog {
    
    public static boolean infoEnabled       = true;
    public static boolean warningEnabled    = true;
    public static boolean errorEnabled      = true;
    public static boolean debugEnabled      = false;
    public static PrintStream out           = System.out;
    
    public static void info(String... list) {
        if(infoEnabled) {
            String time = new Timestamp(System.currentTimeMillis()).toString();
            out.print(time.substring(0,time.length()-4));
            out.print(" [INFO] ");
            println(list);
        }
    }
    
    public static void warning(String... list) {
        if(warningEnabled) {
            String time = new Timestamp(System.currentTimeMillis()).toString();
            out.print(time.substring(0,time.length()-4));
            out.print(" [WARNING] ");
            println(list);
        }
    }
    
    public static void error(String... list) {
        if(errorEnabled) {
            String time = new Timestamp(System.currentTimeMillis()).toString();
            out.print(time.substring(0,time.length()-4));
            out.print(" [ERROR] ");
            println(list);
        }
    }
    
    public static void error(Throwable ex) {
        if(errorEnabled) {
            StringWriter error = new StringWriter();
            ex.printStackTrace(new PrintWriter(error));
            Scanner scan = new Scanner(error.toString());
            scan.useDelimiter("\n");
            String time = new Timestamp(System.currentTimeMillis()).toString();
            while(scan.hasNext()) {
                println(time.substring(0,time.length()-4)," [ERROR] ",scan.nextLine());
            }
        }
    }
    
    public static void debug(String... list) {
        if(debugEnabled) {
            String time = new Timestamp(System.currentTimeMillis()).toString();
            out.print(time.substring(0,time.length()-4));
            out.print(" [DEBUG] ");
            println(list);
        }
    }
    
    public static void debug(Throwable ex) {
        if(debugEnabled) {
            StringWriter error = new StringWriter();
            ex.printStackTrace(new PrintWriter(error));
            Scanner scan = new Scanner(error.toString());
            scan.useDelimiter("\n");
            String time = new Timestamp(System.currentTimeMillis()).toString();
            while(scan.hasNext()) {
                println(time.substring(0,time.length()-4)," [DEBUG] ",scan.nextLine());
            }
        }
    }
    
    public static void println(String... list) {
        for(String part : list) {
            out.print(part);
        }
        out.println();
    }
    
    
}
