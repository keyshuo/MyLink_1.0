package com.example.mylink_10.pojo;

import java.util.Arrays;

public class RankingResult {
    private String code;
    private ScorePojo[] data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ScorePojo[] getData() {
        return data;
    }

    public void setData(ScorePojo[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RankingResult{" +
                "code='" + code + '\'' +
                ", data=" + Arrays.toString(data).replace("[","").replace("]","").replaceAll("ScorePojo","") +
                '}';
    }
}
