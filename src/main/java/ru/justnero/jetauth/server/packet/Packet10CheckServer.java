package ru.justnero.jetauth.server.packet;

import org.sql2o.Connection;

import ru.justnero.jetauth.server.MySQL;
import ru.justnero.jetauth.server.model.Session;
import ru.justnero.jetauth.server.model.User;

import static ru.justnero.jetauth.utils.UtilLog.*;

class Packet10CheckServer extends Packet {
    
    @Override
    public void process() {
        Connection con = MySQL.open();
        try {
            String username = _input.readUTF();
            String serv = _input.readUTF();
            User user = User.get(con,username);
            if(user != null) {
                if(Session.get(con,user.id).serverId.equalsIgnoreCase(serv)) {
                    _output.writeInt(200);
                } else {
                    _output.writeInt(401);
                }
            } else {
                _output.writeInt(404);
            }
            _output.flush();
            _input.close();
            _output.close();
            _client.close();
        } catch(Exception ex) {
            warning("Unable to process packet.");
            debug(ex);
        }
        con.close();
    }
    
}
