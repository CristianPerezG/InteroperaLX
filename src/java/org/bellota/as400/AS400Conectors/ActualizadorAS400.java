/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.as400.AS400Conectors;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Cristian Alberto Perez
 */
public class ActualizadorAS400 {

    private Connection conexionAS400;
    private int resultadoProceso;
    private String querySQL, msjError;
    private Statement declaracionAS400;

    public ActualizadorAS400(Connection conexionSolicitada, String querySolicitado) throws Exception {

        this.conexionAS400 = conexionSolicitada;
        this.querySQL = querySolicitado;

        try {
            declaracionAS400 = conexionAS400.createStatement();
            resultadoProceso = declaracionAS400.executeUpdate(querySQL);
            if (resultadoProceso == 0) {
                msjError = "Error en el Proceso de Inserción - DB2 Responde 0 en UPDATE o INSERT.";
            } else {
                msjError = null;
            }
        } catch (SQLException ex) {
            msjError = ex.getMessage();
            throw new Exception(ex);
        }
    }

    public String obtenerMensajeError() {
        return this.msjError;
    }

    public int obtenerResultado() {
        return this.resultadoProceso;
    }

}
