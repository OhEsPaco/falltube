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
    //private static String url = "jdbc:mysql://localhost:3306/falltube?user=falltube&password=76/9+;bRAr&serverTimezone=UTC&useSSL=false";
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

    public boolean isVideoOnDatabase(String id) throws SQLException {
        String sql = "Select 1 from video where videoId = ?";
        conectar();
        PreparedStatement ps = mBD.prepareStatement(sql);
        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();
        boolean r = rs.next();
        desconectar();
        return r;
    }

    public int insertVideo(SimplifiedVideo video) throws SQLException, Exception {

        conectar();
        String sql = "insert into video values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = mBD.prepareStatement(sql);
        ps.setString(1, video.getId());
        ps.setString(2, video.getEtag());
        ps.setString(3, video.getPublishedAt());
        ps.setString(4, video.getChannelId());

        ps.setString(5, video.getTitle().substring(0, (video.getTitle().length() < 200) ? video.getTitle().length() : 200));//200
        ps.setString(6, video.getDescription().substring(0, (video.getDescription().length() < 2000) ? video.getDescription().length() : 2000));//2000
        ps.setString(7, video.getChannelTitle().substring(0, (video.getChannelTitle().length() < 200) ? video.getChannelTitle().length() : 200));//200

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

        int lastTag = 20;
        for (int i = 0; i < video.getTags().size() && i < 10; i++) {
            ps.setString(lastTag, video.getTags().get(i).substring(0, (video.getTags().get(i).length() < 200) ? video.getTags().get(i).length() : 200));//200
            lastTag++;
        }

        if (lastTag != 30) {
            for (int i = lastTag; i < 30; i++) {
                ps.setString(i, null);
            }
        }

        int res = ps.executeUpdate();
        desconectar();
        return res;

    }

    public void insertComments(ArrayList<Comment> comments) throws SQLException, Exception {
        for (Comment comment : comments) {
            insertComment(comment);
        }
    }

    public int insertComment(Comment comment) throws SQLException, Exception {

        conectar();
        String sql = "insert into comments values(?,?,?,?,?)";
        PreparedStatement ps = mBD.prepareStatement(sql);
        ps.setString(1, comment.getCommentId());
        ps.setString(2, comment.getVideoId());
        ps.setString(3, comment.getAuthorName().substring(0, (comment.getAuthorName().length() < 100) ? comment.getAuthorName().length() : 100));//100
        ps.setString(4, comment.getaAthorUrl().substring(0, (comment.getaAthorUrl().length() < 100) ? comment.getaAthorUrl().length() : 100));//100
        ps.setString(5, comment.getComment().substring(0, (comment.getComment().length() < 2000) ? comment.getComment().length() : 2000));//2000
        int res = 0;
        try {
            res = ps.executeUpdate();
        } catch (SQLException ex) {

        }

        desconectar();
        return res;

    }

}
