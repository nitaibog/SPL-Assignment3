package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.BGSProtocol.DataBase;
import bgu.spl.net.impl.BGSProtocol.MessageEncoderDecoderImpl;
import bgu.spl.net.impl.Messages.Message;
import bgu.spl.net.srv.BidiMessagingProtocol;
import bgu.spl.net.srv.ProtocolSupplier;
import bgu.spl.net.srv.Server;

import java.util.function.Supplier;

public class TPCMain {

    private static Server tpc;
    private static int port;
    private static Supplier<BidiMessagingProtocol<Message>> protocolFactory;
    private static Supplier<MessageEncoderDecoder<Message>> readerFactory;
    private static DataBase dataBase;

    public static void main(String[]args){
        port=Integer.parseInt(args[0]);
        dataBase=new DataBase();
        protocolFactory=new ProtocolSupplier(dataBase);
        readerFactory = new Supplier<MessageEncoderDecoder<Message>>() {
            @Override
            public MessageEncoderDecoder<Message> get() {
                return new MessageEncoderDecoderImpl();
            }
        };
        tpc=Server.threadPerClient(port,protocolFactory,readerFactory);
        tpc.serve();
    }
}

