/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaporware.com.domain;

import org.vaporware.com.domain.search.SearchObject;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.vaporware.com.domain.exceptions.NoMorePagesException;
import org.vaporware.com.domain.exceptions.NoResultsException;
import org.vaporware.com.domain.video.YoutubeVideoData;
import org.vaporware.com.persistence.DAOManager;

/**
 *
 * @author pacog
 */
public class YoutubeDataDownloader {

    /**
     * Global instance of the HTTP transport.
     */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private final String APIKEY;
    private Gson g = new Gson();
    private YouTube youtube;

    public YoutubeDataDownloader(String apiKey) {
        this.APIKEY = apiKey;

        youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName("youtube-cmdline-search-sample").build();

    }

    public YoutubeVideoData getVideoDataFromID(String videoId) throws MalformedURLException, IOException {
        String requestURL = "https://www.googleapis.com/youtube/v3/videos?key=" + APIKEY + "&part=contentDetails,statistics,snippet&id=" + videoId;
        URL request = new URL(requestURL);
        URLConnection connection = request.openConnection();
        connection.setDoOutput(true);

        Scanner scanner = new Scanner(request.openStream());
        String response = scanner.useDelimiter("\\Z").next();

        YoutubeVideoData video = g.fromJson(response, YoutubeVideoData.class);
        return video;
    }

    public SearchObject search(String query, long resultsPerSearch) throws MalformedURLException, IOException, NoResultsException {
        String requestURL = "https://www.googleapis.com/youtube/v3/search?key=" + APIKEY + "&part=snippet&maxResults=" + resultsPerSearch + "&order=viewCount&q=" + query + "&type=video";
        URL request = new URL(requestURL);
        URLConnection connection = request.openConnection();
        connection.setDoOutput(true);

        Scanner scanner = new Scanner(request.openStream());
        String response = scanner.useDelimiter("\\Z").next();
        SearchObject sea = g.fromJson(response, SearchObject.class);
        if (sea.getItems().isEmpty()) {
            throw new NoResultsException();
        }
        return sea;

    }

    public SearchObject search(String query, long resultsPerSearch, SearchObject searchLast) throws MalformedURLException, IOException, NoMorePagesException, NoResultsException {
        if (searchLast.getNextPageToken() == null) {
            throw new NoMorePagesException();
        }
        String requestURL = "https://www.googleapis.com/youtube/v3/search?key=" + APIKEY + "&part=snippet&maxResults=" + resultsPerSearch + "&order=viewCount&pageToken=" + searchLast.getNextPageToken() + "&q=" + query + "&type=video";
        URL request = new URL(requestURL);
        URLConnection connection = request.openConnection();
        connection.setDoOutput(true);

        Scanner scanner = new Scanner(request.openStream());
        String response = scanner.useDelimiter("\\Z").next();
        SearchObject sea = g.fromJson(response, SearchObject.class);
        if (sea.getItems().isEmpty()) {
            throw new NoResultsException();
        }
        return sea;
    }

    public String[] searchObjectToIDs(SearchObject sea) {
        String[] ids = new String[sea.getItems().size()];
        for (int i = 0; i < sea.getItems().size(); i++) {
            ids[i] = sea.getItems().get(i).getId().getVideoId();
        }
        return ids;
    }

    public YvdSimplified getVideoDataFromIDSimplified(String videoId) throws IOException {
        return YvdSimplifier.simplify(getVideoDataFromID(videoId));
    }

    public ArrayList<Comment> readComments(String videoId, long maxNumberOfResults) throws IOException {
        ArrayList<Comment> comments = new ArrayList<Comment>();
        try {
            CommentThreadListResponse videoCommentsListResponse = youtube.commentThreads()
                    .list("snippet").setKey(APIKEY).setVideoId(videoId).setMaxResults(maxNumberOfResults).setTextFormat("plainText").execute();

            List<CommentThread> videoComments = videoCommentsListResponse.getItems();

            for (CommentThread videoComment : videoComments) {
                CommentSnippet snippet = videoComment.getSnippet().getTopLevelComment().getSnippet();
                Comment comment = new Comment();
                comment.setCommentId(videoComment.getId());
                comment.setVideoId(videoId);
                comment.setAuthorName(snippet.getAuthorDisplayName());
                comment.setAuthorUrl(snippet.getAuthorChannelUrl());
                comment.setComment(snippet.getTextDisplay());
                comments.add(comment);
            }

        } catch (Exception e) {

        }

        return comments;
    }

    public void videoIdToSql(String videoId, long maxNumberOfComments) throws IOException, Exception {
        DAOManager dao = new DAOManager();
        if (dao.isVideoOnDatabase(videoId) == false) {
            YvdSimplified video = getVideoDataFromIDSimplified(videoId);
            ArrayList<Comment> comments = readComments(videoId, maxNumberOfComments);
            dao.videoToDatabase(video);
            dao.commentsToDatabase(comments);
        }
    }

}
