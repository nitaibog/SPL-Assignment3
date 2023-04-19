package bgu.spl.net.impl.Messages;

import bgu.spl.net.impl.BGSProtocol.Client;
import bgu.spl.net.impl.BGSProtocol.DataBase;
import bgu.spl.net.srv.Connections;

import java.util.List;

public class Stats implements Message{

    private final int OpCode = 8;
    private List<String> userNames;

    public Stats(List<String> userNames) {
        this.userNames = userNames;
    }

    @Override
    public byte[] encode() {
        return null;
    }

    @Override
    public boolean process(int connectionId, Connections connections, DataBase dataBase) {
        Client c = dataBase.isConnected(connectionId);
        if (c == null || !dataBase.isRegistered(c.getUsername())){
            connections.send(connectionId, new Error((short)8));
            return false;
        }
        int i = 0;
        String[] usersData = new String[userNames.size()];
        for (String name : userNames) {
            if (dataBase.isRegistered(name) && !c.isBlocked(name)) {
                    Client curr = dataBase.getRegisteredClientByUserName(name);
                    String s = curr.getClientAge() + " " + String.valueOf(dataBase.getPostMap().get(curr.getUsername()).size())
                            + " " + String.valueOf(curr.getFollowers().size()) + " " + String.valueOf(curr.getFollowing().size());
                    usersData[i] = s;
                    i++;
            } else {
                connections.send(connectionId,new Error((short)8));
                return false;
            }
        }
        connections.send(connectionId,new ACK((short)8,usersData));
        return true;
    }
}
