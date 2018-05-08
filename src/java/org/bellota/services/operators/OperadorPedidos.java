/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.services.operators;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bellota.as400.operators.OperadorAS400;
import org.bellota.as400.operators.OperadorLXConectors;
import org.bellota.util.UtilFabricaSOAP;
import org.bellota.util.objects.WSResponse.pedidos.ObjDetallePedido;
import org.bellota.util.objects.WSRequest.ReqPedidosWS;
import org.bellota.util.objects.WSResponse.pedidos.ObjLineaPedido;
import org.bellota.util.objects.WSResponse.pedidos.ObjOrdenPedido;
import org.bellota.util.objects.WSResponse.pedidos.ResPedidosWS;

/**
 *
 * @author Cristian Alberto Perez
 */
public class OperadorPedidos {

    private final OperadorLXConectors operadorLX = new OperadorLXConectors();
    private final OperadorAS400 operadorAS400 = new OperadorAS400();

    // Objetos de Operacion del Servicio
    private ResPedidosWS objResPedidos;
    private ObjOrdenPedido ordenPedido;
    private ObjLineaPedido objLineaPedido;
    private ObjLineaPedido resultadoLineas;

    private ReqPedidosWS pedidoNuevo;

    // Objetos de Resultado del Servicio
    private List<ObjOrdenPedido> listadoOrdenPedido;
    private List<ObjDetallePedido> detallePedido;
    private List<ReqPedidosWS> pedidosConector;
    private List<ObjLineaPedido> listadoLienasOrden;
    private List<ResPedidosWS> resultadoPedidos;

    private final SimpleDateFormat formatoFechaNumerico = new SimpleDateFormat("yyyyMMdd");
    private String insertSQL, querySQL = "", bodegaPedido, resultadoLX;

