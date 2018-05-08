/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.services;

import java.util.List;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.bellota.services.operators.OperadorDespachos;
import org.bellota.util.objects.WSRequest.ReqDespachosWS;
import org.bellota.util.objects.WSRequest.asignacion.ObjAsignacion;
import org.bellota.util.objects.WSResponse.ResDespachosWS;
import org.bellota.util.objects.WSResponse.asignacion.ResAsigancion;

/**
 *
 * @author Cristian Alberto Perez
 */
@WebService(serviceName = "DespachosService")
public class DespachosService {

    private final OperadorDespachos operadorDespachos = new OperadorDespachos();
    private ResDespachosWS objResServicio;
    private ResAsigancion objAsigacion;

    /**
     * This is a sample web service operation
     *
     * @param listadoTraslados
     * @param iBatch
     * @return
     */
    @WebMethod(operationName = "operarTraslados")
    public ResDespachosWS operarTraslados(@WebParam(name = "listadoTraslados") List<ReqDespachosWS> listadoTraslados,
            @WebParam(name = "iBatch") String iBatch) {
        try {
            objResServicio = new ResDespachosWS();

            objResServicio.setBatch(iBatch);
            objResServicio.setTipoRespuesta(1);
            objResServicio.setTipoRespuestaDescripcion("1. Recibido en el Servicio WEB");
            objResServicio.setEstadoGeneral(1);
            objResServicio.setEstadoDescripcionGeneral("1. Recibido, 1. Recibido Exitosamente en el Servicio");
            objResServicio.setTransacciones(listadoTraslados);

            operadorDespachos.operarTraslados(listadoTraslados, iBatch);

        } catch (Exception ex) {
            objResServicio.setBatch("iBatch");
            objResServicio.setTipoRespuesta(-1);
            objResServicio.setTipoRespuestaDescripcion("-1. Error en el Proceso del Inventario");
            objResServicio.setEstadoGeneral(-1);
            objResServicio.setEstadoDescripcionGeneral("-1. Error, -1. Se ha generado un error en el Servicio DespachosService - " + ex);
            objResServicio.setTransacciones(listadoTraslados);
        }
        return objResServicio;
    }

    @WebMethod(operationName = "operarAsgnacion")
    public ResAsigancion operarAsgnacion(@WebParam(name = "listadoAsignacion") List<ObjAsignacion> listadoAsignacion,
            @WebParam(name = "idLog") String idLog) {
        try {
            objAsigacion = new ResAsigancion();
            objAsigacion.setBatch(listadoAsignacion.get(0).getiBatchProceso());
            objAsigacion.setEstado(1);
            objAsigacion.setEstadoDescripcion("Recibido Correctamente en el Servicio");
            objAsigacion.setPedidosTotal(listadoAsignacion.size());

            // Asincrono
            operadorDespachos.operarAsignacion(listadoAsignacion, idLog);

        } catch (Exception ex) {
            objAsigacion = new ResAsigancion();
            objAsigacion.setBatch(listadoAsignacion.get(0).getiBatchProceso());
            objAsigacion.setEstado(-1);
            objAsigacion.setEstadoDescripcion("Se ha generado un error recibiendo la Asignación: " + (listadoAsignacion.size() > 0 ? ex.toString() : "Listado en Blanco."));
            objAsigacion.setPedidosTotal(listadoAsignacion.size());
        }
        return objAsigacion;
    }

}
