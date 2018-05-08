/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.as400.AS400Conectors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cristian Alberto Perez
 */
public class ConectorConsAS400 {

    private String[] columnasResultado;
    private String queryDB2, msjError;

    private Connection conexionAS400;
    private ResultSet resultadoQuery;
    private ResultSetMetaData metaDataResultado;
    private Statement st;    

    public ConectorConsAS400(Connection conexionSolicitada, String querySolicitado) throws SQLException {

        this.conexionAS400 = conexionSolicitada;
        this.queryDB2 = querySolicitado;

        try {
            st = conexionAS400.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            resultadoQuery = st.executeQuery(queryDB2);
            metaDataResultado = resultadoQuery.getMetaData();
            msjError = null;
        } catch (SQLException ex) {
            msjError = ex.getMessage();            
        }
    }

    public String obtenerError() {
        return this.msjError;
    }

    public ResultSet obtenerResultado() {
        return this.resultadoQuery;
    }

    public String[] obtenerNombresColumnas() {
        try {
            int numColumnas = metaDataResultado.getColumnCount();
            this.columnasResultado = new String[numColumnas];
            for (int a = 0; a < numColumnas; a++) {
                columnasResultado[a] = metaDataResultado.getColumnLabel(a + 1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConectorConsAS400.class.getName()).log(Level.SEVERE, null, ex);
        }
        return columnasResultado;
    }

}
