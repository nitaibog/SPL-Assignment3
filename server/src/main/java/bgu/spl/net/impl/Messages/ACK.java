package bgu.spl.net.impl.Messages;

import bgu.spl.net.impl.BGSProtocol.DataBase;
import bgu.spl.net.srv.Connections;

import java.nio.charset.StandardCharsets;

public class ACK implements Message{

    private final int opCode = 10;
    private short messageOpCode;
    private Object optional;
    private String[] optional1;
    private String optional2;

    public ACK(short messageOpCode, String[] optional1) {
        this.messageOpCode = messageOpCode;
        this.optional1 = optional1;
    }

    public ACK(short messageOpCode, String optional2) {
        this.messageOpCode = messageOpCode;
        this.optional2 = optional2;
    }

    @Override
    public byte[] encode() {
        byte[] AckOp = shortToBytes((short) 10);
        byte[] msgOp = shortToBytes((short) messageOpCode);
        byte[] res;
        switch (messageOpCode){
            case 4:
                byte[] name = optional2.getBytes(StandardCharsets.UTF_8);
                res = new byte[5 + name.length];
                System.arraycopy(AckOp,0,res,0,2);
                System.arraycopy(msgOp,0,res,2,2);
                System.arraycopy(name,0,res,4,name.length);
                res[res.length-1] = '\0';
                return res;
            case 7:
            case 8:
                byte[] stats = new byte[8*(optional1).length];
                int currIndex = 0;
                for (int i = 0; i < optional1.length ; i++) {
                    String[] s = optional1[i].split(" ");
                    for (int j = 0; j < 4; j++){
                        byte[] temp = shortToBytes(Short.valueOf(s[j]));
                        if (temp.length == 1){
                            stats[currIndex+1] = temp[0];
                        } else {
                            System.arraycopy(temp, 0, stats, currIndex, 2);
                        }
                        currIndex = currIndex +2;
                    }
                }
                res = new byte[5 + stats.length];
                System.arraycopy(AckOp,0,res,0,2);
                System.arraycopy(msgOp,0,res,2,2);
                System.arraycopy(stats,0,res,4,stats.length);
                res[res.length -1] = ';';
                return res;
        }
        res = new byte[4];
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

    public short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }
    @Override
    public boolean process(int connectionId, Connections connections, DataBase dataBase) {
        return false;
    }
}
