package ru.justnero.jetauth.server.packet;

import java.io.File;
import java.io.FileInputStream;

import ru.justnero.jetauth.server.Main;
import ru.justnero.jetauth.utils.UtilFile;

import static ru.justnero.jetauth.utils.UtilLog.*;

class Packet05FileDownload extends Packet {
    
    @Override
    public void process() {
        try {
            String fileName = _input.readUTF();
            byte[] buffer = new byte[Main._config.getIOBuffer()];
            int n = 0;
            File file = UtilFile.getFile(Main._config.getDataDir(),fileName.replaceAll("\\\\","/").replaceAll("\\.\\.","").replaceAll("\0",""));
            if(file.exists() && file.isFile()) {
                _output.writeInt(200);
                _output.writeLong(file.length());
                FileInputStream fis = new FileInputStream(file);
                while((n = fis.read(buffer)) != -1) {
                    _client.getOutputStream().write(buffer,0,n);
                }
                fis.close();
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
    }
    
}
