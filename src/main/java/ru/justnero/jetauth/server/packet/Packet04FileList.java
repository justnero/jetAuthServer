package ru.justnero.jetauth.server.packet;

import java.io.File;

import org.sql2o.Connection;

import ru.justnero.jetauth.server.Main;
import ru.justnero.jetauth.server.MySQL;
import ru.justnero.jetauth.server.model.Game;
import ru.justnero.jetauth.utils.UtilFile;

import static ru.justnero.jetauth.utils.UtilLog.*;

class Packet04FileList extends Packet {
    
    @Override
    public void process() {
        Connection con = MySQL.open();
        try {
            int gid = _input.readInt();
            File dir = null;
            String path = null;
            if(gid == 0) {
                String OS = _input.readUTF().replaceAll("\\.\\.","").replaceAll("\0","");
                dir = UtilFile.getFile(Main._config.getDataDir(),"natives",OS);
                path = "natives/"+OS;
            } else {
                Game game = Game.get(con,gid);
                if(game != null) {
                    String gameName = game.code;
                    dir = UtilFile.getFile(Main._config.getDataDir(),"games",gameName);
                    path = "games/"+gameName;
                }
            }
            if(dir != null && path != null) {
                _output.writeInt(200);
                long[] tmp = UtilFile.getFolderSize(dir);
                _output.writeLong(tmp[0]);
                _output.writeLong(tmp[1]);
                _output.writeUTF(path);
                for(String file : UtilFile.getFolderTree(dir)) {
                    _output.writeUTF(file);
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