    /**
     *
     * @param pedidoSolicitado
     * @param idLog
     * @return
     */
    public void operarCrecionPedido(ReqPedidosWS pedidoSolicitado, String idLog) {
        new Thread(() -> {
            try {
                generarAuditoria("Inicio Proceso de Creación de Pedido Sincrono. Metodo: operarCrecionPedido");
                objResPedidos = new ResPedidosWS();
                listadoOrdenPedido = new ArrayList<>();
                pedidosConector = new ArrayList<>();
                listadoLienasOrden = new ArrayList<>();

                // Conexion con LX
                operadorLX.obtenerConexionLX();

                detallePedido = pedidoSolicitado.getDetallePedido();

                for (ObjDetallePedido detalleInd : detallePedido) {

                    bodegaPedido = operadorAS400.obtenerBodega(operadorAS400.obtenerConexion(), detalleInd.getCodigoItem(), pedidoSolicitado.getCodigoCliente());
                    pedidoSolicitado.setBodegaPedido(bodegaPedido.trim());

                    operadorAS400.insertAsync400(generarInsertPedido(pedidoSolicitado, detalleInd, idLog), operadorAS400.obtenerConexion());

                    if (pedidosConector.isEmpty()) {
                        pedidoNuevo = new ReqPedidosWS();
                        pedidoNuevo.setBodegaPedido(pedidoSolicitado.getBodegaPedido());
                        pedidoNuevo.setCodigoCliente(pedidoSolicitado.getCodigoCliente());
                        pedidoNuevo.setCodigoEnvio(pedidoSolicitado.getCodigoEnvio());
                        pedidoNuevo.setCodigoOrdenCompra(pedidoSolicitado.getCodigoOrdenCompra());
                        List<ObjDetallePedido> listadoNuevo = new ArrayList<>();
                        listadoNuevo.add(detalleInd);
                        pedidoNuevo.setDetallePedido(listadoNuevo);
                        pedidoNuevo.setFechaSolicitudEnvio(pedidoSolicitado.getFechaSolicitudEnvio());
                        pedidoNuevo.setFechaUsuario1(pedidoSolicitado.getFechaUsuario1());
                        pedidoNuevo.setFechaUsuario2(pedidoSolicitado.getFechaUsuario2());
                        pedidoNuevo.setIdContrato(pedidoSolicitado.getIdContrato());
                        pedidoNuevo.setNombreOperacion(pedidoSolicitado.getNombreOperacion());
                        pedidoNuevo.setNotaOrdenCompra(pedidoSolicitado.getNotaOrdenCompra());
                        pedidoNuevo.setCodigoPedido(pedidoSolicitado.getCodigoPedido());
                        pedidosConector.add(pedidoNuevo);
                    } else {
                        if (buscarPedidoLX(pedidosConector, bodegaPedido.trim(), detalleInd)) {
                            pedidoNuevo = new ReqPedidosWS();
                            pedidoNuevo.setBodegaPedido(pedidoSolicitado.getBodegaPedido());
                            pedidoNuevo.setCodigoCliente(pedidoSolicitado.getCodigoCliente());
                            pedidoNuevo.setCodigoEnvio(pedidoSolicitado.getCodigoEnvio());
                            pedidoNuevo.setCodigoOrdenCompra(pedidoSolicitado.getCodigoOrdenCompra());
                            List<ObjDetallePedido> listadoNuevo = new ArrayList<>();
                            listadoNuevo.add(detalleInd);
                            pedidoNuevo.setDetallePedido(listadoNuevo);
                            pedidoNuevo.setFechaSolicitudEnvio(pedidoSolicitado.getFechaSolicitudEnvio());
                            pedidoNuevo.setFechaUsuario1(pedidoSolicitado.getFechaUsuario1());
                            pedidoNuevo.setFechaUsuario2(pedidoSolicitado.getFechaUsuario2());
                            pedidoNuevo.setIdContrato(pedidoSolicitado.getIdContrato());
                            pedidoNuevo.setNombreOperacion(pedidoSolicitado.getNombreOperacion());
                            pedidoNuevo.setNotaOrdenCompra(pedidoSolicitado.getNotaOrdenCompra());
                            pedidoNuevo.setCodigoPedido(pedidoSolicitado.getCodigoPedido());
                            pedidosConector.add(pedidoNuevo);
                        }
                    }
                }

                pedidosConector.stream().map((pedidosLX) -> {
                    ordenPedido = new ObjOrdenPedido();
                    listadoLienasOrden = new ArrayList<>();
                    return pedidosLX;
                }).map((pedidosLX) -> {
                    resultadoLX = operadorLX.crearPedido(pedidosLX, idLog, pedidoSolicitado.getIdContrato());
                    return pedidosLX;
                }).map((pedidosLX) -> {
                    if (resultadoLX.contains("OK")) {
                        detallePedido = pedidosLX.getDetallePedido();
                        detallePedido.stream().map((detalleInd) -> {
                            objLineaPedido = new ObjLineaPedido();
                            return detalleInd;
                        }).map((detalleInd) -> {
                            objLineaPedido.setLineErrCode("1");
                            return detalleInd;
                        }).map((detalleInd) -> {
                            objLineaPedido.setLineErrDescription(resultadoLX);
                            return detalleInd;
                        }).map((detalleInd) -> {
                            objLineaPedido.setDocLineNumber(detalleInd.getDocumentoNumeroLinea());
                            return detalleInd;
                        }).forEachOrdered((detalleInd) -> {
                            objLineaPedido.setOrderLineNumber(detalleInd.getNumeroLineaOrden());
                            listadoLienasOrden.add(objLineaPedido);
                            generarUpdatePedidos(objLineaPedido, idLog);
                        });
                        ordenPedido.setErrCode("1");
                        ordenPedido.setErrDescription(resultadoLX);
                    } else {
                        detallePedido = pedidosLX.getDetallePedido();
                        detallePedido.stream().map((detalleInd) -> {
                            objLineaPedido = new ObjLineaPedido();
                            return detalleInd;
                        }).map((detalleInd) -> {
                            objLineaPedido.setLineErrCode("-1");
                            return detalleInd;
                        }).map((detalleInd) -> {
                            objLineaPedido.setLineErrDescription(resultadoLX);
                            return detalleInd;
                        }).map((detalleInd) -> {
                            objLineaPedido.setDocLineNumber(detalleInd.getDocumentoNumeroLinea());
                            return detalleInd;
                        }).forEachOrdered((detalleInd) -> {
                            objLineaPedido.setOrderLineNumber(detalleInd.getNumeroLineaOrden());
                            listadoLienasOrden.add(objLineaPedido);
                            generarUpdatePedidos(objLineaPedido, idLog);
                        });
                        ordenPedido.setErrCode("-1");
                        ordenPedido.setErrDescription("Se ha generado algun Error en alguna de las Lineas. Porfavor verificar el Listado de Lineas de esta Orden.");
                    }
                    return pedidosLX;
                }).map((pedidosLX) -> {
                    ordenPedido.setTotalLines(String.valueOf(pedidosLX.getDetallePedido().size()));
                    return pedidosLX;
                }).map((pedidosLX) -> {
                    ordenPedido.setOrderNumber(pedidosLX.getCodigoPedido());
                    return pedidosLX;
                }).map((pedidosLX) -> {
                    ordenPedido.setLines(listadoLienasOrden);
                    return pedidosLX;
                }).forEachOrdered((pedidosLX) -> {
                    listadoOrdenPedido.add(ordenPedido);
                });

                objResPedidos.setBidContractId(idLog);
                objResPedidos.setTotalOrders(String.valueOf(pedidosConector.size()));
                objResPedidos.setOrders(listadoOrdenPedido);

                resultadoPedidos = new ArrayList<>();
                resultadoPedidos.add(objResPedidos);
                System.out.println(UtilFabricaSOAP.llamarWSExterno(resultadoPedidos, "P"));

            } catch (Exception ex) {

                Logger.getLogger(OperadorPedidos.class.getName()).log(Level.SEVERE, null, ex);

                listadoOrdenPedido = new ArrayList<>();
                objResPedidos = new ResPedidosWS();
                listadoLienasOrden = new ArrayList<>();

                ordenPedido = new ObjOrdenPedido();
                ordenPedido.setTotalLines(String.valueOf(pedidoSolicitado.getDetallePedido().size()));
                ordenPedido.setOrderNumber(pedidoSolicitado.getCodigoPedido());
                ordenPedido.setErrCode("-1");
                ordenPedido.setErrDescription("Error procesando este Pedido - " + ex.toString());

                detallePedido = pedidoSolicitado.getDetallePedido();
                detallePedido.stream().map((detalleInd) -> {
                    objLineaPedido = new ObjLineaPedido();
                    return detalleInd;
                }).map((detalleInd) -> {
                    objLineaPedido.setLineErrCode("-1");
                    return detalleInd;
                }).map((detalleInd) -> {
                    objLineaPedido.setLineErrDescription("Error procesando este Pedido - " + ex.toString());
                    return detalleInd;
                }).map((detalleInd) -> {
                    objLineaPedido.setDocLineNumber(detalleInd.getDocumentoNumeroLinea());
                    return detalleInd;
                }).forEachOrdered((detalleInd) -> {
                    objLineaPedido.setOrderLineNumber(detalleInd.getLineaOperacion());
                    listadoLienasOrden.add(objLineaPedido);
                });

                ordenPedido.setLines(listadoLienasOrden);
                listadoOrdenPedido.add(ordenPedido);

                objResPedidos.setBidContractId(idLog);
                objResPedidos.setOrders(listadoOrdenPedido);
                objResPedidos.setTotalOrders(String.valueOf(listadoOrdenPedido.size()));

                resultadoPedidos = new ArrayList<>();
                resultadoPedidos.add(objResPedidos);
                System.out.println(UtilFabricaSOAP.llamarWSExterno(resultadoPedidos, "P"));

            } finally {
                if (operadorAS400.esConexionAbierta()) {
                    operadorAS400.cerrarConexion();
                }
                if (operadorLX.esConexionAbierta()) {
                    operadorLX.cerrarConexionLX();
                }
                generarAuditoria("Finalizo Proceso de Creación de Pedido Sincrono. Metodo: operarCrecionPedido");
            }
        }).start();
    }

