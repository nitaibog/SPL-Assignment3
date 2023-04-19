package bgu.spl.net.impl.Messages;

import bgu.spl.net.impl.BGSProtocol.Client;
import bgu.spl.net.impl.BGSProtocol.DataBase;
import bgu.spl.net.srv.Connections;

public class Follow implements Message {

    private int OpCode = 4;
    private String userNameToFollow;
    private int FollowUnFollow;

    public Follow(String userName, byte followUnFollow) {
        this.userNameToFollow = userName;
        this.FollowUnFollow = followUnFollow -'0';
    }

    @Override
    public byte[] encode() {
        return null;
    }

    @Override
    public boolean process(int connectionId, Connections connections, DataBase dataBase) {
        Client c = dataBase.isConnected(connectionId);  // who is connected??
        if (c == null)
            return false;
        else {
           boolean done = dataBase.Follow(c,FollowUnFollow, userNameToFollow);
           if (done){
               connections.send(connectionId ,new ACK((short)4, userNameToFollow));
               return true;
           } else {
               connections.send(connectionId, new Error((short)4));
               return false;
           }
        }
    }


}
