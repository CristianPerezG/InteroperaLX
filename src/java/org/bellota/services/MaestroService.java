/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.services;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.bellota.services.operators.OperadorMaestros;
import org.bellota.util.objects.WSRequest.ReqMaestrosWS;
import org.bellota.util.objects.WSResponse.ResMaestrosWS;

/**
 * Servicio de Maestro de Productos.
 * <p>
 * Servicio que permite la Administracion del Maestro de Productos
 * <p>
 * Este servicio se utilizara de manera Individual procesando en LX por producto
 * solicitado
 *
 * @author Cristian Alberto Perez
 * @version 1.0
 */
@WebService(serviceName = "MaestroService")
public class MaestroService {

    private final OperadorMaestros operadorMaestros = new OperadorMaestros();

    /**
     * Metodo de Creación de Productos.
     * <p>
     * Metodo que permite la creación de un producto con LXConectors
     * <p>
     * Este metodo se utilizara de manera Individual procesando en LX por
     * producto enviado
     *
     * @param productoSolicitado Variable de tipo ReqMaestrosWS
     * @param iBatch Variable de tipo String
     * @return ResMaestrosWS Objeto de tipo GET - SET
     *
     */
    @WebMethod(operationName = "crearProducto")
    public ResMaestrosWS crearProducto(@WebParam(name = "productoSolicitado") ReqMaestrosWS productoSolicitado, @WebParam(name = "iBatch") String iBatch) {
        return operadorMaestros.operarCreacionProducto(iBatch, productoSolicitado);
    }

}
