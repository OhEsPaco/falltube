/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaporware.com.domain;

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
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import org.vaporware.com.domain.video.YoutubeVideoData;

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

    public YvdSimplified getVideoDataFromIDSimplified(String videoId) throws IOException {
        return YvdSimplifier.simplify(getVideoDataFromID(videoId));
    }

    public ArrayList<Comment> readComments(String videoId, long maxNumberOfResults) throws IOException {

        CommentThreadListResponse videoCommentsListResponse = youtube.commentThreads()
                .list("snippet").setKey(APIKEY).setVideoId(videoId).setMaxResults(maxNumberOfResults).setTextFormat("plainText").execute();

        List<CommentThread> videoComments = videoCommentsListResponse.getItems();
        ArrayList<Comment> comments = new ArrayList<Comment>();
        for (CommentThread videoComment : videoComments) {
            CommentSnippet snippet = videoComment.getSnippet().getTopLevelComment().getSnippet();
            Comment comment = new Comment();
            comment.setVideoId(videoId);
            comment.setAuthorName(snippet.getAuthorDisplayName());
            comment.setAuthorUrl(snippet.getAuthorChannelUrl());
            comment.setComment(snippet.getTextDisplay());
            comments.add(comment);
        }

        return comments;
    }

    public void queryToSql(String queryTerm, long maxNumberOfVideos, long maxNumberOfComments) throws IOException {
        YouTube.Search.List search = youtube.search().list("id,snippet");

        search.setKey(APIKEY);
        search.setQ(queryTerm);
        search.setType("video");

        search.setFields("items(id/kind,id/videoId)");
        search.setMaxResults(maxNumberOfVideos);
        SearchListResponse searchResponse = search.execute();

        List<SearchResult> searchResultList = searchResponse.getItems();

        if (searchResultList != null) {
            Iterator<SearchResult> iteratorSearchResults = searchResultList.iterator();
            while (iteratorSearchResults.hasNext()) {

                SearchResult singleVideo = iteratorSearchResults.next();
                ResourceId rId = singleVideo.getId();
                
                if (rId.getKind().equals("youtube#video")) {
                   
                    //COMPROBAR PRIMERO QUE EL VIDEO NO ESTE EN LA BASE DE DATOS
                    YvdSimplified video=getVideoDataFromIDSimplified(rId.getVideoId());
                    ArrayList<Comment> comments=readComments(rId.getVideoId(),maxNumberOfComments);
                    //Guardar el video en una tabla y los comentarios en otra

                   
                }
            }
        }
    }
}
