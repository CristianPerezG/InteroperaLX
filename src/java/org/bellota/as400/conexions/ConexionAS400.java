/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.as400.conexions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cristian Alberto Perez
 */
public class ConexionAS400 {

    private Connection conexionAS400;
    private String msjError = null;

    public ConexionAS400(String usuarioSolicitado, String passwordUsuario, String baseDatos, String servidorHost) throws SQLException, Exception {

        try {
            Class.forName("com.ibm.as400.access.AS400JDBCDriver");
            this.conexionAS400 = DriverManager.getConnection("jdbc:as400://" + servidorHost + "/" + baseDatos + ";translate binary=true;", usuarioSolicitado, passwordUsuario);
        } catch (ClassNotFoundException ex) {
            this.msjError = ex.getMessage();
            throw new Exception(ex);
        } catch (SQLException ex) {
            this.msjError = ex.getMessage();
            throw new SQLException(ex);
        } catch (Exception ex) {
            this.msjError = ex.getMessage();
            throw new Exception(ex);
        }
    }

    public String obtenerError() {
        return this.msjError;
    }

    public Connection obtenerConector() {
        return this.conexionAS400;
    }

    public void cerrarConexion() {
        try {
            this.conexionAS400.close();
        } catch (Exception ex) {
            Logger.getLogger(ConexionAS400.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
