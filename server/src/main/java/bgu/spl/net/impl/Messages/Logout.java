package bgu.spl.net.impl.Messages;

import bgu.spl.net.impl.BGSProtocol.DataBase;
import bgu.spl.net.srv.Connections;

public class Logout implements Message{
    private final int opCode = 3;
    public Logout() {

    }

    @Override
    public byte[] encode() {
        return null;
    }

    @Override
    public boolean process(int connectionId, Connections connections, DataBase dataBase) {
        boolean loggedOut = dataBase.Logout(connectionId);
        if (loggedOut){
            connections.send(connectionId,new ACK((short)3,""));
            connections.disconnect(connectionId);
            return true;
        }
        else {
            connections.send(connectionId,new Error((short)3));
            return false;
        }
    }
}
