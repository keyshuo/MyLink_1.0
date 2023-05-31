package com.example.mylink_10.gameRelated;

import java.util.List;

public class TempNode {
    private List<Integer> images;
    private int id;

    public TempNode(int id, List<Integer> images) {
        this.images = images;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public List<Integer> getImages() {
        return images;
    }
}