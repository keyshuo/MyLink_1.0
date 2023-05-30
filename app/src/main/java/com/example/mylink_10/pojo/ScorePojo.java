package com.example.mylink_10.pojo;

public class ScorePojo {
    private String Username;
    private String Date;
    private String Score;

    @Override
    public String toString() {
        return "用户名：" + Username +"-创建记录日期：" + Date +"-所用时间：" + Score;
    }
}
