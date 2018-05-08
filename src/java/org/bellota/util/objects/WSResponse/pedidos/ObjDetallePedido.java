/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.util.objects.WSResponse.pedidos;

/**
 *
 * @author Cristian Alberto Perez
 */
public class ObjDetallePedido {

    private String lineaOperacion;
    private String numeroLineaOrden;
    private String documentoNumeroLinea;
    private String codigoItem;
    private String cantidadOrdenada;
    private String precioItem;
    private String notaLinea;

    public String getLineaOperacion() {
        return lineaOperacion;
    }

    public void setLineaOperacion(String lineaOperacion) {
        this.lineaOperacion = lineaOperacion;
    }

    public String getNumeroLineaOrden() {
        return numeroLineaOrden;
    }

    public void setNumeroLineaOrden(String numeroLineaOrden) {
        this.numeroLineaOrden = numeroLineaOrden;
    }

    public String getDocumentoNumeroLinea() {
        return documentoNumeroLinea;
    }

    public void setDocumentoNumeroLinea(String documentoNumeroLinea) {
        this.documentoNumeroLinea = documentoNumeroLinea;
    }

    public String getCodigoItem() {
        return codigoItem;
    }

    public void setCodigoItem(String codigoItem) {
        this.codigoItem = codigoItem;
    }

    public String getCantidadOrdenada() {
        return cantidadOrdenada;
    }

    public void setCantidadOrdenada(String cantidadOrdenada) {
        this.cantidadOrdenada = cantidadOrdenada;
    }

    public String getPrecioItem() {
        return precioItem;
    }

    public void setPrecioItem(String precioItem) {
        this.precioItem = precioItem;
    }

    public String getNotaLinea() {
        return notaLinea;
    }

    public void setNotaLinea(String notaLinea) {
        this.notaLinea = notaLinea;
    }
}
