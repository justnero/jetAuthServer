package ru.justnero.jetauth.server.packet;

import java.util.Map;

import org.lorecraft.phparser.SerializedPhpParser;

import org.sql2o.Connection;

import ru.justnero.jetauth.server.Main;
import ru.justnero.jetauth.server.MySQL;
import ru.justnero.jetauth.server.model.Authenticate;
import ru.justnero.jetauth.server.model.Session;
import ru.justnero.jetauth.server.model.User;
import ru.justnero.jetauth.server.model.LogAuthenticate;
import ru.justnero.jetauth.server.model.UserBan;
import ru.justnero.jetauth.utils.UtilCommon;
import ru.justnero.jetauth.utils.UtilFile;

import static ru.justnero.jetauth.utils.UtilHash.*;
import static ru.justnero.jetauth.utils.UtilLog.*;

class Packet02UserAuth extends Packet {
    
    private final String _pepper = sha1("If you can read this - contact Nero");
    
    @Override
    public void process() {
        Connection con = MySQL.open();
        try {
            String username = _input.readUTF().toLowerCase();
            String password = _input.readUTF().toLowerCase();
            String hash = _input.readUTF();
            boolean isValid = hash.equals(sha1(_pepper+sha1(UtilFile.getFile(Main._config.getDataDir(),"launcher",Main._config.getDataLauncher()+".jar")))) 
                           || hash.equals(sha1(_pepper+sha1(UtilFile.getFile(Main._config.getDataDir(),"launcher",Main._config.getDataLauncher()+".exe"))));
            if(isValid) {
                User user = User.get(con,username);
                if(user != null) {
                    Authenticate uAuth = Authenticate.get(con,user.id);
                    if(uAuth != null) {
                        Map<String,String> data = (Map<String,String>) new SerializedPhpParser(uAuth.data).parse();
                        if(!user.status.equalsIgnoreCase("verified")) {
                            _output.writeInt(406);
                        } else if(!sha1(password+sha1(data.get("salt"))).equalsIgnoreCase(data.get("key"))) {
                            _output.writeInt(401);
                        } else if(UserBan.check(con,user.id)) {
                            _output.writeInt(403);
                            _output.writeUTF(UserBan.get(con,user.id).reason);
                        } else {
                            String sid = sha1(UtilCommon.generateSession()).substring(0, 32);
                            LogAuthenticate.log(con,user.id,_client.getInetAddress().getHostAddress());
                            Session.updateLauncher(con,user.id,sid);
                            _output.writeInt(200);
                            _output.writeInt(user.id);
                            _output.writeUTF(sha1(sid+_client.getInetAddress().getHostAddress()));
                            _output.writeUTF(user.name);
                        }
                    } else {
                        _output.writeInt(401);
                    }
                    
                } else {
                    _output.writeInt(404);
                }
            } else {
                _output.writeInt(505);
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
