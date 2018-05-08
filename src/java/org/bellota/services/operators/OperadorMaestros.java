/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.services.operators;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bellota.as400.operators.OperadorAS400;
import org.bellota.as400.operators.OperadorLXConectors;
import org.bellota.util.objects.WSRequest.ReqMaestrosWS;
import org.bellota.util.objects.WSRequest.productos.ObjProductoEAN;
import org.bellota.util.objects.WSRequest.productos.ObjProductoPrecios;
import org.bellota.util.objects.WSResponse.ResMaestrosWS;

/**
 *
 * @author Cristian Alberto Perez
 */
public class OperadorMaestros {

    private ResMaestrosWS resultadoMaestros;

    private String insertSQL, querySQL, resultadoLX, resultadoInsert;
    private Date fechaAhora;

    private final SimpleDateFormat formatoFechaNumerico = new SimpleDateFormat("yyyyMMdd");
    private final SimpleDateFormat formatoHoraNumerico = new SimpleDateFormat("hhmmss");

    private final OperadorLXConectors operadorLX = new OperadorLXConectors();
    private final OperadorAS400 operadorAS400 = new OperadorAS400();

    public ResMaestrosWS operarCreacionProducto(String iBatch, ReqMaestrosWS productoSolicitado) {

        try {
            generarAuditoria("Inicio Proceso de Creación de Producto Sincrono. Metodo: operarCreacionProducto");

            operadorLX.obtenerConexionLX();
            resultadoMaestros = new ResMaestrosWS();
            resultadoMaestros.setBatch(Integer.valueOf(iBatch));

            resultadoMaestros.setTipoRespuesta(1);
            resultadoLX = operadorLX.crearProducto(productoSolicitado);

            if (resultadoLX.contains("Se ha generado un Error devuelto por el conector IIM.")) {
                resultadoMaestros.setTipoRespuesta(-1);
                resultadoMaestros.setTipoRespuestaDescripcion("1. Error General en el Proceso");
                resultadoMaestros.setEstadoGeneral(-1);
                resultadoMaestros.setEstadoDescripcionGeneral("1. Recibido, -1 Con Error en creacionPedido: " + resultadoLX);
            } else {

                for (ObjProductoEAN eanInd : productoSolicitado.getEAN()) {
                    resultadoInsert = operadorAS400.insertSync400(crearInsertZLI(eanInd, productoSolicitado.getCodigo()), operadorAS400.obtenerConexion());
                    if (!resultadoInsert.contains("Insertado Exitosamente.")) {
                        resultadoMaestros.setTipoRespuesta(-1);
                        resultadoMaestros.setTipoRespuestaDescripcion("1. Error General en el Proceso");
                        resultadoMaestros.setEstadoGeneral(-1);
                        resultadoMaestros.setEstadoDescripcionGeneral("1. Recibido, -1 Con Error registrando informacion en la ZLI: " + resultadoInsert);
                    }
                }

                for (ObjProductoPrecios precioInd : productoSolicitado.getPrecios()) {
                    resultadoInsert = operadorAS400.insertSync400(crearInsertPrecios(precioInd, productoSolicitado.getCodigo(), productoSolicitado.getInstalacion()), operadorAS400.obtenerConexion());
                    if (!resultadoInsert.contains("Insertado Exitosamente.")) {
                        resultadoMaestros.setTipoRespuesta(-1);
                        resultadoMaestros.setTipoRespuestaDescripcion("1. Error General en el Proceso");
                        resultadoMaestros.setEstadoGeneral(-1);
                        resultadoMaestros.setEstadoDescripcionGeneral("1. Recibido, -1 Con Error registrando informacion en la ESP: " + resultadoInsert);
                    }
                }

                if (resultadoMaestros.getTipoRespuesta() == 1) {
                    resultadoMaestros.setTipoRespuestaDescripcion("1. Envio a LX");
                    resultadoMaestros.setEstadoGeneral(1);
                    resultadoMaestros.setEstadoDescripcionGeneral("1. Recibido, 1 Sin Errores: " + resultadoLX);
                }
            }

        } catch (Exception ex) {
            resultadoMaestros.setTipoRespuesta(-1);
            resultadoMaestros.setTipoRespuestaDescripcion("1. Error General en el Proceso");
            resultadoMaestros.setEstadoGeneral(-1);
            resultadoMaestros.setEstadoDescripcionGeneral("1. Recibido, -1 Con Error en creacionPedido: " + ex.toString());
        } finally {
            if (operadorLX.esConexionAbierta()) {
                operadorLX.cerrarConexionLX();
            }
            generarAuditoria("Finalizo Proceso de Creación de Producto Sincrono. Metodo: operarCreacionProducto");
        }
        return resultadoMaestros;
    }

