package bgu.spl.net.impl.Messages;

import bgu.spl.net.impl.BGSProtocol.DataBase;
import bgu.spl.net.srv.Connections;

import java.nio.charset.StandardCharsets;

public class Notification implements Message{

    private int OpCode = 9;
    private int notificationType;
    private String postingUser;
    private String content;
    private String dateAndTime;

    public Notification(int notificationType, String postingUser, String content) {
        this.notificationType = notificationType;
        this.postingUser = postingUser;
        this.content = content;
    }

    @Override
    public byte[] encode() {
        byte[] NotifiOp = shortToBytes((short) 9);
        byte[] postingUser = this.postingUser.getBytes(StandardCharsets.UTF_8);
        byte[] cont = this.content.getBytes(StandardCharsets.UTF_8);
        byte[] res = new byte[5+ postingUser.length + cont.length];
        System.arraycopy(NotifiOp,0,res,0,2);
        res[2] = Integer.valueOf(notificationType).byteValue();
        System.arraycopy(postingUser,0,res,3,postingUser.length);
        res[3+postingUser.length] = Integer.valueOf(0).byteValue();
        System.arraycopy(cont,0,res,3+postingUser.length+1,cont.length);
        res[res.length-1] = '\0';
        return res;
    }

    @Override
    public boolean process(int connectionId, Connections connections, DataBase dataBase) {
        return false;
    }

    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }
}
