/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.soap.*;
import org.bellota.config.PropiedadesAPP;
import org.bellota.util.objects.WSResponse.ResTransaccionesWSEX;
import org.bellota.util.objects.WSResponse.pedidos.ObjLineaPedido;
import org.bellota.util.objects.WSResponse.pedidos.ObjOrdenPedido;
import org.bellota.util.objects.WSResponse.pedidos.ResPedidosWS;

/**
 *
 * @author Cristian Alberto Perez
 */
public class UtilFabricaSOAP {

    private static final PropiedadesAPP utilPropiedades = new PropiedadesAPP();
    private static FileOutputStream streamArchivo;
    private static InputStream targetStream;
    private static SOAPPart parteSOAP;
    private static String endpointSOAP;
    private static String accionSOAP;
    private static String espacioNombresPrincipal;
    private static String espacioNombresAdicional;
    private static String espacioNombresURIP;
    private static String espacioNombresURIA;
    private static SOAPConnectionFactory fabricaConexionSOAP;
    private static SOAPConnection conexionSOAP;
    private static SOAPMessage respuestaSOAP;
    private static SOAPEnvelope inicioSOAP;
    private static SOAPBody cuerpoSOAP;
    private static SOAPElement elementoCuerpoSOAP;
    private static MessageFactory fabricaMensajesSOAP;
    private static SOAPMessage mensajeSOAP;
    private static MimeHeaders cabezerasSOAP;
    private static SOAPElement eleObjPrincipalSOAP;
    private static SOAPElement eleHijoObjPrincipalSOAP;
    private static SOAPElement eleNodoIndSOAP;
    private static SOAPElement eleNodoIndSOAP2;
    private static SOAPElement eleNodoIndSOAP3;
    private static SOAPElement eleNodoIndSOAP4;
    private static String resltadoProceso, resultadoGuardarXML;

    private static final String archivoXMLNuevo = utilPropiedades.obtenerPropiedades().getProperty("RutaArchivosRemota");
    private static final DateFormat formatoFecha = new SimpleDateFormat("yyyyMMdd-hhmmssSSS");
    ;
    private static File archivoXML;

    public static String llamarWSExterno(List<?> listadoTransacciones, String tipoTransaccion) {
        if (tipoTransaccion.equals("P")) {
            endpointSOAP = utilPropiedades.obtenerPropiedades().getProperty("WFCBodegaEndPointP");
            accionSOAP = utilPropiedades.obtenerPropiedades().getProperty("WFCBodegaResMethod");
            espacioNombresPrincipal = utilPropiedades.obtenerPropiedades().getProperty("WFCBodegaNameSpaceP");
            espacioNombresAdicional = utilPropiedades.obtenerPropiedades().getProperty("WFCBodegaNameSpaceA");
            espacioNombresURIP = utilPropiedades.obtenerPropiedades().getProperty("WFCBodegaUriNameSpaceP");
            espacioNombresURIA = utilPropiedades.obtenerPropiedades().getProperty("WFCBodegaUriNameSpaceA");
            resltadoProceso = llamarWebServiceSOAP(endpointSOAP, accionSOAP, listadoTransacciones, tipoTransaccion);
        } else {
            endpointSOAP = utilPropiedades.obtenerPropiedades().getProperty("WFCBatchEndPointP");
            accionSOAP = utilPropiedades.obtenerPropiedades().getProperty("WFCBatchResMethod");
            espacioNombresPrincipal = utilPropiedades.obtenerPropiedades().getProperty("WFCBatchNameSpaceP");
            espacioNombresAdicional = utilPropiedades.obtenerPropiedades().getProperty("WFCBatchNameSpaceA");
            espacioNombresURIP = utilPropiedades.obtenerPropiedades().getProperty("WFCBatchUriNameSpaceP");
            espacioNombresURIA = utilPropiedades.obtenerPropiedades().getProperty("WFCBatchUriNameSpaceA");
            resltadoProceso = llamarWebServiceSOAP(endpointSOAP, accionSOAP, listadoTransacciones, tipoTransaccion);
        }
        return resltadoProceso;
    }

