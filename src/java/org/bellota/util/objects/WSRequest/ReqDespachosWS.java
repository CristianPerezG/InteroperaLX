/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.util.objects.WSRequest;

/**
 *
 * @author Cristian Alberto Perez
 */
public class ReqDespachosWS {
 
    private ReqTransaccionesWS transaccionEntrada;
    private ReqTransaccionesWS transaccionSalida;

    public ReqTransaccionesWS getTransaccionEntrada() {
        return transaccionEntrada;
    }

    public void setTransaccionEntrada(ReqTransaccionesWS transaccionEntrada) {
        this.transaccionEntrada = transaccionEntrada;
    }

    public ReqTransaccionesWS getTransaccionSalida() {
        return transaccionSalida;
    }

    public void setTransaccionSalida(ReqTransaccionesWS transaccionSalida) {
        this.transaccionSalida = transaccionSalida;
    }    
}
