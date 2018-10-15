/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaporware.com.domain.video;

public class RegionRestriction {

    private String[] allowed;

    public String[] getAllowed() {
        return allowed;
    }

    public void setAllowed(String[] allowed) {
        this.allowed = allowed;
    }

    @Override
    public String toString() {
        return "ClassPojo [allowed = " + allowed + "]";
    }
}
