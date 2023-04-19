package bgu.spl.net.srv;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T> {

    public Map<Integer, ConnectionHandler<T>> ClientList;
    private int currId = 1;
    private Object Lock = new Object();


    public ConnectionsImpl() {
        this.ClientList = new ConcurrentHashMap<>();
    }

    @Override
    public boolean send(int connectionId, T msg) {
        ConnectionHandler<T> handler = ClientList.get(connectionId);
        if (handler == null)
            return false;
        else synchronized (handler){
            handler.send(msg);
        }
        return true;
    }

    @Override
    public void broadcast(T msg) {
        for (ConnectionHandler c : ClientList.values()){
            synchronized (c) {
                c.send(msg);
            }
        }
    }

    @Override
    public void disconnect(int connectionId) {
        ConnectionHandler<T> handler = ClientList.get(connectionId);
        ClientList.remove(connectionId);
        try {
            handler.close();
        } catch (IOException e){}
    }

    public int connect(ConnectionHandler<T> handler) {
        synchronized (Lock) {
            ClientList.put(currId, handler);
            currId++;
            return currId - 1;
        }
    }

}
