package ru.justnero.jetauth.server.packet;

import org.sql2o.Connection;

import ru.justnero.jetauth.server.MySQL;
import ru.justnero.jetauth.server.model.Session;
import ru.justnero.jetauth.server.model.User;
import ru.justnero.jetauth.utils.UtilCommon;

import static ru.justnero.jetauth.utils.UtilHash.*;
import static ru.justnero.jetauth.utils.UtilLog.*;

class Packet06ClientAuth extends Packet {
    
    private final String _pepper = sha1("UO does not works :P");
    
    @Override
    public void process() {
        Connection con = MySQL.begin();
        try {
            String username = _input.readUTF().toLowerCase();
            String sessionID = _input.readUTF().toLowerCase();
            User user = User.get(con,username);
            if(user != null) {
                if(sha1(sha1(sha1(Session.get(con,user.id).launcher+_client.getInetAddress().getHostAddress())+_pepper)).equalsIgnoreCase(sessionID)) {
                    String sid = sha1(UtilCommon.generateSession()).substring(0, 32);
                    Session.updateClient(con,user.id,sid);
                    _output.writeInt(200);
                    _output.writeUTF(sid);
                } else {
                    _output.writeInt(401);
                }
            } else {
                _output.writeInt(401);
            }
            _output.flush();
            _input.close();
            _output.close();
            _client.close();
            con.commit();
        } catch(Exception ex) {
            warning("Unable to process packet.");
            debug(ex);
            con.rollback();
        }
    }
    
}
