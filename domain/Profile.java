package com.example.guiex1.domain;

public class Profile extends Entity<Long>{

    private String imageURL;
    private String description;


    Long Uid;


    public Profile(String imageURL, String description, Long Uid) {
        this.imageURL = imageURL;
        this.description = description;
        this.Uid = Uid;

    }

    public String getImageURL(){
        return imageURL;
    }
    public void setImageURL(String imageURL){
        this.imageURL = imageURL;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public Long getUid(){
        return Uid;
    }
    public void setUid(Long uid){
        this.Uid = uid;
    }
    @Override
    public String toString(){
        return Uid + " - " + imageURL+ " - " + description;
    }
}
