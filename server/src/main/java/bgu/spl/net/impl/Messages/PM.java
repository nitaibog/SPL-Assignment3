package bgu.spl.net.impl.Messages;

import bgu.spl.net.impl.BGSProtocol.Client;
import bgu.spl.net.impl.BGSProtocol.DataBase;
import bgu.spl.net.srv.Connections;

public class PM implements Message {

    private final int OpCode = 6;
    private String userNameToPM;
    private String content;
    private String[] filters = {"tramp", "war"};



    public PM(String userNameToPM, String content) {
        this.userNameToPM = userNameToPM;
        this.content = content;

    }

    @Override
    public byte[] encode() {
        return null;
    }

    @Override
    public boolean process(int connectionId, Connections connections, DataBase dataBase) {
        Client sender = dataBase.isConnected(connectionId);
        if (sender == null) {
            connections.send(connectionId, new Error((short) 6));
            return false;
        }
        else {
            if (dataBase.isRegistered(userNameToPM)){
                if (sender.isBlocked(userNameToPM)){
                    connections.send(connectionId,new Error((short)6));
                    return false;
                }
                for (int i = 0; i < filters.length; i++) {
                    content = content.replaceAll(filters[i], "<filtered>");
                }
                Client c = dataBase.isConnected(userNameToPM);
                if (c != null){
                    connections.send(c.getConnectionId(),new Notification(0,sender.getUsername(),content));
                } else {
                   c = dataBase.getRegisteredClientByUserName(userNameToPM);
                   c.AddNotifications(new Notification(0,sender.getUsername(),content));
                }
                dataBase.addToPmMap(userNameToPM, this);
                connections.send(connectionId, new ACK((short)6,""));
                return true;
            }
              connections.send(connectionId, new Error((short) 6));
              return false;
        }
    }


    public String getUserNameToPM() {
        return userNameToPM;
    }

    public String getContent() {
        return content;
    }

//    public String getDateAndTime() {
//        return timestamp.toString();
//    }
}
