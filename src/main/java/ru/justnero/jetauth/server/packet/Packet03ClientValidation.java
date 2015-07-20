package ru.justnero.jetauth.server.packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.sql2o.Connection;

import ru.justnero.jetauth.server.Main;
import ru.justnero.jetauth.server.MySQL;
import ru.justnero.jetauth.server.model.Game;
import ru.justnero.jetauth.utils.UtilFile;

import static ru.justnero.jetauth.utils.UtilHash.*;
import static ru.justnero.jetauth.utils.UtilLog.*;

class Packet03ClientValidation extends Packet {
    
    @Override
    public void process() {
        Connection con = MySQL.open();
        try {
            int gid = _input.readInt();
            String gameId = String.valueOf(gid);
            String hash = _input.readUTF();
            boolean isGameValid = false;
            for(String val : Main._config.getDataGames().split(",")) {
                if(val.equals(gameId)) {
                    isGameValid = true;
                }
            }
            if(isGameValid) {
                Game game = Game.get(con,gid);
                ArrayList<String> hashes = UtilFile.getDirHashes(UtilFile.getFile(Main._config.getDataDir(),"games",game.code));
                Collections.sort(hashes);
                StringBuilder sb = new StringBuilder();
                for(String temp : hashes) {
                    sb.append(temp);
                }
                _output.writeBoolean(hash.equalsIgnoreCase(sha1(sb.toString())));
            } else {
                _output.writeBoolean(false);
            }
            _output.flush();
            _input.close();
            _output.close();
            _client.close();
        } catch(IOException ex) {
            warning("Unable to process packet.");
            debug(ex);
        }
        con.close();
    }
    
}
