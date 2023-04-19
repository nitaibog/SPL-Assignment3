package bgu.spl.net.impl.BGSProtocol;

import bgu.spl.net.impl.Messages.PM;
import bgu.spl.net.impl.Messages.Post;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataBase {
    private List<Client> users;
    private Map<Integer, Client> connectedUsers;
    private Map<String, LinkedList<PM>> PmMap;  // <key,value> = <sendToUserName, PM>
    private Map<String , LinkedList<Post>> PostMap; // <userName,Post>

    public DataBase() {
        this.users = new LinkedList<>();
        this.connectedUsers = new ConcurrentHashMap<>();
        this.PmMap = new ConcurrentHashMap<>();
        this.PostMap = new ConcurrentHashMap<>();
    }

    public boolean AddUser(String username, String password, String birthday, int connectionId) {
        boolean exist = false;
        for (Client c : users) {
            if (c.getUsername().equals(username))
                exist = true;
        }
        if (!exist) {
            synchronized (username) {
                users.add(new Client(username, password, birthday,connectionId));
                PmMap.put(username,new LinkedList<PM>());
                PostMap.put(username, new LinkedList<Post>());
            }
            return true;
        }
        return false;
    }

    public boolean Login(String username, String pass, int connectionId) {
        Client c = null;
        for (Client temp : users) {
            if (temp.getUsername().equals(username))
                c = temp;
        }
        if (c != null) {
            if (c.getPassword().equals(pass))
                if (!connectedUsers.containsValue(c)) {
                    connectedUsers.put(connectionId, c);
                    return true;
                }
        }
        return false;
    }

    public boolean Logout(int connectionId) {
        Client c = isConnected(connectionId);
        if (c != null) {
            connectedUsers.remove(connectionId);
            return true;
        }
        return false;
    }

    public boolean Follow(Client c, int task, String usernameToFollow) {
        Client other = getRegisteredClientByUserName(usernameToFollow);
        if (other == null) {
            return false;
        }
        if (c.isBlocked(usernameToFollow)){
            return false;
        }
        boolean temp = c.getFollowing().contains(other);
        if (task == 0) {
            if (temp)
                return false;
            else {
                c.getFollowing().add(other);
                other.addFollower(c);
                return true;
            }
        } else {
            if (!temp)
                return false;
            else {
                c.getFollowing().remove(other);
                other.getFollowers().remove(c);
                return true;
            }
        }
    }

    public boolean isRegistered(String userName) {
        boolean exist = false;
        for (Client c : users) {
            if (c.getUsername().equals(userName))
                exist = true;
        }
        return exist;
    }

    public Client getRegisteredClientByUserName(String userName){
        Client temp = null;
        for (Client c : users) {
            if (c.getUsername().equals(userName))
                temp = c;
        }
        return temp;
    }



    public Client isConnected(int connectionId){
        Client c = null;
        c = connectedUsers.get(connectionId);
        if (c != null) {
            return c;
        }
        return null;
    }

    public Client isConnected(String userName){
        Client c = null;
        Collection<Client> connected = connectedUsers.values();
        for (Client temp : connected){
            if (temp.getUsername().equals(userName))
                c = temp;
        }
        return c;
    }
    public void addToPmMap(String userName, PM pm){
        if (PmMap.get(userName) == null){
            PmMap.put(userName, new LinkedList<PM>());
        }
        PmMap.get(userName).add(pm);  /// should it be syncronzied?????
    }

    public void addToPostMap(String userName,Post post){
        if (PostMap.get(userName) == null){
            PostMap.put(userName, new LinkedList<Post>());
        }
        PostMap.get(userName).add(post); /// should it be syncronzied?????
    }

    public List<Client> getUsers() {
        return users;
    }

    public Map<Integer, Client> getConnectedUsers() {
        return connectedUsers;
    }

    public Map<String, LinkedList<PM>> getPmMap() {
        return PmMap;
    }

    public Map<String, LinkedList<Post>> getPostMap() {
        return PostMap;
    }
}
