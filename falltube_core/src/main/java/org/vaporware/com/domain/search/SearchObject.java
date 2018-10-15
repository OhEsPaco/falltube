/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaporware.com.domain.search;

import java.util.ArrayList;
import java.util.Date;
import org.vaporware.com.domain.video.PageInfo;

/**
 *
 * @author paco
 */
public class SearchObject {

    private String kind;

    public String getKind() {
        return this.kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    private String etag;

    public String getEtag() {
        return this.etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    private String nextPageToken;

    public String getNextPageToken() {
        return this.nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    private String regionCode;

    public String getRegionCode() {
        return this.regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    private PageInfo pageInfo;

    public PageInfo getPageInfo() {
        return this.pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    private ArrayList<Item> items;

    public ArrayList<Item> getItems() {
        return this.items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    private class Default {

        private String url;

        public String getUrl() {
            return this.url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        private int width;

        public int getWidth() {
            return this.width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        private int height;

        public int getHeight() {
            return this.height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    private class Medium {

        private String url;

        public String getUrl() {
            return this.url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        private int width;

        public int getWidth() {
            return this.width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        private int height;

        public int getHeight() {
            return this.height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    private class High {

        private String url;

        public String getUrl() {
            return this.url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        private int width;

        public int getWidth() {
            return this.width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        private int height;

        public int getHeight() {
            return this.height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    private class Thumbnails {

        private Default default1;

        public Default getDefault() {
            return this.default1;
        }

        public void setDefault(Default default1
        ) {
            this.default1
                    = default1;
        }

        private Medium medium;

        public Medium getMedium() {
            return this.medium;
        }

        public void setMedium(Medium medium) {
            this.medium = medium;
        }

        private High high;

        public High getHigh() {
            return this.high;
        }

        public void setHigh(High high) {
            this.high = high;
        }
    }

    private class Snippet {

        private Date publishedAt;

        public Date getPublishedAt() {
            return this.publishedAt;
        }

        public void setPublishedAt(Date publishedAt) {
            this.publishedAt = publishedAt;
        }

        private String channelId;

        public String getChannelId() {
            return this.channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        private String title;

        public String getTitle() {
            return this.title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        private String description;

        public String getDescription() {
            return this.description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        private Thumbnails thumbnails;

        public Thumbnails getThumbnails() {
            return this.thumbnails;
        }

        public void setThumbnails(Thumbnails thumbnails) {
            this.thumbnails = thumbnails;
        }

        private String channelTitle;

        public String getChannelTitle() {
            return this.channelTitle;
        }

        public void setChannelTitle(String channelTitle) {
            this.channelTitle = channelTitle;
        }

        private String liveBroadcastContent;

        public String getLiveBroadcastContent() {
            return this.liveBroadcastContent;
        }

        public void setLiveBroadcastContent(String liveBroadcastContent) {
            this.liveBroadcastContent = liveBroadcastContent;
        }
    }

}