    private static String llamarWebServiceSOAP(String endpointSOAP, String accionSOAP, List<?> listadoTransacciones, String tipoTransaccion) {
        try {
            // Se crea la Conexion SOAP
            fabricaConexionSOAP = SOAPConnectionFactory.newInstance();
            conexionSOAP = fabricaConexionSOAP.createConnection();

            // Se envia el mensaje SOAP
            respuestaSOAP = conexionSOAP.call(crearSolicitudSOAP(accionSOAP, listadoTransacciones, tipoTransaccion), endpointSOAP);

            // Se imprime el Resultado del Proceso
            System.out.println("Mensaje de Respuesta SOAP:");
            respuestaSOAP.writeTo(System.out);
            System.out.println();

            resltadoProceso = respuestaSOAP.getSOAPBody().getAttribute("ResTransaccionesResult");

            conexionSOAP.close();
        } catch (Exception ex) {
            Logger.getLogger(UtilFabricaSOAP.class.getName()).log(Level.SEVERE, null, ex);
            resltadoProceso = "Se ha generado un error enviando la informacion al servicio VISUAL - Se guardara el Archivo para Reintentos: " + ex;
            System.out.println(resltadoProceso);
            guardarArchivoXML(tipoTransaccion);
        }
        return resltadoProceso;
    }

    // Metodos Adicionales al Proceso 
    // Metos para el proceso Principal
    private static SOAPMessage crearSolicitudSOAP(String accionSOAP, List<?> listadoTransacciones, String tipoTransaccion) throws Exception {

        fabricaMensajesSOAP = MessageFactory.newInstance();
        mensajeSOAP = fabricaMensajesSOAP.createMessage();

        if (tipoTransaccion.equals("P")) {
            crearCuerpoSoapWCFBodega(mensajeSOAP, (List<ResPedidosWS>) listadoTransacciones);
        } else {
            crearCuerpoSoapWCFBatch(mensajeSOAP, (List<ResTransaccionesWSEX>) listadoTransacciones, tipoTransaccion);
        }

        cabezerasSOAP = mensajeSOAP.getMimeHeaders();
        cabezerasSOAP.addHeader("SOAPAction", accionSOAP);

        mensajeSOAP.saveChanges();

        System.out.println("Mensaje de Solicitud SOAP:");
        mensajeSOAP.writeTo(System.out);
        System.out.println("\n");

        return mensajeSOAP;
    }

    private static void crearCuerpoSoapWCFBatch(SOAPMessage mensajeSOAP, List<ResTransaccionesWSEX> listadoTransacciones, String tipoTransaccion) throws SOAPException {

        // Parte inicial del Envelope del llamado SOAP
        parteSOAP = mensajeSOAP.getSOAPPart();
        inicioSOAP = parteSOAP.getEnvelope();
        // Declaracion de Nombres de Espacios
        inicioSOAP.addNamespaceDeclaration(espacioNombresPrincipal, espacioNombresURIP);
        inicioSOAP.addNamespaceDeclaration(espacioNombresAdicional, espacioNombresURIA);
        // Cuerpo del Llamado SOAP
        cuerpoSOAP = inicioSOAP.getBody();
        elementoCuerpoSOAP = cuerpoSOAP.addChildElement("ResTransacciones", espacioNombresPrincipal);
        eleObjPrincipalSOAP = elementoCuerpoSOAP.addChildElement("objRes", espacioNombresPrincipal);

        for (ResTransaccionesWSEX transaccionInd : listadoTransacciones) {
            eleHijoObjPrincipalSOAP = eleObjPrincipalSOAP.addChildElement("ResTransaccionesWSEX", espacioNombresAdicional);
            eleNodoIndSOAP = eleHijoObjPrincipalSOAP.addChildElement("estadoTransaccion", espacioNombresAdicional);
            eleNodoIndSOAP.addTextNode(String.valueOf(transaccionInd.getEstadoTransaccion()));
            eleNodoIndSOAP = eleHijoObjPrincipalSOAP.addChildElement("idLog", espacioNombresAdicional);
            eleNodoIndSOAP.addTextNode(String.valueOf(transaccionInd.getIdLog()));
            eleNodoIndSOAP = eleHijoObjPrincipalSOAP.addChildElement("idTransaccion", espacioNombresAdicional);
            eleNodoIndSOAP.addTextNode(transaccionInd.getIdTransaccion());
            eleNodoIndSOAP = eleHijoObjPrincipalSOAP.addChildElement("mensajeTransaccion", espacioNombresAdicional);
            eleNodoIndSOAP.addTextNode("<![CDATA[" + transaccionInd.getMensajeTransaccion() + "]]>");
            eleNodoIndSOAP = eleHijoObjPrincipalSOAP.addChildElement("numeroId", espacioNombresAdicional);
            eleNodoIndSOAP.addTextNode(String.valueOf(transaccionInd.getNumeroId()));
        }

        eleObjPrincipalSOAP = elementoCuerpoSOAP.addChildElement("tipoRespuesta", espacioNombresPrincipal);
        eleObjPrincipalSOAP.addTextNode(tipoTransaccion);
    }

