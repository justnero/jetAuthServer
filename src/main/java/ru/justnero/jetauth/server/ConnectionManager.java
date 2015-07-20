package ru.justnero.jetauth.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import ru.justnero.jetauth.server.packet.Packet;

import static ru.justnero.jetauth.utils.UtilLog.*;

public class ConnectionManager {

    public static void processConnection(final Socket client) {
        Thread t = new Thread("ConnectionManager") {
            @Override
            public void run() {
                DataOutputStream out = null;
                try {
                    DataInputStream inp = new DataInputStream(client.getInputStream());
                    out = new DataOutputStream(client.getOutputStream());
                    Packet packet = Packet.getPacket(inp.readInt());
                    packet.init(client,inp,out);
                    long tmp = System.currentTimeMillis();
                    packet.preProcess();
                    packet.process();
                    packet.postProcess();
                    debug(packet.getClass().getSimpleName()," ",String.valueOf(System.currentTimeMillis()-tmp));
                } catch(IOException ex) {
                    warning("I/O error");
                    debug(ex);
                } finally {
                    try {
                        out.close();
                    } catch(IOException ex) {}
                }
            }
        };
        t.setDaemon(true);
        t.start();
    }
    
}
