package bgu.spl.net.srv;

import bgu.spl.net.impl.BGSProtocol.DataBase;
import bgu.spl.net.impl.Messages.Message;

import java.util.function.Supplier;

public class ProtocolSupplier implements Supplier<BidiMessagingProtocol<Message>> {

    private DataBase dataBase;

    public ProtocolSupplier(DataBase inventory){
        this.dataBase=inventory;
    }

    @Override
    public BidiProtocol get() {
        return new BidiProtocol(dataBase);
    }
}
