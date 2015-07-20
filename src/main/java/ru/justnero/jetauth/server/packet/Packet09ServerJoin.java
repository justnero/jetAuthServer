package ru.justnero.jetauth.server.packet;

import java.util.ArrayList;
import java.util.Arrays;

import org.sql2o.Connection;

import ru.justnero.jetauth.server.MySQL;
import ru.justnero.jetauth.server.model.Game;
import ru.justnero.jetauth.server.model.Server;
import ru.justnero.jetauth.server.model.Session;
import ru.justnero.jetauth.server.model.User;
import ru.justnero.jetauth.utils.UtilFile;

import static ru.justnero.jetauth.utils.UtilHash.*;
import static ru.justnero.jetauth.utils.UtilLog.*;

class Packet09ServerJoin extends Packet {
    
    @Override
    public void process() {
        Connection con = MySQL.begin();
        try {
            String username = _input.readUTF();
            String sessionID = _input.readUTF();
            String serv = _input.readUTF();
            User user = User.get(con,username);
            if(user != null) {
                if(sha1(sha1(Session.get(con,user.id).server+_client.getInetAddress().getHostAddress())+serv).equalsIgnoreCase(sessionID)) {
                    int serverID = _input.readInt();
                    String hash = _input.readUTF();
                    Server server = Server.getFirst(con,serverID);
                    Game game = Game.get(con,server.gid);
                    ArrayList<String> tmp = UtilFile.getDirHashes(UtilFile.getFile("server",game.code));
                    String[] hashes = tmp.toArray(new String[tmp.size()]);
                    Arrays.sort(hashes);
                    StringBuilder sb = new StringBuilder();
                    for(String temp : hashes) {
                        sb.append(temp);
                    }
                    if(!hash.equalsIgnoreCase(sha1(sb.toString()))) {
                        _output.writeUTF("Изменение лаунчера или клиента. Вы так не зайдёте. Перезайдите в лаунчер.");
                    } else {
                        Session.updateServerID(con,user.id,serv);
                        _output.writeUTF("OK");
                    }
                } else {
                    _output.writeUTF("Ошибка авторизации. Возможно у вас изменился IP. Попробуйте перезапустить лаунчер.");
                }
            } else {
                _output.writeUTF("Вы не существуете. У вас всё в порядке?");
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
