/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaporware.com.domain.youtube;

import java.util.HashMap;

public class Category {

    private String etag;

    private Items[] items;

    private String kind;

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public Items[] getItems() {
        return items;
    }

    public void setItems(Items[] items) {
        this.items = items;
    }

    public String getKind() {
        return kind;
    }

    public HashMap<Integer, String> getCategories() {
        HashMap<Integer, String> categories = new HashMap<Integer, String>();
        for (Items it : items) {
            categories.put(Integer.parseInt(it.getId()), it.getSnippet().getTitle());

        }
        return categories;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    @Override
    public String toString() {
        return "ClassPojo [etag = " + etag + ", items = " + items + ", kind = " + kind + "]";
    }

    private class Items {

        private String id;

        private String etag;

        private Snippet snippet;

        private String kind;

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

        public Snippet getSnippet() {
            return snippet;
        }

        public void setSnippet(Snippet snippet) {
            this.snippet = snippet;
        }

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        @Override
        public String toString() {
            return "ClassPojo [id = " + id + ", etag = " + etag + ", snippet = " + snippet + ", kind = " + kind + "]";
        }
    }

    private class Snippet {

        private String assignable;

        private String title;

        private String channelId;

        public String getAssignable() {
            return assignable;
        }

        public void setAssignable(String assignable) {
            this.assignable = assignable;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        @Override
        public String toString() {
            return "ClassPojo [assignable = " + assignable + ", title = " + title + ", channelId = " + channelId + "]";
        }
    }
}
