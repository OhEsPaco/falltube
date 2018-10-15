/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaporware.com.domain.search;

/**
 *
 * @author paco
 */
public class Item {

    private String kind;

    public String getKind() {
        return this.kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    private String etag;

    public String getEtag() {
        return this.etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    private Id id;

    public Id getId() {
        return this.id;
    }

    public void setId(Id id) {
        this.id = id;
    }

  
}