    private static void crearCuerpoSoapWCFBodega(SOAPMessage mensajeSOAP, List<ResPedidosWS> listadoTransacciones) throws SOAPException {
        // Parte inicial del Envelope del llamado SOAP
        parteSOAP = mensajeSOAP.getSOAPPart();
        inicioSOAP = parteSOAP.getEnvelope();
        // Declaracion de Nombres de Espacios
        inicioSOAP.addNamespaceDeclaration(espacioNombresPrincipal, espacioNombresURIP);
        inicioSOAP.addNamespaceDeclaration(espacioNombresAdicional, espacioNombresURIA);
        // Cuerpo del Llamado SOAP
        cuerpoSOAP = inicioSOAP.getBody();
        elementoCuerpoSOAP = cuerpoSOAP.addChildElement("ResPedido", espacioNombresPrincipal);

        for (ResPedidosWS pedidoInd : listadoTransacciones) {
            eleObjPrincipalSOAP = elementoCuerpoSOAP.addChildElement("objRes", espacioNombresPrincipal);
            eleObjPrincipalSOAP.addChildElement("BidContractId", espacioNombresAdicional).addTextNode(pedidoInd.getBidContractId());

            eleNodoIndSOAP = eleObjPrincipalSOAP.addChildElement("Orders", espacioNombresAdicional);

            for (ObjOrdenPedido ordenInd : pedidoInd.getOrders()) {
                eleNodoIndSOAP2 = eleNodoIndSOAP.addChildElement("RespOrdersPedido", espacioNombresAdicional);
                eleNodoIndSOAP2.addChildElement("ErrCode", espacioNombresAdicional).addTextNode(ordenInd.getErrCode());
                eleNodoIndSOAP2.addChildElement("ErrDescription", espacioNombresAdicional).addTextNode("<![CDATA[" + ordenInd.getErrDescription() + "]]>");
                eleNodoIndSOAP3 = eleNodoIndSOAP2.addChildElement("Lines", espacioNombresAdicional);

                for (ObjLineaPedido lineaInd : ordenInd.getLines()) {
                    eleNodoIndSOAP4 = eleNodoIndSOAP3.addChildElement("RespLinesPedido", espacioNombresAdicional);
                    eleNodoIndSOAP4.addChildElement("DocLineNumber", espacioNombresAdicional).addTextNode(lineaInd.getDocLineNumber());
                    eleNodoIndSOAP4.addChildElement("LineErrCode", espacioNombresAdicional).addTextNode(lineaInd.getLineErrCode());
                    eleNodoIndSOAP4.addChildElement("LineErrDescription", espacioNombresAdicional).addTextNode("<![CDATA[" + lineaInd.getLineErrDescription() + "]]>");
                    eleNodoIndSOAP4.addChildElement("OrderLineNumber", espacioNombresAdicional).addTextNode(lineaInd.getOrderLineNumber());
                }

                eleNodoIndSOAP2.addChildElement("OrderNumber", espacioNombresAdicional).addTextNode(ordenInd.getOrderNumber());
                eleNodoIndSOAP2.addChildElement("TotalLines", espacioNombresAdicional).addTextNode(ordenInd.getTotalLines());
            }

            eleObjPrincipalSOAP.addChildElement("TotalOrders", espacioNombresAdicional).addTextNode(pedidoInd.getTotalOrders());

        }
    }

    private static void guardarArchivoXML(String tipoTransaccion) {
        try {

            if (tipoTransaccion.equals("P")) {
                archivoXML = new File(archivoXMLNuevo + "SOAP-XML-" + formatoFecha.format(new Date()) + ".xml");
            } else {
                archivoXML = new File(archivoXMLNuevo + "SOAP-XML-" + "-P" + formatoFecha.format(new Date()) + ".xml");
            }

            streamArchivo = new FileOutputStream(archivoXML);
            mensajeSOAP.writeTo(streamArchivo);

            streamArchivo.flush();
            streamArchivo.close();

            targetStream = new FileInputStream(archivoXML);
            mensajeSOAP = MessageFactory.newInstance().createMessage(null, targetStream);
            mensajeSOAP.writeTo(System.out);
            targetStream.close();

            resultadoGuardarXML = "Se ha guardado un nuevo archivo XML Para Reintentos.";
            System.out.println(resultadoGuardarXML);

        } catch (IOException | SOAPException ex) {
            Logger.getLogger(UtilFabricaSOAP.class.getName()).log(Level.SEVERE, null, ex);
            resultadoGuardarXML = "Se ha generado un error guardando archivo XML: " + ex;
            System.out.println(resultadoGuardarXML);
        }
    }

}
