package bgu.spl.net.impl.Messages;

import bgu.spl.net.impl.BGSProtocol.Client;
import bgu.spl.net.impl.BGSProtocol.DataBase;
import bgu.spl.net.srv.Connections;

import java.util.Collection;
import java.util.LinkedList;

public class LoggedInStates implements Message{

    private final int OpCode = 7;


    @Override
    public byte[] encode() {
        return null;
    }

    @Override
    public boolean process(int connectionId, Connections connections, DataBase dataBase) {
        Client c = dataBase.isConnected(connectionId);
        if (c == null || !dataBase.isRegistered(c.getUsername())){
            connections.send(connectionId, new Error((short)7));
            return false;
        }
        Collection<Client> users = dataBase.getConnectedUsers().values();
        Collection<Client> relUsers = new LinkedList<>();
        for (Client client: users){
            if (!c.isBlocked(client.getUsername()) && !c.getUsername().equals(client.getUsername())){
                relUsers.add(client);
            }
        }
        int i = 0;
        String[] usersData = new String[relUsers.size()];
        for (Client curr : relUsers){

                String s = curr.getClientAge() + " " + String.valueOf(dataBase.getPostMap().get(curr.getUsername()).size())
                        + " " + String.valueOf(curr.getFollowers().size()) + " " + String.valueOf(curr.getFollowing().size());
                usersData[i] = s;
                i++;

        }
        connections.send(connectionId,new ACK((short)7,usersData));
        return true;
    }
}
