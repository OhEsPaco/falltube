/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaporware.com.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vaporware.com.domain.Comment;
import org.vaporware.com.domain.YvdSimplified;

/**
 *
 * @author pacog
 */
public class SQLManager {

    private static SQLManager INSTANCE;

    protected static SQLManager mInstancia = null;
    protected static Connection mBD;
    private static String url = "jdbc:mysql://localhost:3306/falltube?user=falltube&password=76/9+;bRAr&serverTimezone=UTC&useSSL=false";
    private static String driver = "com.mysql.cj.jdbc.Driver";

    private SQLManager() {
        conectar();
    }

    public Connection getConnection() {
        return mBD;
    }

    public static SQLManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SQLManager();
        }
        return INSTANCE;
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

    public boolean isVideo(String id) throws SQLException {
        String sql = "Select 1 from video where videoId = ?";
        conectar();
        PreparedStatement ps = mBD.prepareStatement(sql);
        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();
        boolean r= rs.next();
        desconectar();
        return r;
    }

    // Metodo para realizar una insercion en la base de datos
    public int insert(PreparedStatement stmt) throws SQLException, Exception {
        conectar();
        int res = stmt.executeUpdate();
        stmt.close();
        desconectar();
        return res;
    }

    public int insertVideo(YvdSimplified video) throws SQLException, Exception {

        conectar();
        String sql = "insert into video values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = mBD.prepareStatement(sql);
        ps.setString(1, video.getId());
        ps.setString(2, video.getEtag());
        ps.setString(3, video.getPublishedAt());
        ps.setString(4, video.getChannelId());
        
        ps.setString(5, video.getTitle().substring(0, (video.getTitle().length() < 200)?video.getTitle().length():200));//200
        ps.setString(6, video.getDescription().substring(0, (video.getDescription().length() < 2000)?video.getDescription().length():2000));//2000
        ps.setString(7, video.getChannelTitle().substring(0, (video.getChannelTitle().length() < 200)?video.getChannelTitle().length():200));//200
        
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
            ps.setString(lastTag, video.getTags().get(i).substring(0, (video.getTags().get(i).length() < 200)?video.getTags().get(i).length():200));//200
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
        ps.setString(3, comment.getAuthorName().substring(0, (comment.getAuthorName().length() < 100)?comment.getAuthorName().length():100));//100
        ps.setString(4, comment.getaAthorUrl().substring(0, (comment.getaAthorUrl().length() < 100)?comment.getaAthorUrl().length():100));//100
        ps.setString(5, comment.getComment().substring(0, (comment.getComment().length() < 2000)?comment.getComment().length():2000));//2000
        int res=0;
        try{
              res = ps.executeUpdate();
        }catch(SQLException ex){
            
        }
       
        desconectar();
        return res;

    }

    // Metodo para realizar una eliminacion en la base de datos
    public int delete(String SQL) throws SQLException, Exception {
        PreparedStatement stmt = mBD.prepareStatement(SQL);
        int res = stmt.executeUpdate();
        stmt.close();
        desconectar();
        return res;
    }

    // Metodo para realizar una actualizacion en la base de datos
    public int update(String SQL) throws SQLException, Exception {
        conectar();
        PreparedStatement stmt = mBD.prepareStatement(SQL);
        int res = stmt.executeUpdate();
        stmt.close();
        desconectar();
        return res;
    }

    public Vector<Object> select(String SQL) throws SQLException, Exception {
        /*
        * Metodo para realizar una busqueda o seleccion de informacion en la
	* base de datos El mŽtodo select develve un vector de vectores, donde
        * cada uno de los vectores que contiene el vector principal representa
	* los registros que se recuperan de la base de datos.
         */

        // Creamos el vector que retornaremos mas adelante
        Vector<Object> v = new Vector<Object>();

        // Conectamos con la base de datos
        conectar();

        // Creamos la sentencia SQL
        Statement st = mBD.createStatement();
        // Ejecutamos la consulta
        ResultSet rSet = st.executeQuery(SQL);

        // Creamos un vector auxiliar que al final insertaremos dentro
        // del vector v
        Vector<Object> auxiliar = new Vector<Object>();

        while (rSet.next()) {

            //Completamos el vector auxiliar y se lo añadimos a v
            auxiliar.add(rSet.getString("login"));
            auxiliar.add(rSet.getString("pass"));
            v.add(auxiliar);
        }

        return v;
    }
}
