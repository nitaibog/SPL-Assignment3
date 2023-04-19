package bgu.spl.net.impl.Messages;

import bgu.spl.net.impl.BGSProtocol.Client;
import bgu.spl.net.impl.BGSProtocol.DataBase;
import bgu.spl.net.srv.Connections;

public class Block implements Message{

    private final int OpCode = 12;
    private String userNameToBlock;

    public Block(String userNameToBlock) {
        this.userNameToBlock = userNameToBlock;
    }

    @Override
    public byte[] encode() {
        return null;
    }

    @Override
    public boolean process(int connectionId, Connections connections, DataBase dataBase) {
        Client b = dataBase.getRegisteredClientByUserName(userNameToBlock);
        if (b == null){
            connections.send(connectionId, new Error((short) 12));
            return false;
        }
        Client c = dataBase.isConnected(connectionId);
        if (c == null || !dataBase.isRegistered(c.getUsername())) {
            connections.send(connectionId, new Error((short) 12));
            return false;
        }
        else if (c.isBlocked(userNameToBlock)){
            connections.send(connectionId, new Error((short) 12));
            return false;
        }
        b.addBlockedUser(c.getUsername());
        c.addBlockedUser(userNameToBlock);

        connections.send(connectionId,new ACK((short)12,""));
        return true;
    }
}