    /**
     *
     * @param pedidoSolicitado
     * @param idLog
     * @return
     */
    public void operarModificacionPedido(ReqPedidosWS pedidoSolicitado, String idLog) {
        new Thread(() -> {
            try {
                generarAuditoria("Inicio Proceso de Modificación de Pedido Sincrono. Metodo: operarModificacionPedido");
                objResPedidos = new ResPedidosWS();
                listadoOrdenPedido = new ArrayList<>();
                pedidosConector = new ArrayList<>();
                listadoLienasOrden = new ArrayList<>();

                // Conexion con LX
                operadorLX.obtenerConexionLX();
                ordenPedido = new ObjOrdenPedido();
                resultadoLX = operadorLX.modificarPedido(pedidoSolicitado, idLog, pedidoSolicitado.getIdContrato());

                for (ObjDetallePedido detalleInd : pedidoSolicitado.getDetallePedido()) {
                    operadorAS400.insertAsync400(generarInsertPedido(pedidoSolicitado, detalleInd, idLog), operadorAS400.obtenerConexion());
                }

                if (resultadoLX.contains("Exitosamente")) {
                    detallePedido = pedidoSolicitado.getDetallePedido();
                    detallePedido.stream().map((detalleInd) -> {
                        objLineaPedido = new ObjLineaPedido();
                        return detalleInd;
                    }).map((detalleInd) -> {
                        objLineaPedido.setLineErrCode("1");
                        return detalleInd;
                    }).map((detalleInd) -> {
                        objLineaPedido.setLineErrDescription(resultadoLX);
                        return detalleInd;
                    }).map((detalleInd) -> {
                        objLineaPedido.setDocLineNumber(detalleInd.getDocumentoNumeroLinea());
                        return detalleInd;
                    }).forEachOrdered((detalleInd) -> {
                        objLineaPedido.setOrderLineNumber(detalleInd.getLineaOperacion());
                    });
                    ordenPedido.setErrCode("1");
                    ordenPedido.setErrDescription(resultadoLX);
                    listadoLienasOrden.add(objLineaPedido);
                } else {
                    detallePedido = pedidoSolicitado.getDetallePedido();
                    detallePedido.stream().map((detalleInd) -> {
                        objLineaPedido = new ObjLineaPedido();
                        return detalleInd;
                    }).map((detalleInd) -> {
                        objLineaPedido.setLineErrCode("-1");
                        return detalleInd;
                    }).map((detalleInd) -> {
                        objLineaPedido.setLineErrDescription(resultadoLX);
                        return detalleInd;
                    }).map((detalleInd) -> {
                        objLineaPedido.setDocLineNumber(detalleInd.getDocumentoNumeroLinea());
                        return detalleInd;
                    }).forEachOrdered((detalleInd) -> {
                        objLineaPedido.setOrderLineNumber(detalleInd.getLineaOperacion());
                    });

                    listadoLienasOrden.add(objLineaPedido);
                    ordenPedido.setErrCode("-1");
                    ordenPedido.setErrDescription("Se ha generado algun Error en alguna de las Lineas. Porfavor verificar el Listado de Lineas de esta Orden.");
                }

                for (ObjLineaPedido lineaInd : listadoLienasOrden) {
                    operadorAS400.insertAsync400(generarUpdatePedidos(lineaInd, idLog), operadorAS400.obtenerConexion());
                }

                ordenPedido.setTotalLines(String.valueOf(pedidoSolicitado.getDetallePedido().size()));
                ordenPedido.setOrderNumber(pedidoSolicitado.getCodigoPedido());
                ordenPedido.setLines(listadoLienasOrden);

                listadoOrdenPedido.add(ordenPedido);

                objResPedidos.setBidContractId("1");
                objResPedidos.setTotalOrders(String.valueOf(pedidosConector.size()));
                objResPedidos.setOrders(listadoOrdenPedido);

                resultadoPedidos = new ArrayList<>();
                resultadoPedidos.add(objResPedidos);
                System.out.println(UtilFabricaSOAP.llamarWSExterno(resultadoPedidos, "P"));

            } catch (Exception ex) {

                Logger.getLogger(OperadorPedidos.class.getName()).log(Level.SEVERE, null, ex);

                listadoOrdenPedido = new ArrayList<>();
                objResPedidos = new ResPedidosWS();
                listadoLienasOrden = new ArrayList<>();

                ordenPedido = new ObjOrdenPedido();
                ordenPedido.setTotalLines(String.valueOf(pedidoSolicitado.getDetallePedido().size()));
                ordenPedido.setOrderNumber(pedidoSolicitado.getCodigoPedido());
                ordenPedido.setErrCode("-1");
                ordenPedido.setErrDescription("Error procesando este Pedido - " + ex.toString());

                detallePedido = pedidoSolicitado.getDetallePedido();
                detallePedido.stream().map((detalleInd) -> {
                    objLineaPedido = new ObjLineaPedido();
                    return detalleInd;
                }).map((detalleInd) -> {
                    objLineaPedido.setLineErrCode("-1");
                    return detalleInd;
                }).map((detalleInd) -> {
                    objLineaPedido.setLineErrDescription("Error procesando este Pedido - " + ex.toString());
                    return detalleInd;
                }).map((detalleInd) -> {
                    objLineaPedido.setDocLineNumber(detalleInd.getDocumentoNumeroLinea());
                    return detalleInd;
                }).forEachOrdered((detalleInd) -> {
                    objLineaPedido.setOrderLineNumber(detalleInd.getLineaOperacion());
                    listadoLienasOrden.add(objLineaPedido);
                });

                ordenPedido.setLines(listadoLienasOrden);
                listadoOrdenPedido.add(ordenPedido);

                objResPedidos.setBidContractId(idLog);
                objResPedidos.setOrders(listadoOrdenPedido);
                objResPedidos.setTotalOrders(String.valueOf(listadoOrdenPedido.size()));

                resultadoPedidos = new ArrayList<>();
                resultadoPedidos.add(objResPedidos);
                System.out.println(UtilFabricaSOAP.llamarWSExterno(resultadoPedidos, "P"));

            } finally {
                if (operadorLX.esConexionAbierta()) {
                    operadorLX.cerrarConexionLX();
                }
                generarAuditoria("Finaliza Proceso de Modificación de Pedido Sincrono. Metodo: operarModificacionPedido");
            }
        }).start();
    }

