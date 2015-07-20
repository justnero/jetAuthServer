package ru.justnero.jetauth.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import ru.justnero.jetauth.server.Main;

import static ru.justnero.jetauth.utils.UtilHash.*;
import static ru.justnero.jetauth.utils.UtilLog.*;

public class UtilFile {
    
    public static final int NO_ERROR            = 0;
    public static final int NO_FILE_ERROR       = 1;
    public static final int FILE_EXISTS_ERROR   = 1;
    public static final int INPUT_ERROR         = 3;
    public static final int PERMISSION_ERROR    = 4;
    public static final int UNKNOWN_ERROR       = 5;
    public static Exception lastException       = null;
    private static File workDir                 = null;
    
    public static ArrayList<String> getDirHashes(File dir) {
        ArrayList<String> tmp_map = new ArrayList<String>();
        if(dir.listFiles() != null) {
            for(File file : dir.listFiles()) {
                if(file.isDirectory()) {
                    if(!file.getName().equalsIgnoreCase("natives")) {
                        tmp_map.addAll(getDirHashes(file));
                    }
                } else {
                    if(file.getName().endsWith(".jar") || file.getName().endsWith(".zip") || file.getName().endsWith(".class")) {
                        tmp_map.add(sha1(file));
                    }
                }
            }
        }
        return tmp_map;
    }
    
    public static Set<String> getFolderTree(File directory) {
        return getFolderTree(directory,"");
    }
    
    public static Set<String> getFolderTree(File directory, String root) {
        Set<String> retVal = new TreeSet<String>();
        for(File file : directory.listFiles()) {
            if(file.isFile()) {
                retVal.add(root+file.getName());
            } else {
                retVal.addAll(getFolderTree(file,root+file.getName()+"/"));
            }
        }
        return retVal;
    }
    
    public static long[] getFolderSize(File directory) {
        long length = 0;
        long count = 0;
        for(File file : directory.listFiles()) {
            if(file.isFile()) {
                length += file.length();
                count++;
            } else {
                long[] tmp = getFolderSize(file);
                count += tmp[0];
                length += tmp[1];
            }
        }
        return new long[]{count,length};
    }
    
    public static File getFile(String... args) {
        StringBuilder sb = new StringBuilder();
        for(String arg : args) {
            if(sb.length() > 0) {
                sb.append(File.separatorChar);
            }
            sb.append(arg);
        }
        return new File(getWorkDir(),sb.toString());
    }
    
    public static File getWorkDir() {
        return workDir == null ? workDir = getWorkDir("jetAuth") : workDir;
    }

    private static File getWorkDir(String app) {
        String userHome = System.getProperty("user.home",".");
        File dir;
        switch(UtilCommon.getPlatform().ordinal()) {
            case 0:
            case 1:
                dir = new File(userHome,'.'+app+'/');
                break;
            case 2:
                String appData=  System.getenv("APPDATA");
                dir = new File(appData == null ? userHome : appData,"."+app+'/');
                break;
            case 3:
                dir = new File(userHome,"Library/Application Support/"+app+'/');
                break;
            default:
                dir = new File(userHome,app+'/');
        }
        if((!dir.exists()) && (!dir.mkdirs())) {
            throw new RuntimeException("The working directory could not be created: "+dir);
        }
        return dir;
    }
    
    public static int downloadFile(String strURL, String strPath, int buffSize) {
        if(fileExists(strPath)) {
            return FILE_EXISTS_ERROR;
        }
        try {
            Files.createDirectory(Paths.get(strPath).getParent());
            URL connection = new URL(strURL);
            HttpURLConnection urlconn;
            urlconn = (HttpURLConnection) connection.openConnection();
            urlconn.setRequestMethod("GET");
            urlconn.connect();
            try(InputStream in = urlconn.getInputStream(); OutputStream writer = new FileOutputStream(strPath)) {
                byte buffer[] = new byte[buffSize];
                int c = in.read(buffer);
                while(c > 0) {
                    writer.write(buffer, 0, c);
                    c = in.read(buffer);
                }
                writer.flush();
            }
        } catch (IOException ex) {
            lastException = ex;
            debug("Can`t download file from:",strURL," to:",strPath);
            error(ex);
            return UNKNOWN_ERROR;
        }
        return NO_ERROR;
    }
    
    public static int extractFile(String root,String filePath,boolean cleanBefore,boolean deleteAfter) {
        if(!fileExists(filePath))
            return NO_FILE_ERROR;
        BufferedOutputStream dest = null;
        try {
            Path rootPath = Paths.get(root);
            if(cleanBefore) {
                Files.walkFileTree(rootPath,new DeleteWalker());
                Files.deleteIfExists(rootPath);
            }
            if(!fileExists(root)) {
                Files.createDirectory(rootPath);
            }
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(filePath)));
            ZipEntry entry;
            while((entry = zis.getNextEntry()) != null) {
                if(fileExists(root+File.separator+entry.getName())) {
                    continue;
                }
                debug("Extracting: ",entry.getName());
                int count;
                byte data[] = new byte[Main._config.getIOBuffer()];
                if(entry.isDirectory()) {
                    Files.createDirectory(Paths.get(root,entry.getName()));
                } else {
                    int lastSeparator = entry.getName().lastIndexOf('/');
                    if (lastSeparator != -1) {
                        if(!fileExists(root+File.separator+entry.getName().substring(0,lastSeparator))) {
                            Files.createDirectory(Paths.get(root,entry.getName().substring(0,lastSeparator)));
                        }
                    }
                    FileOutputStream fos = new FileOutputStream(root + "/" + entry.getName());
                    dest = new BufferedOutputStream(fos);
                    while((count = zis.read(data)) != -1) {
                        dest.write(data,0,count);
                    }
                    dest.flush();
                    dest.close();
                }
            }
            zis.closeEntry();
            zis.close();
            if(deleteAfter) {
                Files.delete(Paths.get(filePath));
            }
        } catch(Exception ex) {
            lastException = ex;
            error(ex);
            return UNKNOWN_ERROR;
        }
        return NO_ERROR;
    }
    
    public static boolean fileExists(String path) {
        return Files.exists(Paths.get(path));
    }
    
    public static class DeleteWalker implements FileVisitor<Path> {

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }

    }
    
}
