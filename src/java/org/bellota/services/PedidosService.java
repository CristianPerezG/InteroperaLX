/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.services;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

import org.bellota.services.operators.OperadorPedidos;
import org.bellota.util.objects.WSResponse.pedidos.ObjDetallePedido;

import org.bellota.util.objects.WSRequest.ReqPedidosWS;

/**
 *
 * @author Cristian Alberto Perez
 */
@WebService(serviceName = "PedidosService")
public class PedidosService {

    private final OperadorPedidos operadorPedidos = new OperadorPedidos();
    private String respuestaServicio;

    /**
     *
     * @param productoSolicitado
     * @param idLog
     * @return
     */
    @WebMethod(operationName = "crearPedido")
    public String crearPedido(
            @WebParam(name = "listadoPedido") ReqPedidosWS productoSolicitado,
            @WebParam(name = "idLog") String idLog) {
        try {
            operadorPedidos.operarCrecionPedido(productoSolicitado, idLog);
            respuestaServicio = "Recibido Correctamente en el Servicio Proceso: Creación de Pedido";
        } catch (Exception ex) {
            Logger.getLogger(PedidosService.class.getName()).log(Level.SEVERE, null, ex);
            respuestaServicio = "Error recibiendo en el Serivicio: " + ex.toString();
        }
        return respuestaServicio;
    }

    /**
     *
     * @param productoSolicitado
     * @param idLog
     * @return
     */
    @WebMethod(operationName = "modificarPedido")
    public String modificarPedido(
            @WebParam(name = "listadoPedido") ReqPedidosWS productoSolicitado,
            @WebParam(name = "idLog") String idLog) {
        try {
            operadorPedidos.operarModificacionPedido(productoSolicitado, idLog);
            respuestaServicio = "Recibido Correctamente en el Servicio Proceso: Modificación de Pedido";
        } catch (Exception ex) {
            Logger.getLogger(PedidosService.class.getName()).log(Level.SEVERE, null, ex);
            respuestaServicio = "Error recibiendo en el Serivicio: " + ex.toString();
        }
        return respuestaServicio;
    }

    /**
     *
     * @param codigoPedido
     * @param codigoCliente
     * @param idLog
     * @param idContrato
     * @return
     */
    @WebMethod(operationName = "eliminarPedido")
    public String eliminarPedido(
            @WebParam(name = "codigoPedido") String codigoPedido,
            @WebParam(name = "codigoCliente") String codigoCliente,
            @WebParam(name = "idLog") String idLog,
            @WebParam(name = "idContrato") String idContrato) {
        try {
            operadorPedidos.operarEliminacionPedido(codigoPedido, codigoCliente, idLog, idContrato);
            respuestaServicio = "Recibido Correctamente en el Servicio Proceso: Eliminación de Pedido";
        } catch (Exception ex) {
            Logger.getLogger(PedidosService.class.getName()).log(Level.SEVERE, null, ex);
            respuestaServicio = "Error recibiendo en el Serivicio: " + ex.toString();
        }
        return respuestaServicio;
    }

    /**
     *
     * @param listadoLineas
     * @param codigoPedido
     * @param codigoCliente
     * @param idContrato
     * @param idLog
     * @return
     */
    @WebMethod(operationName = "agregarLinea")
    public String agregarLinea(@WebParam(name = "listadoLineas") List<ObjDetallePedido> listadoLineas,
            @WebParam(name = "codigoPedido") String codigoPedido,
            @WebParam(name = "codigoCliente") String codigoCliente,
            @WebParam(name = "idContrato") String idContrato,
            @WebParam(name = "idLog") String idLog) {
        try {
            operadorPedidos.operarAdicionLinea(listadoLineas, codigoPedido, codigoCliente, idContrato, idLog);
            respuestaServicio = "Recibido Correctamente en el Servicio Proceso: Adición de Linea";
        } catch (Exception ex) {
            Logger.getLogger(PedidosService.class.getName()).log(Level.SEVERE, null, ex);
            respuestaServicio = "Error recibiendo en el Serivicio: " + ex.toString();
        }
        return respuestaServicio;
    }

    /**
     *
     * @param listadoLineas
     * @param codigoPedido
     * @param codigoCliente
     * @param idContrato
     * @param idLog
     * @return
     */
    @WebMethod(operationName = "modificarLinea")
    public String modificarLinea(
            @WebParam(name = "listadoLineas") List<ObjDetallePedido> listadoLineas,
            @WebParam(name = "codigoPedido") String codigoPedido,
            @WebParam(name = "codigoCliente") String codigoCliente,
            @WebParam(name = "idContrato") String idContrato,
            @WebParam(name = "idLog") String idLog) {
        try {
            operadorPedidos.operarModificacionLinea(listadoLineas, codigoPedido, codigoCliente, idContrato, idLog);
            respuestaServicio = "Recibido Correctamente en el Servicio Proceso: Modificacón de Linea";
        } catch (Exception ex) {
            Logger.getLogger(PedidosService.class.getName()).log(Level.SEVERE, null, ex);
            respuestaServicio = "Error recibiendo en el Serivicio: " + ex.toString();
        }
        return respuestaServicio;
    }

    /**
     *
     * @param listadoLineas
     * @param codigoPedido
     * @param codigoCliente
     * @param idContrato
     * @param idLog
     * @return
     */
    @WebMethod(operationName = "eliminarLinea")
    public String eliminarLinea(@WebParam(name = "listadoLineas") List<ObjDetallePedido> listadoLineas,
            @WebParam(name = "codigoPedido") String codigoPedido,
            @WebParam(name = "codigoCliente") String codigoCliente,
            @WebParam(name = "idContrato") String idContrato,
            @WebParam(name = "idLog") String idLog) {
        try {
            operadorPedidos.operarEliminacionLinea(listadoLineas, codigoPedido, codigoCliente, idContrato, idLog);
            respuestaServicio = "Recibido Correctamente en el Servicio Proceso: Eliminación de Linea";
        } catch (Exception ex) {
            Logger.getLogger(PedidosService.class.getName()).log(Level.SEVERE, null, ex);
            respuestaServicio = "Error recibiendo en el Serivicio: " + ex.toString();
        }
        return respuestaServicio;
    }

}