    /**
     *
     * @param codigoPedido
     * @param codigoCliente
     * @param iBatch
     * @param idContrato
     * @return
     */
    public void operarEliminacionPedido(String codigoPedido, String codigoCliente, String iBatch, String idContrato) {
        new Thread(() -> {
            try {

                generarAuditoria("Inicio Proceso de Eliminación de Pedido Sincrono. Metodo: operarEliminacionPedido");
                objResPedidos = new ResPedidosWS();
                listadoOrdenPedido = new ArrayList<>();
                pedidosConector = new ArrayList<>();
                listadoLienasOrden = new ArrayList<>();

                // Conexion con LX
                operadorLX.obtenerConexionLX();
                ordenPedido = new ObjOrdenPedido();
                resultadoLX = operadorLX.eliminarPedido(codigoPedido, codigoCliente, iBatch, idContrato);

                if (resultadoLX.contains("Exitosamente")) {
                    objLineaPedido = new ObjLineaPedido();
                    objLineaPedido.setLineErrCode("1");
                    objLineaPedido.setLineErrDescription(resultadoLX);
                    objLineaPedido.setDocLineNumber("0");
                    objLineaPedido.setOrderLineNumber("0");
                    ordenPedido.setErrCode("1");
                    ordenPedido.setErrDescription(resultadoLX);
                    listadoLienasOrden.add(objLineaPedido);
                } else {
                    objLineaPedido = new ObjLineaPedido();
                    objLineaPedido.setLineErrCode("-1");
                    objLineaPedido.setLineErrDescription(resultadoLX);
                    objLineaPedido.setDocLineNumber("0");
                    objLineaPedido.setOrderLineNumber("0");
                    ordenPedido.setErrCode("-1");
                    ordenPedido.setErrDescription(resultadoLX);
                    listadoLienasOrden.add(objLineaPedido);
                    ordenPedido.setErrCode("-1");
                    ordenPedido.setErrDescription(resultadoLX);
                }

                ordenPedido.setTotalLines("1");
                ordenPedido.setOrderNumber(codigoPedido);
                ordenPedido.setLines(listadoLienasOrden);

                listadoOrdenPedido.add(ordenPedido);

                objResPedidos.setBidContractId("1");
                objResPedidos.setTotalOrders(String.valueOf(pedidosConector.size()));
                objResPedidos.setOrders(listadoOrdenPedido);

                resultadoPedidos = new ArrayList<>();
                resultadoPedidos.add(objResPedidos);
                System.out.println(UtilFabricaSOAP.llamarWSExterno(resultadoPedidos, "P"));

            } catch (Exception ex) {

                Logger.getLogger(OperadorPedidos.class.getName()).log(Level.SEVERE, null, ex);

                ordenPedido = new ObjOrdenPedido();

                ordenPedido.setTotalLines("-1");
                ordenPedido.setOrderNumber(codigoPedido);
                ordenPedido.setErrCode("-1");
                ordenPedido.setErrDescription("Error procesando este Pedido - " + ex.toString());

                objLineaPedido = new ObjLineaPedido();
                objLineaPedido.setLineErrCode("-1");
                objLineaPedido.setLineErrDescription(resultadoLX);
                objLineaPedido.setDocLineNumber("0");
                objLineaPedido.setOrderLineNumber("0");
                listadoLienasOrden.add(objLineaPedido);

                ordenPedido.setLines(listadoLienasOrden);

                listadoOrdenPedido = new ArrayList<>();
                listadoOrdenPedido.add(ordenPedido);

                objResPedidos = new ResPedidosWS();
                objResPedidos.setBidContractId("-1");
                objResPedidos.setTotalOrders("1");
                objResPedidos.setOrders(listadoOrdenPedido);

                resultadoPedidos = new ArrayList<>();
                resultadoPedidos.add(objResPedidos);
                System.out.println(UtilFabricaSOAP.llamarWSExterno(resultadoPedidos, "P"));

            } finally {
                if (operadorLX.esConexionAbierta()) {
                    operadorLX.cerrarConexionLX();
                }
                generarAuditoria("Finalizo Proceso de Eliminación de Pedido Sincrono. Metodo: operarEliminacionPedido");
            }
        }).start();
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
    public void operarAdicionLinea(List<ObjDetallePedido> listadoLineas, String codigoPedido, String codigoCliente, String idContrato, String idLog) {
        new Thread(() -> {
            try {
                generarAuditoria("Inicio Proceso de Adicion de Linea Sincrono. Metodo: operarAdicionLinea");

                operadorLX.obtenerConexionLX();
                resultadoLX = operadorLX.agregarLineaPedido(listadoLineas, codigoPedido, codigoCliente, idContrato, idLog);

                listadoLienasOrden = new ArrayList<>();

                pedidoNuevo = new ReqPedidosWS();
                pedidoNuevo.setCodigoCliente(codigoCliente);
                pedidoNuevo.setDetallePedido(listadoLineas);
                pedidoNuevo.setCodigoPedido(codigoPedido);
                pedidoNuevo.setIdContrato(idContrato);
                pedidoNuevo.setBodegaPedido("");
                pedidoNuevo.setCodigoEnvio("");
                pedidoNuevo.setCodigoOrdenCompra("");
                pedidoNuevo.setFechaSolicitudEnvio("");
                pedidoNuevo.setFechaUsuario1("");
                pedidoNuevo.setFechaUsuario2("");
                pedidoNuevo.setNombreOperacion("");
                pedidoNuevo.setNotaOrdenCompra("");

                for (ObjDetallePedido detalleInd : listadoLineas) {
                    operadorAS400.insertAsync400(generarInsertPedido(pedidoNuevo, detalleInd, idLog), operadorAS400.obtenerConexion());
                }

                listadoLineas.stream().map((detalleInd) -> {
                    resultadoLineas = new ObjLineaPedido();
                    return detalleInd;
                }).map((detalleInd) -> {
                    if (resultadoLX.contains("Exitosamente")) {
                        resultadoLineas.setLineErrCode("1");
                    } else {
                        resultadoLineas.setLineErrCode("-1");
                    }
                    return detalleInd;
                }).map((detalleInd) -> {
                    resultadoLineas.setDocLineNumber(detalleInd.getDocumentoNumeroLinea());
                    return detalleInd;
                }).map((detalleInd) -> {
                    resultadoLineas.setOrderLineNumber(detalleInd.getLineaOperacion());
                    return detalleInd;
                }).map((detalleInd) -> {
                    resultadoLineas.setLineErrDescription(resultadoLX);
                    return detalleInd;
                }).map((detalleInd) -> {
                    resultadoLineas.setDocLineNumber(detalleInd.getLineaOperacion());
                    return detalleInd;
                }).forEachOrdered((_item) -> {
                    listadoLienasOrden.add(resultadoLineas);
                });

                ordenPedido = new ObjOrdenPedido();

                if (resultadoLX.contains("Exitosamente")) {
                    ordenPedido.setErrCode("1");
                } else {
                    ordenPedido.setErrCode("-1");
                }

                ordenPedido.setErrDescription(resultadoLX);
                ordenPedido.setOrderNumber(codigoPedido);
                ordenPedido.setTotalLines(String.valueOf(listadoLineas.size()));
                ordenPedido.setLines(listadoLienasOrden);

                for (ObjLineaPedido lineaInd : listadoLienasOrden) {
                    operadorAS400.insertAsync400(generarUpdatePedidos(lineaInd, idLog), operadorAS400.obtenerConexion());
                }

                listadoOrdenPedido = new ArrayList<>();
                listadoOrdenPedido.add(ordenPedido);

                objResPedidos = new ResPedidosWS();
                objResPedidos.setBidContractId(idContrato);
                objResPedidos.setTotalOrders("1");
                objResPedidos.setOrders(listadoOrdenPedido);

                resultadoPedidos = new ArrayList<>();
                resultadoPedidos.add(objResPedidos);
                System.out.println(UtilFabricaSOAP.llamarWSExterno(resultadoPedidos, "P"));

            } catch (Exception ex) {
                Logger.getLogger(OperadorPedidos.class.getName()).log(Level.SEVERE, null, ex);

                listadoLineas.stream().map((detalleInd) -> {
                    resultadoLineas = new ObjLineaPedido();
                    return detalleInd;
                }).map((detalleInd) -> {
                    resultadoLineas.setLineErrCode("-1");
                    return detalleInd;
                }).map((detalleInd) -> {
                    resultadoLineas.setDocLineNumber(detalleInd.getDocumentoNumeroLinea());
                    return detalleInd;
                }).map((detalleInd) -> {
                    resultadoLineas.setOrderLineNumber(detalleInd.getLineaOperacion());
                    return detalleInd;
                }).map((detalleInd) -> {
                    resultadoLineas.setLineErrDescription(ex.toString());
                    return detalleInd;
                }).map((detalleInd) -> {
                    resultadoLineas.setDocLineNumber(detalleInd.getLineaOperacion());
                    return detalleInd;
                }).forEachOrdered((detalleInd) -> {
                    listadoLienasOrden.add(resultadoLineas);
                });

                ordenPedido = new ObjOrdenPedido();
                ordenPedido.setErrCode("-1");
                ordenPedido.setErrDescription(ex.toString());
                ordenPedido.setOrderNumber(codigoPedido);
                ordenPedido.setTotalLines(String.valueOf(listadoLineas.size()));
                ordenPedido.setLines(listadoLienasOrden);

                listadoOrdenPedido = new ArrayList<>();
                listadoOrdenPedido.add(ordenPedido);

                objResPedidos = new ResPedidosWS();
                objResPedidos.setBidContractId(idContrato);
                objResPedidos.setTotalOrders("1");
                objResPedidos.setOrders(listadoOrdenPedido);

                resultadoPedidos = new ArrayList<>();
                resultadoPedidos.add(objResPedidos);
                System.out.println(UtilFabricaSOAP.llamarWSExterno(resultadoPedidos, "P"));

            } finally {
                if (operadorLX.esConexionAbierta()) {
                    operadorLX.cerrarConexionLX();
                }
                generarAuditoria("Finalizo Proceso de Adicion de Linea Sincrono. Metodo: operarAdicionLinea");
            }
        }).start();
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
    public void operarModificacionLinea(List<ObjDetallePedido> listadoLineas, String codigoPedido, String codigoCliente, String idContrato, String idLog) {
        new Thread(() -> {
            try {
                generarAuditoria("Inicio Proceso de Modificación de Linea Sincrono. Metodo: operarModificacionLinea");

                operadorLX.obtenerConexionLX();
                resultadoLX = operadorLX.modificarLineaPedido(listadoLineas, codigoPedido, codigoCliente, idContrato, idLog);

                listadoLienasOrden = new ArrayList<>();

                pedidoNuevo = new ReqPedidosWS();
                pedidoNuevo.setCodigoCliente(codigoCliente);
                pedidoNuevo.setDetallePedido(listadoLineas);
                pedidoNuevo.setCodigoPedido(codigoPedido);
                pedidoNuevo.setIdContrato(idContrato);
                pedidoNuevo.setBodegaPedido("");
                pedidoNuevo.setCodigoEnvio("");
                pedidoNuevo.setCodigoOrdenCompra("");
                pedidoNuevo.setFechaSolicitudEnvio("");
                pedidoNuevo.setFechaUsuario1("");
                pedidoNuevo.setFechaUsuario2("");
                pedidoNuevo.setNombreOperacion("");
                pedidoNuevo.setNotaOrdenCompra("");

                for (ObjDetallePedido detalleInd : listadoLineas) {
                    operadorAS400.insertAsync400(generarInsertPedido(pedidoNuevo, detalleInd, idLog), operadorAS400.obtenerConexion());
                }

                listadoLineas.stream().map((detalleInd) -> {
                    resultadoLineas = new ObjLineaPedido();
                    return detalleInd;
                }).map((detalleInd) -> {
                    if (resultadoLX.contains("Exitosamente")) {
                        resultadoLineas.setLineErrCode("1");
                    } else {
                        resultadoLineas.setLineErrCode("-1");
                    }
                    return detalleInd;
                }).map((detalleInd) -> {
                    resultadoLineas.setDocLineNumber(detalleInd.getDocumentoNumeroLinea());
                    return detalleInd;
                }).map((detalleInd) -> {
                    resultadoLineas.setOrderLineNumber(detalleInd.getLineaOperacion());
                    return detalleInd;
                }).map((detalleInd) -> {
                    resultadoLineas.setLineErrDescription(resultadoLX);
                    return detalleInd;
                }).map((detalleInd) -> {
                    resultadoLineas.setDocLineNumber(detalleInd.getLineaOperacion());
                    return detalleInd;
                }).forEachOrdered((detalleInd) -> {
                    listadoLienasOrden.add(resultadoLineas);
                });

                ordenPedido = new ObjOrdenPedido();

                if (resultadoLX.contains("Exitosamente")) {
                    ordenPedido.setErrCode("1");
                } else {
                    ordenPedido.setErrCode("-1");
                }

                ordenPedido.setErrDescription(resultadoLX);
                ordenPedido.setOrderNumber(codigoPedido);
                ordenPedido.setTotalLines(String.valueOf(listadoLineas.size()));
                ordenPedido.setLines(listadoLienasOrden);

                for (ObjLineaPedido lineaInd : listadoLienasOrden) {
                    operadorAS400.insertAsync400(generarUpdatePedidos(lineaInd, idLog), operadorAS400.obtenerConexion());
                }

                listadoOrdenPedido = new ArrayList<>();
                listadoOrdenPedido.add(ordenPedido);

                objResPedidos = new ResPedidosWS();
                objResPedidos.setBidContractId(idContrato);
                objResPedidos.setTotalOrders("1");
                objResPedidos.setOrders(listadoOrdenPedido);

                resultadoPedidos = new ArrayList<>();
                resultadoPedidos.add(objResPedidos);
                System.out.println(UtilFabricaSOAP.llamarWSExterno(resultadoPedidos, "P"));

            } catch (Exception ex) {

                Logger.getLogger(OperadorPedidos.class.getName()).log(Level.SEVERE, null, ex);

                listadoLineas.stream().map((detalleInd) -> {
                    resultadoLineas = new ObjLineaPedido();
                    return detalleInd;
                }).map((detalleInd) -> {
                    resultadoLineas.setLineErrCode("-1");
                    return detalleInd;
                }).map((detalleInd) -> {
                    resultadoLineas.setDocLineNumber(detalleInd.getDocumentoNumeroLinea());
                    return detalleInd;
                }).map((detalleInd) -> {
                    resultadoLineas.setOrderLineNumber(detalleInd.getLineaOperacion());
                    return detalleInd;
                }).map((detalleInd) -> {
                    resultadoLineas.setLineErrDescription(ex.toString());
                    return detalleInd;
                }).map((detalleInd) -> {
                    resultadoLineas.setDocLineNumber(detalleInd.getLineaOperacion());
                    return detalleInd;
                }).forEachOrdered((_item) -> {
                    listadoLienasOrden.add(resultadoLineas);
                });

                ordenPedido = new ObjOrdenPedido();
                ordenPedido.setErrCode("-1");
                ordenPedido.setErrDescription(ex.toString());
                ordenPedido.setOrderNumber(codigoPedido);
                ordenPedido.setTotalLines(String.valueOf(listadoLineas.size()));
                ordenPedido.setLines(listadoLienasOrden);

                listadoOrdenPedido = new ArrayList<>();
                listadoOrdenPedido.add(ordenPedido);

                objResPedidos = new ResPedidosWS();
                objResPedidos.setBidContractId(idContrato);
                objResPedidos.setTotalOrders("1");
                objResPedidos.setOrders(listadoOrdenPedido);

                resultadoPedidos = new ArrayList<>();
                resultadoPedidos.add(objResPedidos);
                System.out.println(UtilFabricaSOAP.llamarWSExterno(resultadoPedidos, "P"));

            } finally {
                if (operadorLX.esConexionAbierta()) {
                    operadorLX.cerrarConexionLX();
                }
                generarAuditoria("Finalizo Proceso de Modificación de Linea Sincrono. Metodo: operarModificacionLinea");
            }
        }).start();
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
    public void operarEliminacionLinea(List<ObjDetallePedido> listadoLineas, String codigoPedido, String codigoCliente, String idContrato, String idLog) {
        new Thread(() -> {
            try {
                generarAuditoria("Inicio Proceso de Eliminación de Linea Sincrono. Metodo: operarEliminacionLinea");

                operadorLX.obtenerConexionLX();
                resultadoLX = operadorLX.eliminarLineaPedido(listadoLineas, codigoPedido, codigoCliente, idContrato, idLog);

                listadoLienasOrden = new ArrayList<>();

                pedidoNuevo = new ReqPedidosWS();
                pedidoNuevo.setCodigoCliente(codigoCliente);
                pedidoNuevo.setDetallePedido(listadoLineas);
                pedidoNuevo.setCodigoPedido(codigoPedido);
                pedidoNuevo.setIdContrato(idContrato);
                pedidoNuevo.setBodegaPedido("");
                pedidoNuevo.setCodigoEnvio("");
                pedidoNuevo.setCodigoOrdenCompra("");
                pedidoNuevo.setFechaSolicitudEnvio("");
                pedidoNuevo.setFechaUsuario1("");
                pedidoNuevo.setFechaUsuario2("");
                pedidoNuevo.setNombreOperacion("");
                pedidoNuevo.setNotaOrdenCompra("");

                for (ObjDetallePedido detalleInd : listadoLineas) {
                    operadorAS400.insertAsync400(generarInsertPedido(pedidoNuevo, detalleInd, idLog), operadorAS400.obtenerConexion());
                }

                listadoLineas.stream().map((detalleInd) -> {
                    resultadoLineas = new ObjLineaPedido();
                    return detalleInd;
                }).map((detalleInd) -> {
                    if (resultadoLX.contains("Exitosamente")) {
                        resultadoLineas.setLineErrCode("1");
                    } else {
                        resultadoLineas.setLineErrCode("-1");
                    }
                    return detalleInd;
                }).map((detalleInd) -> {
                    resultadoLineas.setDocLineNumber(detalleInd.getDocumentoNumeroLinea());
                    return detalleInd;
                }).map((detalleInd) -> {
                    resultadoLineas.setOrderLineNumber(detalleInd.getLineaOperacion());
                    return detalleInd;
                }).map((detalleInd) -> {
                    resultadoLineas.setLineErrDescription(resultadoLX);
                    return detalleInd;
                }).map((detalleInd) -> {
                    resultadoLineas.setDocLineNumber(detalleInd.getLineaOperacion());
                    return detalleInd;
                }).forEachOrdered((_item) -> {
                    listadoLienasOrden.add(resultadoLineas);
                });

                ordenPedido = new ObjOrdenPedido();

                if (resultadoLX.contains("Exitosamente")) {
                    ordenPedido.setErrCode("1");
                } else {
                    ordenPedido.setErrCode("-1");
                }

                ordenPedido.setErrDescription(resultadoLX);
                ordenPedido.setOrderNumber(codigoPedido);
                ordenPedido.setTotalLines(String.valueOf(listadoLineas.size()));
                ordenPedido.setLines(listadoLienasOrden);

                for (ObjLineaPedido lineaInd : listadoLienasOrden) {
                    operadorAS400.insertAsync400(generarUpdatePedidos(lineaInd, idLog), operadorAS400.obtenerConexion());
                }

                listadoOrdenPedido = new ArrayList<>();
                listadoOrdenPedido.add(ordenPedido);

                objResPedidos = new ResPedidosWS();
                objResPedidos.setBidContractId(idContrato);
                objResPedidos.setTotalOrders("1");
                objResPedidos.setOrders(listadoOrdenPedido);

                resultadoPedidos = new ArrayList<>();
                resultadoPedidos.add(objResPedidos);
                System.out.println(UtilFabricaSOAP.llamarWSExterno(resultadoPedidos, "P"));

            } catch (Exception ex) {

                Logger.getLogger(OperadorPedidos.class.getName()).log(Level.SEVERE, null, ex);

                listadoLineas.stream().map((detalleInd) -> {
                    resultadoLineas = new ObjLineaPedido();
                    return detalleInd;
                }).map((detalleInd) -> {
                    resultadoLineas.setLineErrCode("-1");
                    return detalleInd;
                }).map((detalleInd) -> {
                    resultadoLineas.setDocLineNumber(detalleInd.getDocumentoNumeroLinea());
                    return detalleInd;
                }).map((detalleInd) -> {
                    resultadoLineas.setOrderLineNumber(detalleInd.getLineaOperacion());
                    return detalleInd;
                }).map((detalleInd) -> {
                    resultadoLineas.setLineErrDescription(ex.toString());
                    return detalleInd;
                }).map((detalleInd) -> {
                    resultadoLineas.setDocLineNumber(detalleInd.getLineaOperacion());
                    return detalleInd;
                }).forEachOrdered((_item) -> {
                    listadoLienasOrden.add(resultadoLineas);
                });

                ordenPedido = new ObjOrdenPedido();
                ordenPedido.setErrCode("-1");
                ordenPedido.setErrDescription(ex.toString());
                ordenPedido.setOrderNumber(codigoPedido);
                ordenPedido.setTotalLines(String.valueOf(listadoLineas.size()));
                ordenPedido.setLines(listadoLienasOrden);

                listadoOrdenPedido = new ArrayList<>();
                listadoOrdenPedido.add(ordenPedido);

                objResPedidos = new ResPedidosWS();
                objResPedidos.setBidContractId(idContrato);
                objResPedidos.setTotalOrders("1");
                objResPedidos.setOrders(listadoOrdenPedido);

                resultadoPedidos = new ArrayList<>();
                resultadoPedidos.add(objResPedidos);
                System.out.println(UtilFabricaSOAP.llamarWSExterno(resultadoPedidos, "P"));

            } finally {
                if (operadorLX.esConexionAbierta()) {
                    operadorLX.cerrarConexionLX();
                }
                generarAuditoria("Finaliza Proceso de Eliminación de Linea Sincrono. Metodo: operarEliminacionLinea");
            }
        }).start();
    }

    // Metodos Utiles
    /**
     *
     * @param pedidosSolicitado
     * @param detalleInd
     * @param idLog
     * @return
     */
    private String generarInsertPedido(ReqPedidosWS pedidoSolicitado, ObjDetallePedido detallePedido, String idLog) throws Exception {
        try {

            querySQL = "INSERT INTO COLLX835F.RFPEDID (";
            insertSQL = "VALUES (";
            querySQL += "ABATCH, ";
            insertSQL += idLog + ", ";     //8P0   Id Batch
            querySQL += "ADOCTP, ";
            insertSQL += "'" + "W" + "', ";     //1A    Tipo W/E/P
            querySQL += "ADOCNR, ";
            insertSQL += "'" + (!pedidoSolicitado.getIdContrato().equals("") ? pedidoSolicitado.getIdContrato() : "0") + "', ";     //20A   Num Doc
            querySQL += "APO, ";
            insertSQL += "'" + (!pedidoSolicitado.getCodigoOrdenCompra().equals("") ? pedidoSolicitado.getCodigoOrdenCompra() : "0") + "', ";     //15A   Orden de Compra
            querySQL += "ANOTE, ";
            insertSQL += "'" + pedidoSolicitado.getNotaOrdenCompra() + "', ";     //150A  Nota al pedido
            querySQL += "ALINE, ";
            insertSQL += " " + detallePedido.getNumeroLineaOrden() + ", ";     //3P0   Linea del Pedido
            querySQL += "ACUST, ";
            insertSQL += " " + pedidoSolicitado.getCodigoCliente() + ", ";     //6P0   Cliente
            querySQL += "ASHIP, ";
            insertSQL += " " + (!pedidoSolicitado.getCodigoEnvio().equals("") ? pedidoSolicitado.getCodigoEnvio() : "0") + ", ";     //4P0   Punto de Envio
            querySQL += "APROD, ";
            insertSQL += "'" + detallePedido.getCodigoItem() + "', ";     //15A   Producto
            querySQL += "AQORD, ";
            insertSQL += " " + detallePedido.getCantidadOrdenada() + ", ";     //11P3  Cantidad
            querySQL += "AVALOR, ";
            insertSQL += " " + detallePedido.getPrecioItem() + ", ";     //14P4  Valor
            querySQL += "ARDTE, ";
            insertSQL += "'" + formatoFechaNumerico.format(formatoFechaNumerico.parse(pedidoSolicitado.getFechaSolicitudEnvio())) + "', ";     //8P0   Fecha de Requerido
            querySQL += "ALNOTE, ";
            insertSQL += "'" + detallePedido.getNotaLinea() + "', ";     //50A   Nota a la linea
            querySQL += "AUSER, ";
            insertSQL += "'" + "WEBPED" + "', ";     //10A   Usuario Ingreso
            querySQL += "ACRUDP, ";
            insertSQL += "'" + pedidoSolicitado.getNombreOperacion() + "', ";     //1A    CRUD Pedido
            querySQL += "ACRUDL, ";
            insertSQL += "'" + " " + "', ";     //1A    CRUD Linea
            querySQL += "AFECUSR1, ";
            insertSQL += "'" + ((!pedidoSolicitado.getFechaUsuario1().equals("0")) && (!pedidoSolicitado.getFechaUsuario1().equals("")) ? formatoFechaNumerico.format(formatoFechaNumerico.parse(pedidoSolicitado.getFechaUsuario1())) : "0") + "', ";     //8P0   Fecha Usuario 1
            querySQL += "AFECUSR2, ";
            insertSQL += "'" + ((!pedidoSolicitado.getFechaUsuario2().equals("0")) && (!pedidoSolicitado.getFechaUsuario2().equals("")) ? formatoFechaNumerico.format(formatoFechaNumerico.parse(pedidoSolicitado.getFechaUsuario2())) : "0") + "', ";     //8P0   Fecha Usuario 2
            querySQL += "ALINDOC, ";
            insertSQL += "" + detallePedido.getDocumentoNumeroLinea() + ", ";     //3P0   Linea del Doc        
            querySQL += "ABODEG";
            insertSQL += "'" + pedidoSolicitado.getBodegaPedido() + "'";     //3P0   Linea del Doc                      
            querySQL += ")";
            insertSQL += ") ";

            System.out.println("Return SQL: \n" + querySQL + insertSQL);
        } catch (ParseException ex) {
            Logger.getLogger(OperadorAS400.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception(ex);
        }
        return querySQL + insertSQL;
    }

    private String generarUpdatePedidos(ObjLineaPedido lineaPedido, String idLog) {
        try {
            querySQL = "UPDATE COLLX835F.RFPEDID SET APEDLX = '"
                    + (lineaPedido.getLineErrDescription().contains("<CustomerOrderCode>") ? lineaPedido.getLineErrDescription().substring(lineaPedido.getLineErrDescription().indexOf("<CustomerOrderCode>") + "<CustomerOrderCode>".length(), lineaPedido.getLineErrDescription().indexOf("</CustomerOrderCode>")) : "0")
                    + "', ADESER = '" + lineaPedido.getLineErrCode()
                    + "', AERROR = " + (lineaPedido.getLineErrCode().equals("-1") ? 7 : 0)
                    + " WHERE ALINDOC = '" + lineaPedido.getDocLineNumber() + "' AND ABATCH = '" + idLog + "' \n";
            System.out.println("UPDATE SQL: \n" + querySQL);
            operadorAS400.insertAsync400(querySQL, operadorAS400.obtenerConexion());
        } catch (Exception ex) {
            Logger.getLogger(OperadorAS400.class.getName()).log(Level.SEVERE, null, ex);
        }
        return querySQL;
    }

    /**
     *
     * @param pedidosEnviados
     * @param bodegaSolicitada
     * @param detallePedido
     * @return
     */
    private boolean buscarPedidoLX(List<ReqPedidosWS> pedidosEnviados, String bodegaSolicitada, ObjDetallePedido detallePedido) {
        for (ReqPedidosWS pedidInd : pedidosEnviados) {
            if (pedidInd.getBodegaPedido().equals(bodegaSolicitada)) {
                pedidInd.getDetallePedido().add(detallePedido);
                return false;
            }
        }
        return true;
    }

    public void generarAuditoria(String mensaje) {
        java.util.Date date = new java.util.Date();
        SimpleDateFormat formato = new SimpleDateFormat("hh:mm:ss:mmmm");
        System.out.println(mensaje + " - Hora: " + formato.format(date));
    }

}
