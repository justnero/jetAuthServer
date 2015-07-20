package ru.justnero.jetauth.server;

import java.io.Console;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;

import org.apache.commons.io.output.TeeOutputStream;

import ru.justnero.jetauth.utils.UtilFile;
import ru.justnero.jetauth.utils.UtilLog;

import static ru.justnero.jetauth.utils.UtilLog.*;

public class Main  {
    
    public static Config _config            = null;
    public static Console _console          = null;
    public static ServerSocket _socket      = null;
    
    private static Thread _consoleListen    = null;
    private static Thread _socketListen     = null;
    
    private static final float _version     = 1.0F;
    
    public static void main(String[] args) {
        init();
        
        _console = System.console();
        if(_console != null) {
            _consoleListen = new Thread(new ConsoleListener());
            _consoleListen.setDaemon(true);
            _consoleListen.start();
        }
    }
    
    public static void init() {
        if(!initConfig()) {
            System.exit(1);
        }
        initLogger();
        initSocket();
    }
    
    private static boolean initConfig() {
        _config = new Config(UtilFile.getFile("main.cfg"));
        return _config.init();
    }
    
    public static void initLogger() {
        try {
            FileOutputStream fot = new FileOutputStream(UtilFile.getFile(_config.getLoggerFile()),true);
            UtilLog.out = new PrintStream(new TeeOutputStream(fot,System.out),true);
        } catch(FileNotFoundException ex) {
            UtilLog.out = System.out;
            error("Logger file not found");
            error(ex);
        }
        UtilLog.infoEnabled     = _config.isLoggerInfo();
        UtilLog.warningEnabled  = _config.isLoggerWarning();
        UtilLog.errorEnabled    = _config.isLoggerError();
        UtilLog.debugEnabled    = _config.isLoggerDebug();
    }
    
    public static void initSocket() {
        try {
            if(_socket != null) {
                _socket.close();
                _socket = null;
            }
            _socket = new ServerSocket(_config.getIOPort());
        } catch(IOException ex) {
            error("Can`t listen server socket. Server closing...");
            error(ex);
            System.exit(1);
        }
        if(_socketListen != null) {
            _socketListen.interrupt();
            _socketListen = null;
        }
        _socketListen = new Thread(new SocketListener());
        _socketListen.start();
    }
    
    /** Protocol implementation version
     * 
     * @return version float
     */
    public static float getDataDir() {
        return _version;
    }
    
}
