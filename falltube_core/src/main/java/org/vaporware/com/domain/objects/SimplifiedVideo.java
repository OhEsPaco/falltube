/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaporware.com.domain.objects;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author pacog
 */
public class SimplifiedVideo {

    private String id;
    private String etag;
    private String publishedAt;
    private String channelId;
    private String title;
    private String description;
    private String channelTitle;
    private ArrayList<String> tags = new ArrayList<String>();
    private String categoryId;
    private String defaultAudioLanguage;
    private String duration;
    private String dimension;
    private String definition;
    private boolean caption;
    private boolean licensedContent;
    private String projection;
    private long viewCount;
    private long likeCount;
    private long dislikeCount;
    private long commentCount;

    public SimplifiedVideo() {

    }

    public SimplifiedVideo(String id, String etag, String publishedAt, String channelId, String title, String description, String channelTitle, String categoryId, String defaultAudioLanguage, String duration, String dimension, String definition, Boolean caption, boolean licensedContent, String projection, long viewCount, long likeCount, long dislikeCount, long commentCount) {
        this.id = id;
        this.etag = etag;
        this.publishedAt = publishedAt;
        this.channelId = channelId;
        this.title = title;
        this.description = description;
        this.channelTitle = channelTitle;
        this.categoryId = categoryId;
        this.defaultAudioLanguage = defaultAudioLanguage;
        this.duration = duration;
        this.dimension = dimension;
        this.definition = definition;
        this.caption = caption;
        this.licensedContent = licensedContent;
        this.projection = projection;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.commentCount = commentCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getDefaultAudioLanguage() {
        return defaultAudioLanguage;
    }

    public void setDefaultAudioLanguage(String defaultAudioLanguage) {
        this.defaultAudioLanguage = defaultAudioLanguage;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public Boolean isCaption() {
        return caption;
    }

    public void setCaption(Boolean caption) {
        this.caption = caption;
    }

    public boolean isLicensedContent() {
        return licensedContent;
    }

    public void setLicensedContent(boolean licensedContent) {
        this.licensedContent = licensedContent;
    }

    public String getProjection() {
        return projection;
    }

    public void setProjection(String projection) {
        this.projection = projection;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public long getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(long dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

    @Override
    public String toString() {

        return "YvdSimplified{" + "id=" + id + ", etag=" + etag + ", publishedAt=" + publishedAt + ", channelId=" + channelId + ", title=" + title + ", channelTitle=" + channelTitle + ", categoryId=" + categoryId + ", defaultAudioLanguage=" + defaultAudioLanguage + ", duration=" + duration + ", dimension=" + dimension + ", definition=" + definition + ", caption=" + caption + ", licensedContent=" + licensedContent + ", projection=" + projection + ", viewCount=" + viewCount + ", likeCount=" + likeCount + ", dislikeCount=" + dislikeCount + ", commentCount=" + commentCount + '}';
    }

}
