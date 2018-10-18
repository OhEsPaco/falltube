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
    
    private static Connection conn;
    private static PreparedStatement ps;
    
    public static boolean isVideoOnDatabase(String videoId) {
        //IMPLEMENTAR ESTE METODO
        boolean a = false;
        
        return a;
    }

    public static  void videoToDatabase(YvdSimplified video) {
        
		try {
						
			String sql = "insert into proyectos values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";			
			ps = conn.prepareStatement(sql);
			ps.setString(1, video.getId());
			ps.setString(2, video.getEtag());
			ps.setString(3, video.getPublishedAt());
			ps.setString(4, video.getChannelId());
			ps.setString(5, video.getTitle());
                        ps.setString(6, video.getDescription());
			ps.setString(7, video.getChannelTitle());
			ps.setString(8, video.getCategoryId());
			ps.setString(9, video.getDefaultAudioLanguage());
			ps.setString(10, video.getDuration());
                        ps.setString(11, video.getDimension());
			ps.setString(12, video.getDefinition());
			ps.setBoolean(13, video.isCaption());
			ps.setBoolean(14, video.isLicensedContent());
			ps.setString(15, video.getProjection());
                        ps.setLong(16, video.getViewCount());
			ps.setLong(17, video.getLikeCount());
			ps.setLong(18, video.getDislikeCount());
			ps.setLong(19, video.getCommentCount());
			ps.setString(20, video.getTags().get(0));
                        ps.setString(21, video.getTags().get(1));
			ps.setString(22, video.getTags().get(2));
			ps.setString(23, video.getTags().get(3));
			ps.setString(24, video.getTags().get(4));
			ps.setString(25, video.getTags().get(5));
			ps.setString(26, video.getTags().get(6));
			ps.setString(27, video.getTags().get(7));
			ps.setString(28, video.getTags().get(8));
			ps.setString(29, video.getTags().get(9));
                        
                        SQLManager.getInstance().insert(sql);

			
		} catch (Exception e) {
			System.out.println("Error al insertar proyecto en la base de datos.");
		}
    }

    public static void commentsToDatabase(ArrayList<Comment> comments) {
        //IMPLEMENTAR ESTE METODO
        
    }
    private static void commentToDatabase(Comment comment){
        //SQLManager.getInstance().insert("");
    }
}
