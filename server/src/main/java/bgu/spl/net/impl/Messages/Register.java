package bgu.spl.net.impl.Messages;


import bgu.spl.net.impl.BGSProtocol.DataBase;
import bgu.spl.net.srv.Connections;

public class Register implements Message{

    private final int opCode = 1;
    private String username;
    private String password;
    private String birthday;

    public Register(String userName, String password, String birthday) {
        this.username = userName;
        this.password = password;
        this.birthday = birthday;
    }

    @Override
    public byte[] encode() {
        return null;
    }

    public boolean process(int connectionId, Connections connections, DataBase dataBase){

        boolean added = dataBase.AddUser(username, password, birthday,connectionId);
        if (added){
            connections.send(connectionId, new ACK((short)1 ,""));
            return true;
        }
        else {
            connections.send(connectionId, new Error((short)1));
            return false;
        }
    }




}
