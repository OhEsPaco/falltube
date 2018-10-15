/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaporware.com.persistence;

import java.util.ArrayList;
import org.vaporware.com.domain.Comment;
import org.vaporware.com.domain.YvdSimplified;

/**
 *
 * @author pacog
 */
public class SQLManager {

    private static SQLManager INSTANCE;

    private SQLManager() {

    }

    public static SQLManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SQLManager();
        }
        return INSTANCE;
    }

    public boolean isVideoOnDatabase(String videoId) {
        //IMPLEMENTAR ESTE METODO
        return false;
    }

    public void videoToDatabase(YvdSimplified video) {
        //IMPLEMENTAR ESTE METODO
    }

    public void commentsToDatabase(ArrayList<Comment> comments) {
        //IMPLEMENTAR ESTE METODO
    }
}
