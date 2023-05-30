package com.example.mylink_10.pojo;

import java.util.List;

public class RankingResult {
    private String code;
    private List<ScorePojo> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<ScorePojo> getData() {
        return data;
    }

    public void setData(List<ScorePojo> data) {
        this.data = data;
    }
}
