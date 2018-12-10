/*
MIT License

Copyright (c) 2018 Francisco Manuel Garcia Sanchez

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
package org.vaporware.com.persistence;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vaporware.com.domain.exceptions.AlreadyExistsException;
import org.vaporware.com.domain.exceptions.ImpossibleToCreateTable;
import org.vaporware.com.domain.objects.Comment;
import org.vaporware.com.domain.objects.SimplifiedVideo;
import org.vaporware.com.domain.utilities.TextCleaner;

public class SQLManager {

    private Connection mBD;
    private static String url;
    private static String driver = "com.mysql.cj.jdbc.Driver";

    public SQLManager(String host, int port, String database, String user, String password) {
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=true&characterEncoding=utf-8&user=" + user + "&password=" + password + "&serverTimezone=UTC&useSSL=false";

    }

    public Connection getConnection() {
        return mBD;
    }

    //Metodo para conectar de la base de datos
    private void conectar() throws SQLException {

        try {
            Class.forName(driver);
            mBD = DriverManager.getConnection(url);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void tryDatabase() throws SQLException {
        conectar();
        desconectar();
    }

    // Metodo para desconectar de la base de datos
    public void desconectar() {
        try {
            mBD.close();
        } catch (Exception e) {

        }
    }

    public boolean isVideoOnDatabase(String id) {
        boolean r = false;
        try {
            String sql = "Select 1 from video where videoId = ?";
            conectar();
            PreparedStatement ps = mBD.prepareStatement(sql);
            ps.setString(1, id);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            if (rs != null) {
                r = rs.next();
            }

            desconectar();

        } catch (SQLException ex) {

            desconectar();

        }
        return r;
    }

    public void createTable() throws ImpossibleToCreateTable {
        try {
            conectar();

            String sql = " CREATE TABLE `video` ("
                    + "  `videoId` varchar(45) NOT NULL,"
                    + "  `publishedAt` varchar(100) DEFAULT NULL,"
                    + "  `channelId` varchar(100) DEFAULT NULL,"
                    + "  `title` varchar(200) DEFAULT NULL,"
                    + "  `channelTitle` varchar(200) DEFAULT NULL,"
                    + "  `categoryId` varchar(100) DEFAULT NULL,"
                    + "  `duration` bigint(15) DEFAULT NULL,"
                    + "  `definition` varchar(10) DEFAULT NULL,"
                    + "  `defaultAudioLanguage` tinyint(4) DEFAULT NULL,"
                    + "  `caption` tinyint(4) DEFAULT NULL,"
                    + "  `licensedContent` tinyint(4) DEFAULT NULL,"
                    + "  `viewCount` bigint(15) DEFAULT NULL,"
                    + "  `likeCount` bigint(15) DEFAULT NULL,"
                    + "  `dislikeCount` bigint(15) DEFAULT NULL,"
                    + "  `commentsCount` bigint(15) DEFAULT NULL,"
                    + "  `socialImpact` bigint(25) DEFAULT NULL,"
                    + "  `tags` bigint(5) DEFAULT NULL,"
                    + "  PRIMARY KEY (`videoId`)"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";
            Statement stmt = mBD.createStatement();
            stmt.executeUpdate(sql);
            desconectar();
        } catch (Exception ex) {
            desconectar();
            throw new ImpossibleToCreateTable();
        }

    }

    public int insertVideo(SimplifiedVideo video) throws SQLException, AlreadyExistsException {
        int res = 0;
        try {
            conectar();
            String sql = "insert into video values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = mBD.prepareStatement(sql);
            ps.setString(1, video.getId());
            ps.setString(2, video.getPublishedAt());
            ps.setString(3, video.getChannelId());
            String titulo = video.getTitle().substring(0, (video.getTitle().length() < 200) ? video.getTitle().length() : 200);//200
            ps.setString(4, TextCleaner.cleanTextContent(titulo));
            titulo = video.getChannelTitle().substring(0, (video.getChannelTitle().length() < 200) ? video.getChannelTitle().length() : 200);//2000
            ps.setString(5, TextCleaner.cleanTextContent(titulo));
            ps.setString(6, TextCleaner.stripOffNoAlphanumerics(TextCleaner.cleanTextContent(video.getCategoryId())));
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
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            throw new AlreadyExistsException();
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

    public boolean tableExists() {
        boolean r = false;
        try {

            conectar();

            DatabaseMetaData dbm = mBD.getMetaData();
            ResultSet rs = dbm.getTables(null, null, "video", null);
            if (rs.next()) {
                r = true;
            } else {
                r = false;
            }

            desconectar();

        } catch (SQLException ex) {
            desconectar();

        }
        return r;
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
