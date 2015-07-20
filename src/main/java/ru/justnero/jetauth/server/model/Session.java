package ru.justnero.jetauth.server.model;

import org.sql2o.Connection;

public class Session {
    
    public int id;
    public int uid;
    public String launcher;
    public String client;
    public String server;
    public String serverId;
    
    public Session(int id, int uid, String launcher, String client, String server, String serverId) {
        this.id = id;
        this.uid = uid;
        this.launcher = launcher;
        this.client = client;
        this.server = server;
        this.serverId = serverId;
    }
    
    public static Session get(Connection con, int uid) {
        String sql = "SELECT * "
                   + "FROM `game_minecraft` "
                   + "WHERE `uid` = :uid";
        return con.createQuery(sql)
                .addParameter("uid",uid)
                .executeAndFetchFirst(Session.class);
    }
    
    public static void updateLauncher(Connection con, int uid, String value) {
        updateParam(con,uid,"launcher",value);
    }
    
    public static void updateClient(Connection con, int uid, String value) {
        updateParam(con,uid,"client",value);
    }
    
    public static void updateServer(Connection con, int uid, String value) {
        updateParam(con,uid,"server",value);
    }
    
    public static void updateServerID(Connection con, int uid, String value) {
        updateParam(con,uid,"serverID",value);
    }
    
    private static void updateParam(Connection con, int uid, String key, String value) {
        String sql = "UPDATE `game_minecraft` "
                   + "SET `"+key+"` = :value "
                   + "WHERE `uid` = :uid";
        con.createQuery(sql)
                .addParameter("uid",uid)
                .addParameter("value",value)
                .executeUpdate();
    }
    
//    public static boolean existsByUID(int userID) {
//        MetaModel metaModel = getMetaModel();
//        return null != new DB(metaModel.getDbName()).firstCell("SELECT " + metaModel.getIdName() + " FROM " + metaModel.getTableName()
//                + " WHERE `uid` = ?", userID);
//    }
//    
//    public static String getLauncher(int userID) {
//        return getParam(userID,"launcher");
////    }
//    
//    public static String getClient(int userID) {
//        return getParam(userID,"client");
//    }
//    
//    public static String getServer(int userID) {
//        return getParam(userID,"server");
//    }
//    
//    public static String getServerID(int userID) {
//        return getParam(userID,"serverID");
//    }
//    
//    public static void setLauncher(int userID, String key) {
//        setParam(userID,"launcher",key);
//    }
//    
//    public static void setClient(int userID, String key) {
//        setParam(userID,"client",key);
//    }
//    
//    public static void setServer(int userID, String key) {
//        setParam(userID,"server",key);
//    }
//    
//    public static void setServerID(int userID, String key) {
//        setParam(userID,"serverID",key);
//    }
//    
//    private static String getParam(int userID, String param) {
//        Session session = findFirst("`uid`=?",userID);
//        if(session != null) {
//            return session.getString(param);
//        } else {
//            return "";
//        }
//    }
//    
//    private static void setParam(int userID, String param, String key) {
//        Session session = findFirst("`uid`=?",userID);
//        if(session != null) {
//            session.set(param,key).saveIt();
//        } else {
//            Session.createIt("uid",userID,param,key);
//        }
//    }
    
}
