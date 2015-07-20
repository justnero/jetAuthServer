package ru.justnero.jetauth.server.model;

import org.sql2o.Connection;

public class Authenticate {
    
    public int id;
    public int uid;
    public String vendor;
    public String data;
    
    public Authenticate(int id, int uid, String vendor, String data) {
        this.id = id;
        this.uid = uid;
        this.vendor = vendor;
        this.data = data;
    }
    
    public static Authenticate get(Connection con, int uid) {
        String sql = "SELECT * "
                   + "FROM `user_authenticate` "
                   + "WHERE `uid` = :uid AND `vendor` = 'password'";
        return con.createQuery(sql)
                .addParameter("uid",uid)
                .executeAndFetchFirst(Authenticate.class);
    }
    
}
