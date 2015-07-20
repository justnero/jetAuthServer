package ru.justnero.jetauth.server;

import java.util.Arrays;

import ru.justnero.jetauth.utils.UtilHash;

import static ru.justnero.jetauth.utils.UtilLog.*;

public class ConsoleListener extends StopableListener {
    
    private static Exception lastException  = null;
    
    @Override
    public void run() {
        while(running) {
            try {
                String line = Main._console.readLine();
                processCommand(line);
            } catch(Exception ex) {
                if(lastException == null || lastException.toString().equals(ex.toString())) {
                    warning("Console error");
                    lastException = ex;
                    debug(ex);
                }
            }
        }
    }
    
    private static void processCommand(String cmdLine) {
        ConsoleCommand cmd = ConsoleCommand.parse(cmdLine);
        if(cmd.cmdName().isEmpty()) {
            return;
        }
        if(cmd.args().length == 0) {
            info("Console issued command: \"",cmd.cmdName(),"\"");
        } else {
            info("Console issued command: \"",cmd.cmdName(),"\", arguments: ",Arrays.toString(cmd.args()));
        }
        switch(cmd.cmdName()) {
            case "reload":
                commandReload(cmd);
                break;
            case "stop":
                commandStop(cmd);
                break;
            case "sha1":
                commandSHA1(cmd);
                break;
            case "md5":
                commandMD5(cmd);
                break;
            default:
                info("Command not found");
                break;
        }
    }
    
    private static void commandReload(ConsoleCommand cmd) {
        Main.init();
        info("Configuration reloaded");
    }
    
    private static void commandStop(ConsoleCommand cmd) {
        info("Stopping server...");
        SocketListener.stop();
        ConsoleListener.stop();
        try {
            UtilHash.md5Pool.shutdown();
        } catch(Exception ex) {}
        try {
            UtilHash.md5Pool.shutdown();
        } catch(Exception ex) {}
        System.exit(0);
    }
    
    private static void commandSHA1(ConsoleCommand cmd) {
        if(cmd.args().length > 0) {
            for(String str : cmd.args()) {
                info(UtilHash.sha1(str));
            }
        } else {
            info("Nothing to hash");
        }
    }
    
    private static void commandMD5(ConsoleCommand cmd) {
        if(cmd.args().length > 0) {
            for(String str : cmd.args()) {
                info(UtilHash.md5(str));
            }
        } else {
            info("Nothing to hash");
        }
    }

}
