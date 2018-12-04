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
