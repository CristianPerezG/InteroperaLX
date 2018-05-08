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
import org.bellota.util.objects.WSRequest.ReqTransaccionesWS;
import org.bellota.util.objects.WSResponse.ResTransaccionesWSEX;

/**
 *
 * @author Cristian Alberto Perez
 */
public class OperadorTransacciones {

    private final OperadorLXConectors operadorLX = new OperadorLXConectors();
    private ResTransaccionesWSEX objResWS;
    private List<ResTransaccionesWSEX> listadoResServico;
    private String resultadoProceso = "", resultadoActualizaLote;
    private final OperadorAS400 operadorAS400 = new OperadorAS400();

    public void procesarTransaccionInv(final List<ReqTransaccionesWS> listadoProceso, final String iBatch) {
        new Thread(() -> {
            try {

                generarAuditoria("Inicio Proceso de Transaccion de Inventario en Hilo Ascincrono. Metodo: procesarTransaccionInv");

                operadorLX.obtenerConexionLX();
                listadoResServico = new ArrayList<>();

                for (ReqTransaccionesWS objInd : listadoProceso) {

                    objResWS = new ResTransaccionesWSEX();

                    if (objInd.getNumeroFactura().equals("")) {
                        objInd.setNumeroFactura("NULL");
                    } else {
                        if (objInd.getNumeroFactura().length() > 8) {
                            objInd.setNumeroFactura(objInd.getNumeroFactura().substring(0, 8));
                        }
                    }

                    if (objInd.getNumeroLote() != null && !objInd.getNumeroLote().isEmpty()) {
                        resultadoActualizaLote = operadorAS400.actualizarLote(objInd.getNumeroLote());
                    } else {
                        resultadoActualizaLote = "No se Requiere Actualizar";
                    }

                    if (resultadoActualizaLote.contains("Exitosamente") || resultadoActualizaLote.equals("No se Requiere Actualizar")) {

                        resultadoProceso = operadorLX.insertarTransaccionInv(objInd, iBatch);

                        if (resultadoProceso.contains("ERROR")) {
                            objResWS.setEstadoTransaccion(7);
                        } else {
                            objResWS.setEstadoTransaccion(2);
                        }
                        objResWS.setMensajeTransaccion(resultadoProceso);

                    } else {
                        objResWS.setMensajeTransaccion(resultadoActualizaLote);
                    }
                    objResWS.setIdTransaccion(iBatch);
                    objResWS.setNumeroId(objInd.getNumeroID());
                    objResWS.setIdLog(objInd.getIdLog());
                    listadoResServico.add(objResWS);
                }

                System.out.println("Resultado de Las Transacciones: \n");

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
                System.out.println("Error generando en el proceso principal de Inventario: \n" + ex.toString());
                Logger.getLogger(OperadorTransacciones.class.getName()).log(Level.SEVERE, null, ex);
                listadoProceso.stream().map((objInd) -> {
                    objResWS = new ResTransaccionesWSEX();
                    return objInd;
                }).map((objInd) -> {
                    objResWS.setEstadoTransaccion(7);
                    return objInd;
                }).map((objInd) -> {
                    objResWS.setIdTransaccion(iBatch);
                    objResWS.setNumeroId(objInd.getNumeroID());
                    return objInd;
                }).map((objInd) -> {
                    objResWS.setMensajeTransaccion("Error generando en el proceso principal de Inventario: \n" + ex.toString());
                    return objInd;
                }).map((objInd) -> {
                    objResWS.setIdLog(objInd.getIdLog());
                    return objInd;
                }).forEachOrdered((objInd) -> {
                    listadoResServico.add(objResWS);
                });

                System.out.println(UtilFabricaSOAP.llamarWSExterno(listadoResServico, "T"));
            } finally {
                if (operadorLX.esConexionAbierta()) {
                    operadorLX.cerrarConexionLX();
                }
                generarAuditoria("Finalizo Proceso de Transaccion de Inventario en Hilo Ascincrono. Metodo: procesarTransaccionInv");
            }
        }).start();
    }

    public void procesarOtraTransaccionInv(final List<ReqTransaccionesWS> listadoProceso, final String iBatch) {
        new Thread(() -> {
            try {

                generarAuditoria("Inicio Proceso de Transaccion de Inventario en Hilo Ascincrono. Metodo: procesarOtraTransaccionInv");
                operadorLX.obtenerConexionLX();
                listadoResServico = new ArrayList<>();

                listadoProceso.stream().map((objInd) -> {
                    objResWS = new ResTransaccionesWSEX();
                    return objInd;
                }).map((objInd) -> {
                    resultadoProceso = operadorLX.insertarTransaccionInv(objInd, iBatch);
                    return objInd;
                }).map((objInd) -> {
                    if (resultadoProceso.contains("Error")) {
                        objResWS.setEstadoTransaccion(-1);
                    } else {
                        objResWS.setEstadoTransaccion(1);
                    }
                    return objInd;
                }).map((objInd) -> {
                    objResWS.setNumeroId(objInd.getNumeroID());
                    return objInd;
                }).map((objInd) -> {
                    objResWS.setIdTransaccion(iBatch);
                    return objInd;
                }).map((objInd) -> {
                    objResWS.setMensajeTransaccion(resultadoProceso);
                    return objInd;
                }).map((objInd) -> {
                    objResWS.setIdLog(objInd.getIdLog());
                    return objInd;
                }).forEachOrdered((_item) -> {
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
                System.out.println("Error generando en el proceso principal de Inventario: \n" + ex.toString());
                Logger.getLogger(OperadorTransacciones.class.getName()).log(Level.SEVERE, null, ex);
                listadoProceso.stream().map((objInd) -> {
                    objResWS = new ResTransaccionesWSEX();
                    return objInd;
                }).map((objInd) -> {
                    objResWS.setEstadoTransaccion(7);
                    return objInd;
                }).map((objInd) -> {
                    objResWS.setIdTransaccion(iBatch);
                    objResWS.setNumeroId(objInd.getNumeroID());
                    return objInd;
                }).map((objInd) -> {
                    objResWS.setMensajeTransaccion("Error generando en el proceso principal de Inventario: \n" + ex.toString());
                    return objInd;
                }).map((objInd) -> {
                    objResWS.setIdLog(objInd.getIdLog());
                    return objInd;
                }).forEachOrdered((objInd) -> {
                    listadoResServico.add(objResWS);
                });
                System.out.println(UtilFabricaSOAP.llamarWSExterno(listadoResServico, "T"));
            } finally {
                if (operadorLX.esConexionAbierta()) {
                    operadorLX.cerrarConexionLX();
                }
                generarAuditoria("Finalizo Proceso de Transaccion de Inventario en Hilo Ascincrono. Metodo: procesarOtraTransaccionInv");
            }
        }).start();
    }

    public void generarAuditoria(String mensaje) {
        java.util.Date date = new java.util.Date();
        SimpleDateFormat formato = new SimpleDateFormat("hh:mm:ss:mmmm");
        System.out.println(mensaje + " - Hora: " + formato.format(date));
    }

}
