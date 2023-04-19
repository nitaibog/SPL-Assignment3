package bgu.spl.net.impl.Messages;

import bgu.spl.net.impl.BGSProtocol.DataBase;
import bgu.spl.net.srv.Connections;

public class Error implements Message {

    private final int opCode = 11;
    private int messageOpCode;

    public Error(int messageOpCode){
        this.messageOpCode = messageOpCode;
    }

    @Override
    public byte[] encode() {
        byte[] AckOp = shortToBytes((short) 11);
        byte[] msgOp = shortToBytes((short) messageOpCode);
        byte[] res = new byte[4];
        System.arraycopy(AckOp,0,res,0,2);
        System.arraycopy(msgOp,0,res,2,2);
        return res;
    }
    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    @Override
    public boolean process(int connectionId, Connections connections, DataBase dataBase) {
        return false;
    }
}
