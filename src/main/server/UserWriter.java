package main.server;

import java.util.*;
import java.io.*;

public class UserWriter{
    private String username;
    private String balance;

    public UserWriter(String username, String balance){
        this.username= username;
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public void saveUser(String database) throws IOException{
        UserDB.saveUserDB(database, username, this);
    }

    public void loadUser(String database) throws IOException{
        UserDB.loadUserDB(database, username, this);
    }

}