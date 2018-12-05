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
package org.vaporware.com.domain.objects;

import java.util.ArrayList;


public class PropertiesObjDownloader {

    private String name;
    private ArrayList<String> apiKeys = new ArrayList<String>();
    private ArrayList<String> searchers = new ArrayList<String>();
    private String host;
    private int port;
    private String database;
    private String user;
    private String password;
    private long numberOfComments;
    private String regionCode;

    public PropertiesObjDownloader(String name, String host, int port, String database, String user, String password, long numberOfComments, String regionCode) {
        this.name = name;

        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
        this.numberOfComments = numberOfComments;
        this.regionCode = regionCode;

    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNumberOfComments() {
        return numberOfComments;
    }

    public void setNumberOfComments(long numberOfComments) {
        this.numberOfComments = numberOfComments;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void addApiKey(String apiKey) {
        apiKeys.add(apiKey);
    }

    public void addSearcher(String searcher) {
        searchers.add(searcher);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<String> getApiKeys() {
        return apiKeys;
    }

    public void setApiKeys(ArrayList<String> apiKeys) {
        this.apiKeys = apiKeys;
    }

    @Override
    public String toString() {
        return "PropertiesObjDownloader{" + "name=" + name + ", apiKeys=" + apiKeys.size() + ", host=" + host + ", port=" + port + ", database=" + database + ", user=" + user + ", password=" + password + ", numberOfComments=" + numberOfComments + ", regionCode=" + regionCode + '}';
    }

    public ArrayList<String> getSearchers() {
        return searchers;
    }

}
