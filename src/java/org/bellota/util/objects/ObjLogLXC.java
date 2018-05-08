/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.util.objects;

import java.util.Date;

/**
 *
 * @author Cristian Alberto Perez
 */
public class ObjLogLXC {

    private int idProceso;
    private int mensajePrioridad;
    private Date fechaproceso;
    private boolean confirmaProcesos;
    private String idObjeto;
    private String mensajeProceso;
    private String estadoProceso;
    private String nombreFuente;
    private String usuarioProceso;

    public int getIdProceso() {
        return idProceso;
    }

    public void setIdProceso(int idProceso) {
        this.idProceso = idProceso;
    }

    public int getMensajePrioridad() {
        return mensajePrioridad;
    }

    public void setMensajePrioridad(int mensajePreoridad) {
        this.mensajePrioridad = mensajePreoridad;
    }

    public Date getFechaproceso() {
        return fechaproceso;
    }

    public void setFechaproceso(Date fechaproceso) {
        this.fechaproceso = fechaproceso;
    }

    public boolean isConfirmaProcesos() {
        return confirmaProcesos;
    }

    public void setConfirmaProcesos(boolean confirmaProcesos) {
        this.confirmaProcesos = confirmaProcesos;
    }

    public String getIdObjeto() {
        return idObjeto;
    }

    public void setIdObjeto(String idObjeto) {
        this.idObjeto = idObjeto;
    }

    public String getMensajeProceso() {
        return mensajeProceso;
    }

    public void setMensajeProceso(String mensajeProceso) {
        this.mensajeProceso = mensajeProceso;
    }

    public String getEstadoProceso() {
        return estadoProceso;
    }

    public void setEstadoProceso(String estadoProceso) {
        this.estadoProceso = estadoProceso;
    }

    public String getNombreFuente() {
        return nombreFuente;
    }

    public void setNombreFuente(String nombreFuente) {
        this.nombreFuente = nombreFuente;
    }

    public String getUsuarioProceso() {
        return usuarioProceso;
    }

    public void setUsuarioProceso(String usuarioProceso) {
        this.usuarioProceso = usuarioProceso;
    }
}
