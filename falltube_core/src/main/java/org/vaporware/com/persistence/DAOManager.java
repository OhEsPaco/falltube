/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaporware.com.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
    private SQLManager sqlmanager;

    public DAOManager() {
        sqlmanager = SQLManager.getInstance();
        conn = sqlmanager.getConnection();
    }

    public boolean isVideoOnDatabase(String videoId) throws SQLException {
        return  SQLManager.getInstance().isVideo(videoId);
    }

    public void videoToDatabase(YvdSimplified video) throws SQLException, Exception {

        SQLManager.getInstance().insertVideo(video);

    }

    public void commentsToDatabase(ArrayList<Comment> comments) throws Exception {
       SQLManager.getInstance().insertComments(comments);

    }
}
