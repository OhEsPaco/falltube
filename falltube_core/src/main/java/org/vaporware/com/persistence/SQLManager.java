package org.vaporware.com.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vaporware.com.domain.objects.Comment;
import org.vaporware.com.domain.objects.SimplifiedVideo;

/**
 *
 * @author pacog
 */
public class SQLManager {

    private static Connection mBD;
    private static String url;
    private static String driver = "com.mysql.cj.jdbc.Driver";

    public SQLManager(String host, int port, String database, String user, String password) {
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?user=" + user + "&password=" + password + "&serverTimezone=UTC&useSSL=false";
        //conectar();
    }

    public Connection getConnection() {
        return mBD;
    }

    //Metodo para conectar de la base de datos
    private void conectar() {

        try {
            Class.forName(driver);
            mBD = DriverManager.getConnection(url);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // Metodo para desconectar de la base de datos
    public void desconectar() {
        try {
            mBD.close();
        } catch (Exception e) {

        }
    }

    public boolean isVideoOnDatabase(String id) {
        boolean r = true;
        try {
            String sql = "Select 1 from video where videoId = ?";
            conectar();
            PreparedStatement ps = mBD.prepareStatement(sql);
            ps.setString(1, id);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            r = rs.next();
            desconectar();

        } catch (SQLException ex) {

            desconectar();

        }
        return r;
    }

    public int insertVideo(SimplifiedVideo video) throws SQLException {
        int res = 0;
        try {
            conectar();
            String sql = "insert into video values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = mBD.prepareStatement(sql);
            ps.setString(1, video.getId());
            ps.setString(2, video.getPublishedAt());
            ps.setString(3, video.getChannelId());
            ps.setString(4, video.getTitle().substring(0, (video.getTitle().length() < 200) ? video.getTitle().length() : 200));//200
            ps.setString(5, video.getChannelTitle().substring(0, (video.getChannelTitle().length() < 200) ? video.getChannelTitle().length() : 200));//2000
            ps.setString(6, video.getCategoryId());
            ps.setLong(7, getDurationSeconds(video.getDuration()));
            ps.setString(8, video.getDefinition());
            
            if (video.getDefaultAudioLanguage() == null) {
                ps.setBoolean(9, false);
            } else {
                ps.setBoolean(9, true);
            }
            
            ps.setBoolean(10, video.isCaption());
            ps.setBoolean(11, video.isLicensedContent());

            ps.setLong(12, video.getViewCount());
            ps.setLong(13, video.getLikeCount());
            ps.setLong(14, video.getDislikeCount());
            ps.setLong(15, video.getCommentCount());

            //Impacto social 
            ps.setLong(16, video.getViewCount() + video.getLikeCount() + video.getDislikeCount() + video.getCommentCount());

            ps.setLong(17, video.getTags().size());

            res = ps.executeUpdate();
            desconectar();

        } catch (SQLException ex) {
            desconectar();
            throw new SQLException();
        }
        return res;
    }

    public void insertComments(ArrayList<Comment> comments) throws SQLException {
        for (Comment comment : comments) {
            insertComment(comment);
        }
    }

    public int insertComment(Comment comment) {
        int res = 0;
        try {
            conectar();
            String sql = "insert into comments values(?,?,?,?,?)";
            PreparedStatement ps = mBD.prepareStatement(sql);
            ps.setString(1, comment.getCommentId());
            ps.setString(2, comment.getVideoId());
            ps.setString(3, comment.getAuthorName().substring(0, (comment.getAuthorName().length() < 100) ? comment.getAuthorName().length() : 100));//100
            ps.setString(4, comment.getaAthorUrl().substring(0, (comment.getaAthorUrl().length() < 100) ? comment.getaAthorUrl().length() : 100));//100
            ps.setString(5, comment.getComment().substring(0, (comment.getComment().length() < 2000) ? comment.getComment().length() : 2000));//2000

            res = ps.executeUpdate();

            desconectar();

        } catch (Exception ex) {

            desconectar();

        }
        return res;
    }

    public long getDurationSeconds(String time) {
        time = time.substring(2);
        long duration = 0L;
        Object[][] indexs = new Object[][]{{"H", 3600}, {"M", 60}, {"S", 1}};
        for (int i = 0; i < indexs.length; i++) {
            int index = time.indexOf((String) indexs[i][0]);
            if (index != -1) {
                String value = time.substring(0, index);
                duration += Integer.parseInt(value) * (int) indexs[i][1];
                time = time.substring(value.length() + 1);
            }
        }
        return duration;
    }

}
