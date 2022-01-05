package com.example.chat_de;

public class ItemModel {
    String name;
    String imageURL;
    int generation;
    ItemModel(String name, String imageURL, int generation){
        this.generation = generation;
        this.name = name;
        this.imageURL = imageURL;
    }
}
