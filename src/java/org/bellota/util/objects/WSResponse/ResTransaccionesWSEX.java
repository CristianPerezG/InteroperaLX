/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.util.objects.WSResponse;

/**
 *
 * @author Cristian Alberto Perez
 */
public class ResTransaccionesWSEX {

    private String numeroId; // DID
    private String idTransaccion;// BATCH
    private String mensajeTransaccion; // DESER
    private int estadoTransaccion; // FLAG
    private String idLog;    // IDLOG

    public String getNumeroId() {
        return numeroId;
    }

    public void setNumeroId(String numeroId) {
        this.numeroId = numeroId;
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public int getEstadoTransaccion() {
        return estadoTransaccion;
    }

    public void setEstadoTransaccion(int estadoTransaccion) {
        this.estadoTransaccion = estadoTransaccion;
    }

    public String getMensajeTransaccion() {
        return mensajeTransaccion;
    }

    public void setMensajeTransaccion(String mensajeTransaccion) {
        this.mensajeTransaccion = mensajeTransaccion;
    }

    public String getIdLog() {
        return idLog;
    }

    public void setIdLog(String idLog) {
        this.idLog = idLog;
    }    

}
