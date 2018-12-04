/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaporware.com.domain.objects;

import java.util.ArrayList;

/**
 *
 * @author pacog
 */
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
