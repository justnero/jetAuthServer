package ru.justnero.jetauth.server.packet;

import java.util.List;

import org.sql2o.Connection;

import ru.justnero.jetauth.server.Main;
import ru.justnero.jetauth.server.MySQL;
import ru.justnero.jetauth.server.model.Server;

import static ru.justnero.jetauth.utils.UtilLog.*;

class Packet08ServerList extends Packet {
    
    @Override
    public void process() {
        Connection con = MySQL.open();
        try {
            int gid = _input.readInt();
            List<Server> servers;
            boolean gameAllowed = false;
            for(String val : Main._config.getDataGames().split(",")) {
                if(val.equals(String.valueOf(gid))) {
                    gameAllowed = true;
                    break;
                }
            }
            if(gameAllowed) {
                servers = Server.get(con,gid);
                if(servers.size() > 0) {
                    _output.writeInt(200);
                    _output.writeInt(servers.size());
                    for(Server server : servers) {
                        _output.writeInt(server.id);
                        _output.writeUTF(server.name);
                        _output.writeUTF(server.address);
                    }
                } else {
                    _output.writeInt(404);
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
