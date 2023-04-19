package bgu.spl.net.srv;

import bgu.spl.net.impl.BGSProtocol.DataBase;
import bgu.spl.net.impl.Messages.Message;
import bgu.spl.net.impl.Messages.Logout;
public class BidiProtocol implements BidiMessagingProtocol<Message> {


    private boolean shouldTerminate = false;
    private String name;
    private int connectionId;
    private Connections connections;
    private DataBase dataBase;

    public BidiProtocol(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    @Override
    public void start(int connectionId, Connections connections) {
        this.connectionId = connectionId;
        this.connections = connections;

    }

    @Override
    public void process(Message message) {
        boolean res = message.process(connectionId,connections,dataBase);
        if (res && message instanceof Logout)
            shouldTerminate =true;

    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
