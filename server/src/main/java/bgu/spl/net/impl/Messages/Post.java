package bgu.spl.net.impl.Messages;

import bgu.spl.net.impl.BGSProtocol.Client;
import bgu.spl.net.impl.BGSProtocol.DataBase;
import bgu.spl.net.srv.Connections;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

public class Post implements Message {

    private final int OpCode = 5;
    private String content;
    private Client c;
    private Timestamp timestamp;

    public Post(String content) {
        this.content = content;
        this.timestamp = Timestamp.from(Instant.now());
    }

    @Override
    public byte[] encode() {
        return null;
    }

    @Override
    public boolean process(int connectionId, Connections connections, DataBase dataBase) {
        c = dataBase.isConnected(connectionId);
        if (c == null){
            connections.send(connectionId, new Error((short)5));
            return false;
        }
        String[] s = content.split(" ");
        List<Client> usersToSend = new LinkedList<>();
        for (int i = 0; i < s.length; i++) {
            if (s[i].charAt(0) == '@'){
                String userName = s[i].substring(1);
                Client h = dataBase.getRegisteredClientByUserName(userName);
                if (h != null && !c.isBlocked(userName))
                    usersToSend.add(h);
            }
        }
        List<Client> followers = c.getFollowers();
        usersToSend.addAll(followers);
        for (Client temp : usersToSend){
            int currId = temp.getConnectionId();
            if (dataBase.isConnected(currId) != null){
                connections.send(currId, new Notification(1,c.getUsername(),content));
            } else {
                temp.AddNotifications(new Notification(1,c.getUsername(),content));
            }
        }
        connections.send(connectionId, new ACK((short)5,""));
        dataBase.addToPostMap(c.getUsername(),this);
        return true;
    }
}
