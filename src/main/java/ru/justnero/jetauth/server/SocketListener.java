package ru.justnero.jetauth.server;

import java.io.IOException;

import static ru.justnero.jetauth.utils.UtilLog.*;

public class SocketListener extends StopableListener {
    
    @Override
    public void run() {
        while(running) {
            try {
                ConnectionManager.processConnection(Main._socket.accept());
            } catch(IOException ex) {
                warning("Client connection error.");
                debug(ex);
            }
        }
    }
    
}
