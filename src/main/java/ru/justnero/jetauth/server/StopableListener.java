package ru.justnero.jetauth.server;

public abstract class StopableListener implements Runnable {
    
    protected static boolean running    = true;
    
    public static void stop() {
        running = false;
    }
    
}
