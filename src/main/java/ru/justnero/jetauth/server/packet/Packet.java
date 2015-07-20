package ru.justnero.jetauth.server.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static ru.justnero.jetauth.utils.UtilLog.*;

public class Packet {
    
    protected Socket _client;
    protected DataInputStream _input;
    protected DataOutputStream _output;
    
    public void init(Socket client, DataInputStream input, DataOutputStream output) {
        _client = client;
        _input = input;
        _output = output;
    }
    
    public void preProcess() throws IOException {
        
    }
    
    public void process() {
        try {
            _output.writeInt(405);
            _output.flush();
            _input.close();
            _output.close();
        } catch(IOException ex) {
            warning("Unable to process packet.");
            debug(ex);
        }
    }
    
    public void postProcess() {
        
    }
    
    public static Packet getPacket(int packetID) {
        switch(packetID) {
            case  0:
                return new Packet00Handshake();
            case  1:
                return new Packet01GameList();
            case  2:
                return new Packet02UserAuth();
            case  3:
                return new Packet03ClientValidation();
            case  4:
                return new Packet04FileList();
            case  5:
                return new Packet05FileDownload();
            case  6:
                return new Packet06ClientAuth();
            case  7:
                return new Packet07ServerAuth();
            case  8:
                return new Packet08ServerList();
            case  9:
                return new Packet09ServerJoin();
            case 10:
                return new Packet10CheckServer();
            default:
                return new Packet();
        }
    }
    
}
