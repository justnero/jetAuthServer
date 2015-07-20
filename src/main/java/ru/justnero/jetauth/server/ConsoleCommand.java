package ru.justnero.jetauth.server;

import java.util.ArrayList;

public class ConsoleCommand {
    
    private final String _cmdName;
    private final String[] _args;
    
    private ConsoleCommand(String cmdName, String[] args) {
        _cmdName = cmdName;
        _args = args;
    }
    
    public String cmdName() {
        return _cmdName;
    }
    
    public String[] args() {
        return _args;
    }
    
    public static ConsoleCommand parse(String cmdLine) {
        String cmd = "";
        ArrayList<String> args = new ArrayList<String>();
        int head = 0;
        char[] chars = cmdLine.toCharArray();
        if(chars.length > 0) {
            while(head < chars.length && chars[head] == ' ') {
                head++;
            }
            while(head < chars.length && chars[head] != ' ') {
                cmd += chars[head];
                head++;
            }
            String tmp = "";
            boolean isQuote = false;
            while(head < chars.length) {
                if(chars[head] == ' ' && !isQuote) {
                    if(tmp.length() > 0) {
                        args.add(tmp);
                        tmp = "";
                    }
                    while(head < chars.length && chars[head] == ' ') {
                        head++;
                    }
                }
                if(chars[head] == '"') {
                    isQuote = !isQuote;
                    head++;
                }
                if(head >= chars.length) {
                    break;
                }
                tmp += chars[head];
                head++;
            }
            if(!tmp.isEmpty()) {
                args.add(tmp);
            }
        }
        return new ConsoleCommand(cmd.toLowerCase(),args.toArray(new String[args.size()]));
    } 
    
}
