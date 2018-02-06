package com.digitalelimu.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Term {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("book_id")
    @Expose
    private String bookId;
    @SerializedName("term")
    @Expose
    private String term;
    @SerializedName("week")
    @Expose
    private String week;
    @SerializedName("lesson")
    @Expose
    private String lesson;
    @SerializedName("weeks")
    @Expose
    private String weeks;
    @SerializedName("lessons")
    @Expose
    private String lessons;
    @SerializedName("audio")
    @Expose
    private String audio;
    @SerializedName("video")
    @Expose
    private String video;

    /**
     * No args constructor for use in serialization
     *
     */
    public Term() {
    }

    /**
     *
     * @param id
     * @param lesson
     * @param term
     * @param description
     * @param name
     * @param bookId
     * @param week
     */
    public Term(Integer id, String name, String description, String bookId, String term, String week, String lesson, String weeks, String lessons, String audio, String video) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.bookId = bookId;
        this.term = term;
        this.week = week;
        this.lesson = lesson;
        this.weeks = weeks;
        this.lessons = lessons;
        this.audio = audio;
        this.video = video;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }
    public String getWeeks() {
        return weeks;
    }

    public void setweeks(String weeks) {
        this.weeks = weeks;
    }
    public String getLessons() {
        return lessons;
    }

    public void setLessons(String lessons) {
        this.lessons = lessons;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }
}
