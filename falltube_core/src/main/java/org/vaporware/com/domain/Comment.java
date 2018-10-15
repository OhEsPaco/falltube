/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaporware.com.domain;

/**
 *
 * @author paco
 */
public class Comment {

    private String videoId;
    private String commentId;
    private String authorName;
    private String authorUrl;
    private String comment;

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getaAthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String toString() {
        return ("CommentId: "+commentId+"\nVideo ID: " + videoId + "\nAutor name: " + authorName + "\nAuthor url: " + authorUrl + "\n" + comment + "\n#####################################\n");
    }
}
