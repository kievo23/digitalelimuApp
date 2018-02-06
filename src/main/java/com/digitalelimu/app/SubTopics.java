package com.digitalelimu.app;

/**
 * Created by kev on 2/2/17.
 */

public class SubTopics{
    public String id;
    public String sub_topic;
    public String photo;
    public String description;
    public String lesson;

    public SubTopics(String id,String sub_topic,String photo,String description,String lesson){
        this.id = id;
        this.sub_topic = sub_topic;
        this.photo = photo;
        this.description = description;
        this.lesson = lesson;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return sub_topic;
    }

    public void setName(String name) {
        this.sub_topic = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }
}
