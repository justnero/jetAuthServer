package ru.justnero.jetauth.server.model;

import java.util.List;
import org.sql2o.Connection;
import ru.justnero.jetauth.server.Main;

public class Game {
    
    public int id;
    public String code;
    public String name;
    public String desc;
    public int order;
    public String monitor;
    public String status;
    
    public Game(int id, String code, String name, String desc, int order, String monitor, String status) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.desc = desc;
        this.order = order;
        this.monitor = monitor;
        this.status = status;
    }
    
    public static List<Game> get(Connection con) {
        String sql = "SELECT * "
                   + "FROM `game` "
                   + "WHERE `id` = ANY("+Main._config.getDataGames()+") AND `status` = 'enabled' "
                   + "ORDER BY `order`";
        return con.createQuery(sql)
                .executeAndFetch(Game.class);
    }
    
    public static Game get(Connection con, int id) {
        String sql = "SELECT * "
                   + "FROM `game` "
                   + "WHERE `id` = :id AND `status` = 'enabled'";
        return con.createQuery(sql)
                .addParameter("id",id)
                .executeAndFetchFirst(Game.class);
    }
    
}
