/*
MIT License

Copyright (c) 2018 Francisco Manuel Garcia Sanchez

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
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
