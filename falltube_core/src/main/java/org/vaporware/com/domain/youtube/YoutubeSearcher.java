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
package org.vaporware.com.domain.youtube;

import org.vaporware.com.domain.search.SearchObject;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import org.vaporware.com.domain.exceptions.NoMorePagesException;
import org.vaporware.com.domain.exceptions.NoResultsException;

public class YoutubeSearcher {

    private final String APIKEY;
    private Gson g = new Gson();

    public YoutubeSearcher(String api) {
        this.APIKEY = api;
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

}
