package ru.justnero.jetauth.server;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static ru.justnero.jetauth.utils.UtilLog.*;

public class MySQL {
    
    public static Sql2o sql2o = null;
    
    public static Connection open() {
        initSql2o();
        return sql2o.open();
    }
    
    public static Connection begin() {
        initSql2o();
        return sql2o.beginTransaction();
    }
    
    private static void initSql2o() {
        if(sql2o == null) {
            debug("Sql2o instance is nul, creating one");
            String url = "jdbc:mysql://"+Main._config.getMysqlDSN()+"?autoReconnect=true&characterEncoding=utf8";
            String user = Main._config.getMysqlUser();
            String pass = Main._config.getMysqlPassword();
            sql2o = new Sql2o(url,user,pass);
            debug("Created Sql2o instance");
        }
    }
    
}
