/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.util.objects.WSRequest;

import java.util.List;
import org.bellota.util.objects.WSResponse.pedidos.ObjDetallePedido;

/**
 *
 * @author Cristian Alberto Perez
 */
public class ReqPedidosWS {

    private String nombreOperacion;
    private String codigoCliente;
    private String codigoEnvio;
    private String fechaSolicitudEnvio;
    private String codigoOrdenCompra;
    private String idContrato;
    private String codigoPedido;
    private String fechaUsuario1;
    private String fechaUsuario2;
    private String notaOrdenCompra;
    private String bodegaPedido;
    private List<ObjDetallePedido> detallePedido;

    public String getBodegaPedido() {
        return bodegaPedido;
    }

    public void setBodegaPedido(String bodegaPedido) {
        this.bodegaPedido = bodegaPedido;
    }

    public String getNombreOperacion() {
        return nombreOperacion;
    }

    public void setNombreOperacion(String nombreOperacion) {
        this.nombreOperacion = nombreOperacion;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getCodigoEnvio() {
        return codigoEnvio;
    }

    public void setCodigoEnvio(String codigoEnvio) {
        this.codigoEnvio = codigoEnvio;
    }

    public String getFechaSolicitudEnvio() {
        return fechaSolicitudEnvio;
    }

    public void setFechaSolicitudEnvio(String fechaSolicitudEnvio) {
        this.fechaSolicitudEnvio = fechaSolicitudEnvio;
    }

    public String getCodigoOrdenCompra() {
        return codigoOrdenCompra;
    }

    public void setCodigoOrdenCompra(String codigoOrdenCompra) {
        this.codigoOrdenCompra = codigoOrdenCompra;
    }

    public String getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(String idContrato) {
        this.idContrato = idContrato;
    }

    public String getCodigoPedido() {
        return codigoPedido;
    }

    public void setCodigoPedido(String numeroOrden) {
        this.codigoPedido = numeroOrden;
    }

    public String getFechaUsuario1() {
        return fechaUsuario1;
    }

    public void setFechaUsuario1(String fechaUsuario1) {
        this.fechaUsuario1 = fechaUsuario1;
    }

    public String getFechaUsuario2() {
        return fechaUsuario2;
    }

    public void setFechaUsuario2(String fechaUsuario2) {
        this.fechaUsuario2 = fechaUsuario2;
    }

    public String getNotaOrdenCompra() {
        return notaOrdenCompra;
    }

    public void setNotaOrdenCompra(String notaOrdenCompra) {
        this.notaOrdenCompra = notaOrdenCompra;
    }

    public List<ObjDetallePedido> getDetallePedido() {
        return detallePedido;
    }

    public void setDetallePedido(List<ObjDetallePedido> detallePedido) {
        this.detallePedido = detallePedido;
    }
}
