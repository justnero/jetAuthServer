package ru.justnero.jetauth.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static ru.justnero.jetauth.utils.UtilLog.*;

public class Config extends Properties {
    
    private String  _loggerFile     = "jetAuth.log";
    private boolean _loggerInfo     = true;
    private boolean _loggerWarning  = true;
    private boolean _loggerError    = true;
    private boolean _loggerDebug    = false;
    private int _ioPort             = 15600;
    private int _ioBuffer           = 4096;
    private String _mysqlDSN        = "localhost:3306/jetauth";
    private String _mysqlUser       = "root";
    private String _mysqlPassword   = "";
    private String _dataLauncher    = "Launcher";
    private String _dataGames       = "1";
    private String _dataDir         = "data";
    
    private final String STR_TRUE   = "TRUE";
    private final String STR_FALSE  = "FALSE";
    private final File _configFile;
    
    public Config(File configFile) {
        _configFile = configFile;
    }
    
    public boolean init() {
        try {
            load(new FileInputStream(_configFile));
        } catch(IOException ex) {
            error("Error while loading configuration file");
            error(ex);
            return false;
        }
        _loggerFile     = getString("logger.file",_loggerFile);
        _loggerInfo     = getBoolean("logger.info",_loggerInfo);
        _loggerWarning  = getBoolean("logger.warning",_loggerWarning);
        _loggerError    = getBoolean("logger.error",_loggerError);
        _loggerDebug    = getBoolean("logger.debug",_loggerDebug);

        _ioPort         = getInteger("io.port",_ioPort);
        _ioBuffer       = getInteger("io.buffer",_ioBuffer);
        
        _mysqlDSN       = getString("mysql.dsn",_mysqlDSN);
        _mysqlUser      = getString("mysql.user",_mysqlUser);
        _mysqlPassword  = getString("mysql.password",_mysqlPassword);

        _dataDir        = getString("data.dir",_dataDir);
        _dataGames      = getString("data.games",_dataGames);
        _dataLauncher   = getString("data.launcher",_dataLauncher);
        
        return true;
    }

    /** Logger file name
     * 
     * @return logger file name string
     */
    public String getLoggerFile() {
        return _loggerFile;
    }

    /** Logger info stream enabled
     * 
     * @return is logger info stream enabled
     */
    public boolean isLoggerInfo() {
        return _loggerInfo;
    }

    /** Logger warning stream enabled
     * 
     * @return is logger warning stream enabled
     */
    public boolean isLoggerWarning() {
        return _loggerWarning;
    }

    /** Logger error stream enabled
     * 
     * @return is logger error stream enabled
     */
    public boolean isLoggerError() {
        return _loggerError;
    }

    /** Logger debug stream enabled
     * 
     * @return is logger debug stream enabled
     */
    public boolean isLoggerDebug() {
        return _loggerDebug;
    }

    /** Binded port number for Socket I/O
     * 
     * @return port number
     */
    public int getIOPort() {
        return _ioPort;
    }

    /** Buffer size for I/O operations
     * 
     * @return the _ioBuffer
     */
    public int getIOBuffer() {
        return _ioBuffer;
    }

    /** MySQL DSN suffix formated like host:port/database
     * 
     * @return DNS string
     */
    public String getMysqlDSN() {
        return _mysqlDSN;
    }

    /** MySQL user name
     * 
     * @return user name string
     */
    public String getMysqlUser() {
        return _mysqlUser;
    }

    /** MySQL user password
     * 
     * @return user password string
     */
    public String getMysqlPassword() {
        return _mysqlPassword;
    }

    /** Data directory
     * 
     * @return directory name string
     */
    public String getDataDir() {
        return _dataDir;
    }

    /** Data games list, separated by single comma ","
     * 
     * @return games list string, separated by single comma ","
     */
    public String getDataGames() {
        return _dataGames;
    }

    /** Data launcher file name
     * 
     * @return launcher name string
     */
    public String getDataLauncher() {
        return _dataLauncher;
    }
    
    private int getInteger(String key, int defValue) {
        try {
            return Integer.parseInt(this.getProperty(key,String.valueOf(defValue)));
        } catch(NumberFormatException ex) {
            error("Key ",key," is not an integer");
            error(ex);
            return defValue;
        }
    }
    
    private boolean getBoolean(String key, boolean defValue) {
        String tmp = this.getProperty(key,defValue ? STR_TRUE : STR_FALSE);
        if(tmp != null && (tmp.equalsIgnoreCase("true") || tmp.equalsIgnoreCase("false"))) {
            return tmp.equalsIgnoreCase("true");
        } else {
            error("Key ",key," is not a boolean");
            return defValue;
        }
    }
    
    private String getString(String key, String defValue) {
        return this.getProperty(key,defValue);
    }
    
}
