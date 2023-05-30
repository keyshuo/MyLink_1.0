package com.example.mylink_10.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommunityMSG implements Serializable {
    private String username;
    private String time;
    private String content;

    private static List<String> nameArray = Arrays.asList(new String[]{});
    private static List<String> timeArray = Arrays.asList(new String[]{});
    private static List<String> contentArray = Arrays.asList(new String[]{});

    private static List<String> rm_nameArray = Arrays.asList(new String[]{});
    private static List<String> rm_timeArray = Arrays.asList(new String[]{});
    private static List<String> rm_contentArray = Arrays.asList(new String[]{});

    private static List<String> mypost_timeArray = Arrays.asList(new String[]{});
    private static List<String> mypost_contentArray = Arrays.asList(new String[]{});

    public String getName() {
        return username;
    }
    public String getTime() {
        return time;
    }
    public String getMsg() {
        return content;
    }
    public static void setNameArray(ArrayList<String> NameArray){
        nameArray = NameArray;
        //Log.d("CommunityMSG",nameArray.toString());
    }
    public static void setTimeArray(ArrayList<String> TimeArray){
        timeArray = TimeArray;
        //Log.d("CommunityMSG",timeArray.toString());
    }
    public static void setContentArray(ArrayList<String> ContentArray){
        contentArray = ContentArray;
        //Log.d("CommunityMSG",contentArray.toString());
    }
    public static void setRm_nameArray(List<String> rm_nameArray) {
        CommunityMSG.rm_nameArray = rm_nameArray;
    }

    public static void setRm_timeArray(List<String> rm_timeArray) {
        CommunityMSG.rm_timeArray = rm_timeArray;
    }

    public static void setRm_contentArray(List<String> rm_contentArray) {
        CommunityMSG.rm_contentArray = rm_contentArray;
    }
    public static void setMypost_timeArray(List<String> mypost_timeArray) {
        CommunityMSG.mypost_timeArray = mypost_timeArray;
    }
    public static void setMypost_contentArray(List<String> mypost_contentArray) {
        CommunityMSG.mypost_contentArray = mypost_contentArray;
    }
    public CommunityMSG(String username, String content, String time) {
        this.username = username;
        this.content = content;
        this.time = time;
    }
    public static List<CommunityMSG> getDefaultList()
    {
        List<CommunityMSG> msg_list=new ArrayList<>();
        for (int i = 0; i < nameArray.size(); i++) {
            msg_list.add(new CommunityMSG(nameArray.get(i), contentArray.get(i), timeArray.get(i)));
        }
        return msg_list;
    }

    public static List<CommunityMSG> getRMDefaultList()
    {
        List<CommunityMSG> msg_list=new ArrayList<>();
        for (int i = 0; i < rm_nameArray.size(); i++) {
            msg_list.add(new CommunityMSG(rm_nameArray.get(i), rm_contentArray.get(i), rm_timeArray.get(i)));
        }
        return msg_list;
    }
    public static List<CommunityMSG> getMyDefaultList(String username)
    {
        List<CommunityMSG> msg_list=new ArrayList<>();
        for (int i = 0; i < mypost_contentArray.size(); i++) {
            msg_list.add(new CommunityMSG(username,mypost_contentArray.get(i), mypost_timeArray.get(i)));
        }
        return msg_list;
    }
}
