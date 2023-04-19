package bgu.spl.net.impl.BGSProtocol;

import bgu.spl.net.impl.Messages.Message;

import java.time.LocalDate;
import java.time.Period;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client {

    private String username;
    private String password;
    private String birthDay;
    private Queue<Message> notifications;
    private List<Client> following;
    private List<Client> followers;
    private int connectionId;
    private List<String> blockedUser;


    public Client(String username, String password, String birthDay, int connectionId) {
        this.username = username;
        this.password = password;
        this.birthDay = birthDay;
        this.notifications = new LinkedBlockingQueue<>();
        this.following = new LinkedList<>();
        this.followers = new LinkedList<>();
        this.connectionId = connectionId;
        this.blockedUser = new LinkedList<>();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public List<Client> getFollowing() {
        return following;
    }

    public void addBlockedUser(String username){
        blockedUser.add(username);
        for (Client b : followers){
            if (b.getUsername().equals(username))
                followers.remove(b);
        }

        for (Client b : following){
            if (b.getUsername().equals(username))
                following.remove(b);
        }
    }

    public boolean isBlocked(String username){
        for (String name : blockedUser){
            if (name.equals(username))
                return true;
        }
        return false;
    }


    public List<Client> getFollowers() {
        return followers;
    }

    public void addFollower(Client client){
        followers.add(client);
    }
    public void F(Client client){

    }

    public String getClientAge(){

        int day = Integer.parseInt(birthDay.substring(0,2));
        int month = Integer.parseInt(birthDay.substring(3,5));
        int year = Integer.parseInt(birthDay.substring(6));
        LocalDate date = LocalDate.of(year,month,day);
        Period period = Period.between(date, LocalDate.now());
        String years = String.valueOf(period.getYears());
        return years;

    }

    public Queue<Message> getNotifications() {
        return notifications;
    }

    public void AddNotifications(Message message) {
        notifications.add(message);
    }

    public int getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(int connectionId) {
        this.connectionId = connectionId;
    }
}