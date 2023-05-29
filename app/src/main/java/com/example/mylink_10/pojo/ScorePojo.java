package com.example.mylink_10.pojo;

public class ScorePojo {
    private String Username;
    private String Date;
    private String Score;

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getScore() {
        return Score;
    }

    public void setScore(String score) {
        Score = score;
    }

    @Override
    public String toString() {
        return "ScorePojo{" +
                "Username='" + Username + '\'' +
                ", Date='" + Date + '\'' +
                ", Score='" + Score + '\'' +
                '}';
    }
}
