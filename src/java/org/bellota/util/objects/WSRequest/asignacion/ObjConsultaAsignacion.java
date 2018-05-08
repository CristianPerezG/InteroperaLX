/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.util.objects.WSRequest.asignacion;

/**
 *
 * @author Cristian Alberto Perez
 */
public class ObjConsultaAsignacion {

    private int idAsignacion;
    private int flagProceso;
    private String mensajeProceso;

    public int getIdAsignacion() {
        return idAsignacion;
    }

    public void setIdAsignacion(int idAsignacion) {
        this.idAsignacion = idAsignacion;
    }

    public int getFlagProceso() {
        return flagProceso;
    }

    public void setFlagProceso(int flagProceso) {
        this.flagProceso = flagProceso;
    }

    public String getMensajeProceso() {
        return mensajeProceso;
    }

    public void setMensajeProceso(String mensajeProceso) {
        this.mensajeProceso = mensajeProceso;
    }
}
