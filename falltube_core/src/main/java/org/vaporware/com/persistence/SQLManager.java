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
    private static String url = "jdbc:mysql://localhost:3307/practicabd?user=alumno&password=alumno";    
    private static String driver = "com.mysql.jdbc.Driver";
    
    private SQLManager() {
        conectar();
    }

    public static SQLManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SQLManager();
        }
        return INSTANCE;
    }
    
    //Metodo para conectar de la base de datos
    private void conectar() {
        try{
            Class.forName(driver);
            mBD = DriverManager.getConnection(url);
        }catch(Exception e){
            
        }
    }

    // Metodo para desconectar de la base de datos
    public void desconectar() {
        try{
            mBD.close();
        }catch(Exception e){
            
        }
    }

    // Metodo para realizar una insercion en la base de datos
    public int insert(String SQL) throws SQLException, Exception {
        conectar();
        PreparedStatement stmt = mBD.prepareStatement(SQL);
        int res = stmt.executeUpdate();
        stmt.close();
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
