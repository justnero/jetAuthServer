package ru.justnero.jetauth.server.model;

import org.sql2o.Connection;

public class UserBan {
    
    public int id;
    public int uid;
    public String action;
    public long time;
    public String reason;
    public int auid;
    public long datetime;
    
    public UserBan(int id, int uid, String action, int time, String reason, int auid, int datetime) {
        this.id = id;
        this.uid = uid;
        this.action = action;
        this.time = time;
        this.reason = reason;
        this.auid = auid;
        this.datetime = datetime;
    }
    
    public static UserBan get(Connection con, int uid) {
        String sql = "SELECT * "
                   + "FROM `user_ban` "
                   + "WHERE `uid` = :uid "
                   + "ORDER BY `id` DESC";
        return con.createQuery(sql)
                .addParameter("uid",uid)
                .executeAndFetchFirst(UserBan.class);
    }
    
    public static boolean check(Connection con, int uid) {
        UserBan ban = get(con,uid);
        if(ban != null) {
            if(ban.action.equalsIgnoreCase("punish") && ((ban.time + ban.datetime > System.currentTimeMillis()/1000L) || (ban.time == 0))) {
                return true;
            }
        }
        return false;
    }
    
}
