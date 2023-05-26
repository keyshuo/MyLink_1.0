package com.example.mylink_10.pojo;

public class User {
    private String account;
    private String username;
    private String password;
    public User() {
    }

    public User(String account, String username, String password) {
        this.account = account;
        this.username = username;
        this.password = password;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "account='" + account + '\'' +
                ", userName='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
