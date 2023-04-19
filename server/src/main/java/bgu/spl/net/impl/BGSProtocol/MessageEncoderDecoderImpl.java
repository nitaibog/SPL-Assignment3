package bgu.spl.net.impl.BGSProtocol;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.Messages.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Message> {

    private byte[] bytes = new byte[1 << 10];
    private int len = 0;

    public MessageEncoderDecoderImpl() {
    }

    @Override
    public Message decodeNextByte(byte nextByte) {
        if (nextByte == ';') {
            return createMsg();
        } else pushNextByte(nextByte);
        return null;
//        } else if (len == 1){
//            pushNextByte(nextByte);
//            short op = bytesToShort(bytes);
//        }

    }

    @Override
    public byte[] encode(Message message) {
        return message.encode();
    }

    public Message createMsg(){
         short op = bytesToShort(bytes);
         switch (op){
             case 1:
                 return register();
             case 2:
                 return login();
             case 3:
                 return logout();
             case 4:
                 return follow();
             case 5:
                 return post();
             case 6:
                 return pm();
             case 7:
                 return logStat();
             case 8:
                 return stats();
             case 12:
                 return block();
         }
           return null;

         }

    public void pushNextByte(byte nextByte){
        if (len >= bytes.length){
            bytes = Arrays.copyOf(bytes, bytes.length*2);
        }
        bytes[len] = nextByte;
        len++;
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

    public Register register(){
        String userName;
        String password;
        String birthDay;
        String[] s = new String[3];
        int sIndex = 0;
        int index = 2;
        for (int i = 2; i < bytes.length && sIndex < 3; i++) {
            if (bytes[i] == '\0'){
                s[sIndex] = new String(bytes,index,i-index, StandardCharsets.UTF_8);
                index = i+1;
                sIndex++;
            }
        }
        userName = s[0];
        password = s[1];
        birthDay = s[2];
        len = 0;
        return new Register(userName,password,birthDay);
    }

    public Login login(){
        String userName;
        String password;
        byte captcha;
        String[] s = new String[2];
        int sIndex = 0;
        int index = 2;
        for (int i = 2; i < bytes.length & sIndex <= 1 ; i++) {
            if (bytes[i] == '\0') {
                s[sIndex] = new String(bytes, index, i-index, StandardCharsets.UTF_8);
                index = i+1;
                sIndex++;
            }
        }
        userName = s[0];
        password = s[1];
        captcha = bytes[index];
        len = 0;
        return new Login(userName,password,captcha);
    }

    public Logout logout(){
        return new Logout();
    }

    public Follow follow(){
        byte task = bytes[2];
        boolean found = false;
        int index = 0;
        for (int i = 3; i < bytes.length & !found ; i++) {
            if (bytes[i] == '\0'){
                index = i;
                found = true;
            }
        }
        String s = new String(bytes,3,len-3,StandardCharsets.UTF_8);
        len = 0;
        return new Follow(s,task);
    }

    public Post post(){
        String content = new String(bytes,2,len-2,StandardCharsets.UTF_8);
        len = 0;
        return new Post(content);
    }

    public PM pm(){
        String userName;
        String content;
        String dateAndTime;
        String[] s = new String[2];
        int sIndex = 0;
        int index = 2;
        s[1] = "";
        for (int i = 2; i < len; i++) {
            if (bytes[i] == '\0'){
                String temp = new String(bytes,index,i-index, StandardCharsets.UTF_8);
                index = i+1;
                if (sIndex == 0) {
                    s[sIndex] = temp;
                    sIndex++;
                } else {
                    s[1] += temp+ " ";
                }
            }

        }
        userName = s[0];
        content = s[1];
        len = 0;
        return new PM(userName,content);
    }

    public LoggedInStates logStat(){
        len = 0;
        return new LoggedInStates();
    }

    public Stats stats(){
        String s = new String(bytes,2,len-2,StandardCharsets.UTF_8);
        String[] names = s.split(" ");
        List<String> result = new LinkedList<>();
        for (int i = 0; i < names.length; i++) {
            result.add(names[i]);
        }
        len = 0;
        return new Stats(result);
    }

    public Block block() {
        String s = new String(bytes,2,len-2,StandardCharsets.UTF_8);
        len = 0;
        return new Block(s);
    }










}
