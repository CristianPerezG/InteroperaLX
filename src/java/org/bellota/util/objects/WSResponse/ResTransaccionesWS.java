/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.util.objects.WSResponse;


import java.util.List;
import org.bellota.util.objects.WSRequest.ReqTransaccionesWS;

/**
 *
 * @author Cristian Alberto Perez
 */
public class ResTransaccionesWS {

    private String Batch;
    private int TipoRespuesta;
    private String TipoRespuestaDescripcion;
    private int estadoGeneral;
    private String estadoDescripcionGeneral;    
    private List<ReqTransaccionesWS> Transacciones;

    public String getBatch() {
        return Batch;
    }

    public void setBatch(String Batch) {
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

    public List<ReqTransaccionesWS> getTransacciones() {
        return Transacciones;
    }

    public void setTransacciones(List<ReqTransaccionesWS> Transacciones) {
        this.Transacciones = Transacciones;
    }

}
