/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.services.operators;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bellota.as400.operators.OperadorAS400;
import org.bellota.as400.operators.OperadorLXConectors;
import org.bellota.util.UtilFabricaSOAP;
import org.bellota.util.objects.WSRequest.ReqDespachosWS;
import org.bellota.util.objects.WSRequest.ReqTransaccionesWS;
import org.bellota.util.objects.WSRequest.asignacion.ObjAsignacion;
import org.bellota.util.objects.WSRequest.asignacion.ObjConsultaAsignacion;
import org.bellota.util.objects.WSResponse.ResTransaccionesWSEX;

/**
 *
 * @author Cristian Alberto Perez
 */
public class OperadorDespachos {

    private final OperadorLXConectors operadorLX = new OperadorLXConectors();
    private ResTransaccionesWSEX objResWS;
    private List<ResTransaccionesWSEX> listadoResServico;
    private String resultadoProceso, querySQL, insertSQL;
    private ReqTransaccionesWS transaccionSolicitada;
    private final OperadorAS400 operadorAS400 = new OperadorAS400();
    private boolean confirmaAsignacion;
    private List<ObjConsultaAsignacion> listadoConsultaAsignacion;

    public void operarTraslados(List<ReqDespachosWS> listadoTranslados, String iBatch) {
        new Thread(() -> {
            try {
                generarAuditoria("Inicio Proceso de Traslados en hilo Asincrono. Metodo: operarTranslados");
                operadorLX.obtenerConexionLX();
                listadoResServico = new ArrayList<>();

                listadoTranslados.stream().map((trasladoInd) -> {
                    objResWS = new ResTransaccionesWSEX();
                    return trasladoInd;
                }).map((trasladoInd) -> {
                    transaccionSolicitada = trasladoInd.getTransaccionSalida();
                    return trasladoInd;
                }).map((trasladoInd) -> {
                    resultadoProceso = operadorLX.insertarTransaccionInv(transaccionSolicitada, iBatch);
                    if (resultadoProceso.contains("Error")) {
                        objResWS.setEstadoTransaccion(-1);
                        objResWS.setNumeroId(transaccionSolicitada.getNumeroID() + "|" + trasladoInd.getTransaccionEntrada().getNumeroID());
                        objResWS.setMensajeTransaccion(resultadoProceso + "|" + resultadoProceso);
                        objResWS.setIdTransaccion(iBatch);
                        objResWS.setIdLog(transaccionSolicitada.getIdLog());
                    } else {
                        objResWS.setMensajeTransaccion(resultadoProceso);
                        transaccionSolicitada = trasladoInd.getTransaccionEntrada();
                        resultadoProceso = operadorLX.insertarTransaccionInv(transaccionSolicitada, iBatch);
                        if (resultadoProceso.contains("Error")) {
                            objResWS.setEstadoTransaccion(-1);
                            objResWS.setNumeroId(trasladoInd.getTransaccionSalida().getNumeroID() + "|" + transaccionSolicitada.getNumeroID());
                            objResWS.setMensajeTransaccion(objResWS.getMensajeTransaccion() + "|" + resultadoProceso);
                            objResWS.setIdTransaccion(iBatch);
                            objResWS.setIdLog(transaccionSolicitada.getIdLog());
                        } else {
                            objResWS.setEstadoTransaccion(1);
                            objResWS.setNumeroId(trasladoInd.getTransaccionSalida().getNumeroID() + "|" + transaccionSolicitada.getNumeroID());
                            objResWS.setMensajeTransaccion(objResWS.getMensajeTransaccion() + "|" + resultadoProceso);
                            objResWS.setIdTransaccion(iBatch);
                            objResWS.setIdLog(transaccionSolicitada.getIdLog());
                        }
                    }
                    return trasladoInd;
                }).forEachOrdered((trasladoInd) -> {
                    listadoResServico.add(objResWS);
                });

                System.out.println("Resultado de los Procesos: \n");

                listadoResServico.stream().map((objRespuestaIndv) -> {
                    System.out.println("Id: ");
                    return objRespuestaIndv;
                }).map((objRespuestaIndv) -> {
                    System.out.println(objRespuestaIndv.getIdTransaccion());
                    return objRespuestaIndv;
                }).map((objRespuestaIndv) -> {
                    System.out.println("Estado: ");
                    return objRespuestaIndv;
                }).map((objRespuestaIndv) -> {
                    System.out.println(objRespuestaIndv.getEstadoTransaccion());
                    return objRespuestaIndv;
                }).map((objRespuestaIndv) -> {
                    System.out.println("Mensaje: ");
                    return objRespuestaIndv;
                }).map((objRespuestaIndv) -> {
                    System.out.println(objRespuestaIndv.getMensajeTransaccion());
                    return objRespuestaIndv;
                }).forEachOrdered((objRespuestaIndv) -> {
                    System.out.println("NumeroID: ");
                    System.out.println(objRespuestaIndv.getNumeroId());
                    System.out.println("\n");
                });

                System.out.println(UtilFabricaSOAP.llamarWSExterno(listadoResServico, "T"));

            } catch (Exception ex) {
                Logger.getLogger(OperadorDespachos.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error generando en el proceso principal de Inventario: \n" + ex.toString());

                listadoTranslados.stream().map((trasladoInd) -> {
                    objResWS = new ResTransaccionesWSEX();
                    return trasladoInd;
                }).map((trasladoInd) -> {
                    objResWS.setEstadoTransaccion(-1);
                    objResWS.setNumeroId(trasladoInd.getTransaccionSalida().getNumeroID() + "|" + trasladoInd.getTransaccionEntrada().getNumeroID());
                    objResWS.setMensajeTransaccion("Error generando en el proceso principal de Inventario: \n" + ex.toString() + "|" + "Error generando en el proceso principal de Inventario: \n" + ex.toString());
                    objResWS.setIdTransaccion(iBatch);
                    objResWS.setIdLog(transaccionSolicitada.getIdLog());
                    return trasladoInd;
                }).forEachOrdered((trasladoInd) -> {
                    listadoResServico.add(objResWS);
                });
                System.out.println(UtilFabricaSOAP.llamarWSExterno(listadoResServico, "T"));
            } finally {
                if (operadorLX.esConexionAbierta()) {
                    operadorLX.cerrarConexionLX();
                }
                generarAuditoria("Finalizo Proceso de Traslados en hilo Asincrono. Metodo: operarTranslados");
            }
        }).start();
    }

    public void operarAsignacion(List<ObjAsignacion> listadoAsignacion, String idLog) {
        new Thread(() -> {
            try {
                generarAuditoria("Inicio Proceso de Asignaciones en hilo Asincrono. Metodo: operarAsignacion");
                listadoResServico = new ArrayList<>();
                listadoConsultaAsignacion = new ArrayList<>();
                confirmaAsignacion = true;

                for (ObjAsignacion asignacionInd : listadoAsignacion) {
                    operadorAS400.insertAsync400(crearInsertAsignacion(asignacionInd), operadorAS400.obtenerConexion());
                }

                operadorAS400.llamarPGM(listadoAsignacion.get(0).getiBatchProceso());

                while (confirmaAsignacion) {
                    Thread.sleep(5000);
                    if (operadorAS400.validarAsignacion(listadoAsignacion.get(0).getiBatchProceso())) {
                        confirmaAsignacion = false;
                    }
                }

                if (!confirmaAsignacion) {
                    listadoConsultaAsignacion = operadorAS400.consultarAsiganciones(listadoAsignacion.get(0).getiBatchProceso());

                    listadoConsultaAsignacion.forEach((respuestaInd) -> {
                        listadoAsignacion.stream().filter((asignacionInd) -> (asignacionInd.getIdAsigancion().equals(String.valueOf(respuestaInd.getIdAsignacion())))).forEachOrdered((asignacionInd) -> {
                            objResWS = new ResTransaccionesWSEX();
                            objResWS.setIdLog(idLog);
                            objResWS.setNumeroId(asignacionInd.getIdAsigancion());
                            objResWS.setIdTransaccion(asignacionInd.getiBatchProceso());
                            objResWS.setMensajeTransaccion(respuestaInd.getMensajeProceso());
                            objResWS.setMensajeTransaccion(respuestaInd.getMensajeProceso());
                            objResWS.setEstadoTransaccion(respuestaInd.getFlagProceso());
                            listadoResServico.add(objResWS);
                        });
                    });

                    System.out.println(UtilFabricaSOAP.llamarWSExterno(listadoResServico, "D"));
                }

            } catch (Exception ex) {
                Logger.getLogger(OperadorDespachos.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error generando en el proceso de Asignaciones: \n" + ex.toString());
                listadoResServico = new ArrayList<>();

                listadoAsignacion.stream().map((asignacionInd) -> {
                    objResWS = new ResTransaccionesWSEX();
                    return asignacionInd;
                }).map((asignacionInd) -> {
                    objResWS.setIdLog(idLog);
                    objResWS.setNumeroId(asignacionInd.getIdAsigancion());
                    return asignacionInd;
                }).map((asignacionInd) -> {
                    objResWS.setIdTransaccion(asignacionInd.getiBatchProceso());
                    return asignacionInd;
                }).map((_item) -> {
                    objResWS.setMensajeTransaccion("Error generando en el proceso de Asignaciones: \n" + ex.toString());
                    return _item;
                }).map((_item) -> {
                    objResWS.setEstadoTransaccion(-1);
                    return _item;
                }).forEachOrdered((_item) -> {
                    listadoResServico.add(objResWS);
                });

                System.out.println(UtilFabricaSOAP.llamarWSExterno(listadoResServico, "D"));

            } finally {
                generarAuditoria("Finalizo Proceso de Asignaciones en hilo Asincrono. Metodo: operarAsignacion");
            }
        }).start();
    }

    private String crearInsertAsignacion(ObjAsignacion asignacionInd) throws Exception {
        try {

            querySQL = " INSERT INTO COLLX835F.BFDESPA( ";
            insertSQL = " VALUES ( ";
            querySQL += " DBATCH,";
            insertSQL += " " + asignacionInd.getiBatchProceso() + ",";     //P     DBATCH
            querySQL += " DPEDID,";
            insertSQL += " " + asignacionInd.getPedidoProceso() + ",";     //P     DPEDID
            querySQL += " DID,";
            insertSQL += " " + asignacionInd.getIdAsigancion() + ",";     //P     DPEDID
            querySQL += " DLINEA,";
            insertSQL += " " + asignacionInd.getLineaProceso() + ",";     //P     DLINEA
            querySQL += " DPRODU,";
            insertSQL += "'" + asignacionInd.getProductoProceso() + "',";     //A     DPRODU
            querySQL += " DCANTI,";
            insertSQL += " " + asignacionInd.getCantidadProceso() + ",";     //P     DCANTI
            querySQL += " DBODEG,";
            insertSQL += "'" + asignacionInd.getBodegaProceso() + "',";     //A     DBODEG
            querySQL += " DLOCAL,";
            insertSQL += "'" + asignacionInd.getLocalProceso() + "',";     //A     DLOCAL
            querySQL += " DLOTE,";
            insertSQL += "'" + asignacionInd.getLoteProceso() + "',";     //A     DLOTE                        
            querySQL += " DUSER,";
            insertSQL += "'" + asignacionInd.getUsuarioProceso() + "',";     //A     DUSER
            querySQL += " DFECHA,";
            insertSQL += "'" + asignacionInd.getFechaProceso() + "',";     //A     DFECHA
            querySQL += " DTIPO,";
            insertSQL += "'" + asignacionInd.getTipoProceso() + "',";     //A     DTIPO
            querySQL += " DFLAG";
            insertSQL += " " + asignacionInd.getFlagProceso() + "";     //P     DFLAG
            querySQL += ") ";
            insertSQL += ")";

        } catch (Exception ex) {
            Logger.getLogger(OperadorDespachos.class.getName()).log(Level.SEVERE, null, ex);
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
