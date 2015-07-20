package ru.justnero.jetauth.server.model;

import org.sql2o.Connection;

public class LogAuthenticate {
    
    public int id;
    public int uid;
    public String method;
    public String ip;
    public long datetime;
    
    public LogAuthenticate(int id, int uid, String method, String ip, int datetime) {
        this.id = id;
        this.uid = uid;
        this.method = method;
        this.ip = ip;
        this.datetime = datetime;
    }
    
    public static void log(Connection con, int uid, String ip) {
        String sql = "INSERT INTO `log_authenticate` "
                   + "(`uid`, `method`, `ip`, `datetime`)"
                   + "VALUES (:uid, 'launcher', :ip, :datetime)";
        con.createQuery(sql)
                .addParameter("uid",uid)
                .addParameter("ip",ip)
                .addParameter("datetime",System.currentTimeMillis()/1000L)
                .executeUpdate();
    }
    
}
