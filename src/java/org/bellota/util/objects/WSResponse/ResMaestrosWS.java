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
public class ResMaestrosWS {

    private int Batch;
    private int TipoRespuesta;
    private String TipoRespuestaDescripcion;
    private int estadoGeneral;
    private String estadoDescripcionGeneral;

    public int getBatch() {
        return Batch;
    }

    public void setBatch(int Batch) {
        this.Batch = Batch;
    }

    public int getTipoRespuesta() {
        return TipoRespuesta;
    }

    public void setTipoRespuesta(int TipoRespuesta) {
        this.TipoRespuesta = TipoRespuesta;
    }

    public String getTipoRespuestaDescripcion() {
        return TipoRespuestaDescripcion;
    }

    public void setTipoRespuestaDescripcion(String TipoRespuestaDescripcion) {
        this.TipoRespuestaDescripcion = TipoRespuestaDescripcion;
    }

    public int getEstadoGeneral() {
        return estadoGeneral;
    }

    public void setEstadoGeneral(int estadoGeneral) {
        this.estadoGeneral = estadoGeneral;
    }

    public String getEstadoDescripcionGeneral() {
        return estadoDescripcionGeneral;
    }

    public void setEstadoDescripcionGeneral(String estadoDescripcionGeneral) {
        this.estadoDescripcionGeneral = estadoDescripcionGeneral;
    }
}
