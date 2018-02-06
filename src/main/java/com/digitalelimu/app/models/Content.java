package com.digitalelimu.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Content {

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
    @SerializedName("details")
    @Expose
    private String details;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    /**
     * No args constructor for use in serialization
     *
     */
    public Content() {
    }

    /**
     *
     * @param updatedAt
     * @param id
     * @param lesson
     * @param details
     * @param term
     * @param createdAt
     * @param description
     * @param name
     * @param bookId
     * @param week
     */
    public Content(Integer id, String name, String description, String bookId, String term, String week, String lesson, String details, String createdAt, String updatedAt) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.bookId = bookId;
        this.term = term;
        this.week = week;
        this.lesson = lesson;
        this.details = details;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}