/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.util.objects.WSResponse.asignacion;

/**
 *
 * @author Cristian Alberto Perez
 */
public class ResAsigancion {

    private String batch;
    private int estado;
    private String estadoDescripcion;
    private int pedidosTotal;

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getEstadoDescripcion() {
        return estadoDescripcion;
    }

    public void setEstadoDescripcion(String estadoDescripcion) {
        this.estadoDescripcion = estadoDescripcion;
    }

    public int getPedidosTotal() {
        return pedidosTotal;
    }

    public void setPedidosTotal(int pedidosTotal) {
        this.pedidosTotal = pedidosTotal;
    }

}
