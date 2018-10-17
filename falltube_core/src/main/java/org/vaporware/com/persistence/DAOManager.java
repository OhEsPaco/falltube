/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaporware.com.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import org.vaporware.com.domain.Comment;
import org.vaporware.com.domain.YvdSimplified;

/**
 *
 * @author Angel
 */
public class DAOManager {
    
    private Connection conn;
    private PreparedStatement ps;
    
    public static boolean isVideoOnDatabase(String videoId) {
        //IMPLEMENTAR ESTE METODO
        boolean a = false;
        
        return a;
    }

    public static void videoToDatabase(YvdSimplified video) {
        //IMPLEMENTAR ESTE METODO
        //SQLManager.getInstance().insert("");
    }

    public static void commentsToDatabase(ArrayList<Comment> comments) {
        //IMPLEMENTAR ESTE METODO
        
    }
    private static void commentToDatabase(Comment comment){
        //SQLManager.getInstance().insert("");
    }
}
