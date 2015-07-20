package ru.justnero.jetauth.server.packet;

import java.util.List;

import org.sql2o.Connection;

import ru.justnero.jetauth.server.MySQL;
import ru.justnero.jetauth.server.model.Game;

import static ru.justnero.jetauth.utils.UtilLog.*;

class Packet01GameList extends Packet {
    
    @Override
    public void process() {
        Connection con = MySQL.open();
        try {
            List<Game> games = Game.get(con);
            if(games.size() > 0) {
                _output.writeInt(200);
                _output.writeInt(games.size());
                for(Game game : games) {
                    _output.writeInt(game.id);
                    _output.writeUTF(game.code);
                    _output.writeUTF(game.name);
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
