package ru.justnero.jetauth.server.packet;

import java.io.IOException;

import ru.justnero.jetauth.server.Main;
import ru.justnero.jetauth.utils.UtilFile;

import static ru.justnero.jetauth.utils.UtilHash.*;
import static ru.justnero.jetauth.utils.UtilLog.*;

class Packet00Handshake extends Packet {
    
    private final String _pepper = sha1("If you can read this - contact Nero");
    
    @Override
    public void process() {
        try {
            String hash = _input.readUTF();
            boolean isValid = hash.equals(sha1(_pepper+sha1(UtilFile.getFile(Main._config.getDataDir(),"launcher",Main._config.getDataLauncher()+".jar")))) 
                           || hash.equals(sha1(_pepper+sha1(UtilFile.getFile(Main._config.getDataDir(),"launcher",Main._config.getDataLauncher()+".exe"))));
            _output.writeInt(isValid? 200 : 423);
            _output.flush();
            _input.close();
            _output.close();
            _client.close();
        } catch(IOException ex) {
            warning("Unable to process packet.");
            debug(ex);
        }
    }
    
}
