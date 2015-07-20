package ru.justnero.jetauth.server.model;

import java.util.List;

import org.sql2o.Connection;

public class Server {
    
    public int id;
    public String name;
    public String desc;
    public int gid;
    public String address;
    public int order;
    public String status;
    
    public Server(int id, String name, String desc, int gid, String address, int order, String status) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.gid = gid;
        this.address = address;
        this.order = order;
        this.status = status;
    }
    
    public static List<Server> get(Connection con, int gid) {
        String sql = "SELECT * "
                   + "FROM `game_server` "
                   + "WHERE `gid` = :gid AND `status` = 'enabled' "
                   + "ORDER BY `order`";
        return con.createQuery(sql)
                .addParameter("gid",gid)
                .executeAndFetch(Server.class);
    }
    
    public static Server getFirst(Connection con, int id) {
        String sql = "SELECT * "
                   + "FROM `game_server` "
                   + "WHERE `id` = :id AND `status` = 'enabled'";
        return con.createQuery(sql)
                .addParameter("id",id)
                .executeAndFetchFirst(Server.class);
    }
    
}
