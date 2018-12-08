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
package org.vaporware.com.domain.objects;

import java.util.ArrayList;

public class PropertiesObjManagement extends PropertiesObjDownloader {

    private ArrayList<String> apiKeys = new ArrayList<String>();
    private ArrayList<String> querys = new ArrayList<String>();
    private int numberOfUIAgents;
    private int numberOfDownloaderAgents;
    private int numberOfSearchAgents;

    public PropertiesObjManagement(String host, int port, String database, String user, String password, String regionCode, int numberOfUIAgents, int numberOfDownloaderAgents, int numberOfSearchAgents) {
        super(host, port, database, user, password, regionCode);
        this.numberOfDownloaderAgents = numberOfDownloaderAgents;
        this.numberOfUIAgents = numberOfUIAgents;
        this.numberOfSearchAgents = numberOfSearchAgents;
    }

    public int getNumberOfUIAgents() {
        return numberOfUIAgents;
    }

    public int getNumberOfDownloaderAgents() {
        return numberOfDownloaderAgents;
    }

    public int getNumberOfSearchAgents() {
        return numberOfSearchAgents;
    }

    public ArrayList<String> getQuerys() {
        return querys;
    }

    public void addQuery(String query) {
        querys.add(query);
    }

    public void addApiKey(String apiKey) {
        apiKeys.add(apiKey);
    }

    public void setQuerys(ArrayList<String> querys) {
        this.querys = querys;
    }

    public ArrayList<String> getApiKeys() {
        return apiKeys;
    }

    public PropertiesObjDownloader getPropertiesObjDownloader() {
        return new PropertiesObjDownloader(getHost(), getPort(), getDatabase(), getUser(), getPassword(), getRegionCode());
    }

    @Override
    public String toString() {
        return "PropertiesObjManagement{" + "apiKeys=" + apiKeys + ", querys=" + querys + ", numberOfUIAgents=" + numberOfUIAgents + ", numberOfDownloaderAgents=" + numberOfDownloaderAgents + ", numberOfSearchAgents=" + numberOfSearchAgents + '}';
    }

}
