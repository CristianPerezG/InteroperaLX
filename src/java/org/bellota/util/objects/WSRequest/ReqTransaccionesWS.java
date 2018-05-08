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
public class ReqTransaccionesWS {

    private String numeroID;
    private String tipoTransaccion;
    private String fechaProceso;
    private String ordenFabricacion;
    private String ordenCompra;
    private String referenciaProducto;
    private String numeroFactura;
    private String codigoProducto;
    private String numeroLote;
    private String codigoBodega;
    private String ubicacionProducto;
    private String cantidadProducto;
    private String valorProducto;
    private String causalProceso;
    private String idLog;
    private String lineaOrden;

    public String getLineaOrden() {
        return lineaOrden;
    }

    public void setLineaOrden(String lineaOrden) {
        this.lineaOrden = lineaOrden;
    }

    public String resultadoProceso() {
        return numeroID;
    }

    public String getNumeroID() {
        return numeroID;
    }

    public void setNumeroID(String numeroID) {
        this.numeroID = numeroID;
    }

    public String getTipoTransaccion() {
        return tipoTransaccion;
    }

    public void setTipoTransaccion(String tipoTransaccion) {
        this.tipoTransaccion = tipoTransaccion;
    }

    public String getFechaProceso() {
        return fechaProceso;
    }

    public void setFechaProceso(String fechaProceso) {
        this.fechaProceso = fechaProceso;
    }

    public String getOrdenFabricacion() {
        return ordenFabricacion;
    }

    public void setOrdenFabricacion(String ordenFabricacion) {
        this.ordenFabricacion = ordenFabricacion;
    }

    public String getOrdenCompra() {
        return ordenCompra;
    }

    public void setOrdenCompra(String ordenCompra) {
        this.ordenCompra = ordenCompra;
    }

    public String getReferenciaProducto() {
        return referenciaProducto;
    }

    public void setReferenciaProducto(String referenciaProducto) {
        this.referenciaProducto = referenciaProducto;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getNumeroLote() {
        return numeroLote;
    }

    public void setNumeroLote(String numeroLote) {
        this.numeroLote = numeroLote;
    }

    public String getCodigoBodega() {
        return codigoBodega;
    }

    public void setCodigoBodega(String codigoBodega) {
        this.codigoBodega = codigoBodega;
    }

    public String getUbicacionProducto() {
        return ubicacionProducto;
    }

    public void setUbicacionProducto(String ubicacionProducto) {
        this.ubicacionProducto = ubicacionProducto;
    }

    public String getCantidadProducto() {
        return cantidadProducto;
    }

    public void setCantidadProducto(String cantidadProducto) {
        this.cantidadProducto = cantidadProducto;
    }

    public String getValorProducto() {
        return valorProducto;
    }

    public void setValorProducto(String valorProducto) {
        this.valorProducto = valorProducto;
    }

    public String getCausalProceso() {
        return causalProceso;
    }

    public void setCausalProceso(String causalProceso) {
        this.causalProceso = causalProceso;
    }

    public String getIdLog() {
        return idLog;
    }

    public void setIdLog(String idLog) {
        this.idLog = idLog;
    }    
    
}
