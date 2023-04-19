package bgu.spl.net.impl.Messages;

import bgu.spl.net.impl.BGSProtocol.Client;
import bgu.spl.net.impl.BGSProtocol.DataBase;
import bgu.spl.net.srv.Connections;

public class Login implements Message{

    private final int opCode = 2;
    private String username;
    private String password;
    private int captcha;

    public Login(String username, String password, byte captcha){
        this.captcha = captcha - '0';
        this.username = username;
        this.password = password;
    }

    @Override
    public byte[] encode() {
        return null;
    }

    public boolean process(int connectionId, Connections connections, DataBase dataBase){
        if (captcha == 1){
            boolean logged = dataBase.Login(username,password,connectionId);
            if (logged){
                connections.send(connectionId,new ACK((short)2,""));
                Client c = dataBase.getRegisteredClientByUserName(username);
                while (!c.getNotifications().isEmpty()){
                    connections.send(connectionId,c.getNotifications().poll());
                }
                return true;
            }
            else {
                connections.send(connectionId, new Error((short) 2));
                return false;
            }
        }
        else {
            connections.send(connectionId, new Error((short) 2));
            return false;
        }
    }

}
