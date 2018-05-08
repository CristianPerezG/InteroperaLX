/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.services;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.bellota.util.objects.WSResponse.ResTransaccionesWS;
import org.bellota.util.objects.WSRequest.ReqTransaccionesWS;

import org.bellota.services.operators.OperadorTransacciones;

/**
 *
 * @author Cristian Alberto Perez
 */
@WebService(serviceName = "TransaccionesService")
public class TransaccionesService {

    private ResTransaccionesWS objResServicio;
    private final OperadorTransacciones operadorTransacciones = new OperadorTransacciones();

    @WebMethod(operationName = "operarTransaccionInv")
    public ResTransaccionesWS operarTransaccionInv(
            @WebParam(name = "listadoProceso") List<ReqTransaccionesWS> listadoProceso,
            @WebParam(name = "iBatch") String iBatch) {
        try {

            objResServicio = new ResTransaccionesWS();

            objResServicio.setBatch(iBatch);
            objResServicio.setTipoRespuesta(1);
            objResServicio.setTipoRespuestaDescripcion("1. Recibido en el Servicio WEB");
            objResServicio.setEstadoGeneral(1);
            objResServicio.setEstadoDescripcionGeneral("1. Recibido, 1. Recibido Exitosamente en el Servicio");
            objResServicio.setTransacciones(listadoProceso);

            operadorTransacciones.procesarTransaccionInv(listadoProceso, iBatch);

        } catch (Exception ex) {
            objResServicio.setBatch("0");
            objResServicio.setTipoRespuesta(-1);
            objResServicio.setTipoRespuestaDescripcion("-1. Error en el Proceso del Inventario");
            objResServicio.setEstadoGeneral(-1);
            objResServicio.setEstadoDescripcionGeneral("-1. Error, -1. Se ha generado un error en el Servicio - " + ex);
            objResServicio.setTransacciones(listadoProceso);
        }
        return objResServicio;
    }

    @WebMethod(operationName = "operarOtraTransaccionInv")
    public ResTransaccionesWS operarOtraTransaccionInv(
            @WebParam(name = "listadoProceso") List<ReqTransaccionesWS> listadoProceso,
            @WebParam(name = "iBatch") String iBatch) {
        try {

            objResServicio = new ResTransaccionesWS();

            objResServicio.setBatch(iBatch);
            objResServicio.setTipoRespuesta(1);
            objResServicio.setTipoRespuestaDescripcion("1. Recibido en el Servicio WEB");
            objResServicio.setEstadoGeneral(1);
            objResServicio.setEstadoDescripcionGeneral("1. Recibido, 1. Recibido Exitosamente en el Servicio - Operación Transaccion de Otros");
            objResServicio.setTransacciones(listadoProceso);

            operadorTransacciones.procesarOtraTransaccionInv(listadoProceso, iBatch);

        } catch (Exception ex) {
            objResServicio.setBatch("0");
            objResServicio.setTipoRespuesta(-1);
            objResServicio.setTipoRespuestaDescripcion("-1. Error en el Proceso del Inventario - OperarOtraTransaccion");
            objResServicio.setEstadoGeneral(-1);
            objResServicio.setEstadoDescripcionGeneral("-1. Error, -1. Se ha generado un error en el Servicio - " + ex);
            objResServicio.setTransacciones(listadoProceso);
        }
        return objResServicio;
    }

}