    private String crearInsertPrecios(ObjProductoPrecios preciosProducto, String producto, String instalacionProducto) throws Exception {
        try {

            querySQL = "INSERT INTO ESP( ";
            insertSQL = " VALUES( ";
            querySQL += "SPID, ";
            insertSQL += "'SP',"; //2A Record ID; SP/SZ

            if (preciosProducto.getRegion().equals("BCOL")) {
                querySQL += "PMETH, ";
                insertSQL += "'D',";  //1A    Pricing Method
                querySQL += "PRKEY, ";
                insertSQL += "'" + producto.substring(0, Math.min(producto.length(), 15)) + instalacionProducto + "', ";  //21A   Pricing Key
            } else {
                querySQL += " PMETH, ";
                insertSQL += "'B', ";  //1A    Pricing Method
                querySQL += " PRKEY, ";
                insertSQL += "'" + producto.substring(0, Math.min(producto.length(), 15)) + preciosProducto.getRegion() + "', ";  //21A   Pricing Key
            }

            querySQL += " PFCT1     , ";
            insertSQL += " " + preciosProducto.getPrecio() + ", ";  //14P4  Factor Type #1
            querySQL += " PSDTE     , ";
            insertSQL += " " + preciosProducto.getFechaDesde() + ", ";  //8P0   Starting Date
            querySQL += " PSEDT     , ";
            insertSQL += " " + preciosProducto.getFechaHasta() + ", ";  //8P0   PO Ending Date
            querySQL += " PDESC     , ";
            insertSQL += "'APIPRODUCTO', ";  //15A   Contract Description
            querySQL += " PCURR     , ";
            insertSQL += "'" + preciosProducto.getMoneda() + "', ";  //3A    Currency Code
            querySQL += " PCOMP     , ";
            insertSQL += " " + preciosProducto.getCompany() + ", ";  //2P0   Company Number

            fechaAhora = new Date();

            querySQL += " SPENDT    , ";
            insertSQL += " " + formatoFechaNumerico.format(fechaAhora) + ", ";  //8P0   Creation Date
            querySQL += " SPENTM    , ";
            insertSQL += " " + formatoHoraNumerico.format(fechaAhora) + ", ";  //6P0   Creation Time
            querySQL += " SPENUS    , ";
            insertSQL += "'APIPROD', ";  //10A   User Created
            querySQL += " SPMNDT    , ";
            insertSQL += " " + formatoFechaNumerico.format(fechaAhora) + ", ";  //8P0   Last Maintenance Date
            querySQL += " SPMNTM    , ";
            insertSQL += " " + formatoHoraNumerico.format(fechaAhora) + ", ";  //6P0   Last Maintenance Time
            querySQL += " SPMNUS    ) ";
            insertSQL += "'APIPROD') ";  //10A   User Last Maintained

        } catch (Exception ex) {
            Logger.getLogger(OperadorAS400.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception(ex);
        }
        return querySQL + insertSQL;
    }

    private String crearInsertZLI(ObjProductoEAN productoEAN, String numeroProducto) throws Exception {
        try {

            querySQL = " INSERT INTO ZLI( ";
            insertSQL = " VALUES ( ";
            querySQL += " LIID, ";
            insertSQL += "'LI', ";     						//2A    Record ID; LI/LZ
            querySQL += " LIPROD, ";
            insertSQL += "'" + numeroProducto + "', ";  					//15A   Item number
            querySQL += " LILANG, ";
            insertSQL += "'" + productoEAN.getUEmpaqueEAN() + "', ";     				//3A    Language
            querySQL += " LIDSC1, ";
            insertSQL += "'" + productoEAN.getDescripcionEtiqueta() + "', ";     		//40A   Description 1
            querySQL += " LIDSC2, ";
            insertSQL += "'" + productoEAN.getDescripcionEtiquetaExtendida() + "', ";     	//40A   Description 2
            querySQL += " LIFORM, ";
            insertSQL += "'" + productoEAN.getCodigoEAN() + "', ";     				//15A   Engineering/Formula number
            querySQL += " LIREF) ";
            insertSQL += "'" + productoEAN.getDescripcionUnidadesEtiqueta() + "' ) ";     	//24A   Reference

        } catch (Exception ex) {
            Logger.getLogger(OperadorAS400.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception(ex);
        }
        return querySQL + insertSQL;
    }

    public void generarAuditoria(String mensaje) {
        java.util.Date date = new java.util.Date();
        SimpleDateFormat formato = new SimpleDateFormat("hh:mm:ss:mmmm");
        System.out.println(mensaje + " - Hora: " + formato.format(date));
    }
}
