package bgu.spl.net.impl.Messages;

import bgu.spl.net.impl.BGSProtocol.DataBase;
import bgu.spl.net.srv.Connections;

public interface Message {

    public byte[] encode();

    public String toString();

    public boolean process(int connectionId, Connections connections, DataBase dataBase);
}
