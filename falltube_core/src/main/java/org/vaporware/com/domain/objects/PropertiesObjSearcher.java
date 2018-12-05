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

public class PropertiesObjSearcher {

    private String nombre;
    private ArrayList<String> downloadAgents = new ArrayList<String>();
    private ArrayList<String> apiKeys = new ArrayList<String>();
    private ArrayList<String> querys = new ArrayList<String>();

    public PropertiesObjSearcher(String nombre) {

        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArrayList<String> getDownloadAgents() {
        return downloadAgents;
    }

    public void setDownloadAgents(ArrayList<String> downloadAgents) {
        this.downloadAgents = downloadAgents;
    }

    public ArrayList<String> getQuerys() {
        return querys;
    }

    public void addQuery(String query) {
        querys.add(query);
    }

    public void adddownloadAgent(String downloadAgent) {
        downloadAgents.add(downloadAgent);
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

    public void setApiKeys(ArrayList<String> apiKeys) {
        this.apiKeys = apiKeys;
    }

    @Override
    public String toString() {
        return "PropertiesObjSearcher{" + "nombre=" + nombre + ", downloadAgents=" + downloadAgents + ", apiKey=" + apiKeys.size() + ", querys=" + querys + '}';
    }

}
