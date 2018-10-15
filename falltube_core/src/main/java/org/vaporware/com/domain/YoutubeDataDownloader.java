/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaporware.com.domain;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import org.vaporware.com.domain.video.Items;
import org.vaporware.com.domain.video.YoutubeVideoData;

/**
 *
 * @author pacog
 */
public class YoutubeDataDownloader {

    private final String APIKEY;
    private  Gson g = new Gson();

    public YoutubeDataDownloader(String apiKey) {
        this.APIKEY = apiKey;
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
}
