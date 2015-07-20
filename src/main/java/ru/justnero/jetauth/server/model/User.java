package ru.justnero.jetauth.server.model;

import org.sql2o.Connection;

public class User {
    
    public int id;
    public String name;
    public String email;
    public double money;
    public int gid;
    public String status;
    public String data;
    public long datetime;
    
    public User(int id, String name, String email, double money, int gid, String status, String data, int datetime) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.money = money;
        this.gid = gid;
        this.status = status;
        this.data = data;
        this.datetime = datetime;
    }
    
    public static User get(Connection con, String name) {
        String sql = "SELECT * "
                   + "FROM `user` "
                   + "WHERE `name` = :name";
        return con.createQuery(sql)
                .addParameter("name",name)
                .executeAndFetchFirst(User.class);
    }
    
}
