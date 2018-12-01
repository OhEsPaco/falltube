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
    private String apiKey;
    private String host;
    private int port;
    private String database;
    private String user;
    private String password;
    private long numberOfComments;
    private String regionCode;

    public PropertiesObjDownloader(String name, String apiKey, String host, int port, String database, String user, String password, long numberOfComments, String regionCode) {
        this.name = name;
        this.apiKey = apiKey;
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

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "PropertiesObjDownloader{" + "nombre=" + name + ", apiKey=" + apiKey + ", host=" + host + ", port=" + port + ", database=" + database + ", user=" + user + ", password=" + password + ", numberOfComments=" + numberOfComments + '}';
    }

}
