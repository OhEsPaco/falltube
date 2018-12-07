package org.vaporware.com.domain.youtube;

import org.vaporware.com.domain.utilities.VideoSimplifier;
import org.vaporware.com.domain.objects.SimplifiedVideo;
import org.vaporware.com.domain.objects.Comment;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vaporware.com.domain.exceptions.AlreadyExistsException;
import org.vaporware.com.domain.exceptions.ImpossibleToCreateTable;
import org.vaporware.com.domain.video.YoutubeVideoData;
import org.vaporware.com.persistence.SQLManager;

public class YoutubeDownloader {

    /*
MIT License

Copyright (c) 2018 OhEsPaco

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
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    /**
     * Global instance of the HTTP transport.
     */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private SQLManager sqlmanager;
    private YouTube youtube;
    private final String APIKEY;
    private Gson g = new Gson();
    private HashMap<Integer, String> categories = new HashMap<Integer, String>();
    private String regionCode;

    public YoutubeDownloader(String apiKey, String host, int port, String database, String user, String password, String regionCode) {
        this.APIKEY = apiKey;
        this.sqlmanager = new SQLManager(host, port, database, user, password);
        youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName("youtube-cmdline-search-sample").build();
        this.regionCode = regionCode;
        try {
            setCategories();
        } catch (IOException ex) {
            System.out.println("Error downloading categories.");
        }
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public SimplifiedVideo getVideoDataFromIDSimplified(String videoId) throws IOException {
        return VideoSimplifier.simplify(getVideoDataFromID(videoId));
    }

    public void setCategories() throws MalformedURLException, IOException {
        String requestURL = "https://www.googleapis.com/youtube/v3/videoCategories?part=snippet&regionCode=" + regionCode + "&key=" + APIKEY;
        URL request = new URL(requestURL);
        URLConnection connection = request.openConnection();
        connection.setDoOutput(true);

        Scanner scanner = new Scanner(request.openStream());
        String response = scanner.useDelimiter("\\Z").next();

        Category cats = g.fromJson(response, Category.class);

        categories = cats.getCategories();
    }

    public static boolean validKey(String apik) {
        String requestURL = "https://www.googleapis.com/youtube/v3/videoCategories?part=snippet&regionCode=ES&key=" + apik;
        try {
            URL request = new URL(requestURL);
            URLConnection connection = request.openConnection();
            connection.setDoOutput(true);
            Scanner scanner = new Scanner(request.openStream());
            String response = scanner.useDelimiter("\\Z").next();
        } catch (MalformedURLException ex) {

        } catch (IOException ex) {
            return false;
        }
        return true;

    }

    public HashMap<Integer, String> getCategories() {
        return categories;
    }

    public String getCategory(int id) {
        return categories.get(id);
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

    public void videoIdToSql(String videoId) throws IOException, SQLException, AlreadyExistsException {
        if (sqlmanager.isVideoOnDatabase(videoId) == false) {
            SimplifiedVideo video = getVideoDataFromIDSimplified(videoId);
            try {
                video.setCategoryId(getCategory(Integer.parseInt(video.getCategoryId())));
            } catch (Exception e) {
                video.setCategoryId("ERROR IN CATEGORY");
            }
            sqlmanager.insertVideo(video);

        }
    }

    public boolean isTableOnDatabase() {
        return sqlmanager.tableExists();
    }

    public void createTable() throws ImpossibleToCreateTable {
        sqlmanager.createTable();
    }

    public ArrayList<Comment> readComments(String videoId, long maxNumberOfResults) throws IOException {
        ArrayList<Comment> comments = new ArrayList<Comment>();

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

        return comments;
    }

}
