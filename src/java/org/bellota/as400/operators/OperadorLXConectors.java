/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.as400.operators;

import org.bellota.util.objects.WSResponse.pedidos.ObjDetallePedido;
import com.infor.lx.xmg.bean.CustomerOrder;
import org.bellota.util.objects.WSRequest.ReqTransaccionesWS;
import com.infor.lx.xmg.bean.Inventory;
import com.infor.lx.xmg.bean.Item;
import com.infor.lx.xmg.bean.ItemFacility;
import com.infor.lx.xmg.bean.Line;
import com.infor.lx.xmg.bean.Note;
import com.infor.lx.xmg.bean.SOADoc;
import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bellota.as400.LXConectors.ConexionLXC;
import org.bellota.util.objects.WSRequest.ReqPedidosWS;
import org.bellota.util.objects.WSRequest.ReqMaestrosWS;

/**
 *
 * @author Cristian Alberto Perez
 */
public class OperadorLXConectors {

    private final ConexionLXC conexionLX = new ConexionLXC();

    private List<ObjDetallePedido> detallePedido = new ArrayList<>();

    private Inventory objInvConector;
    private CustomerOrder objOrdenCliente;
    private Line lineaPedido;
    private Note notaLinea;
    private Item objProducto;
    private ItemFacility objProductoCIC;

    private String resultadoProceso = "", indentificadorProceso = "", fuenteProceso = "";
    private final DateFormat formatoFecha = new SimpleDateFormat("yyyyMMdd-hhmmssSSS");

    private boolean conexionAbierta = false;

    // Metodos para Transacciones de Inventario
    public String insertarTransaccionInv(ReqTransaccionesWS objInventario, String iBatch) {
        try {
            if (iBatch != null && !iBatch.isEmpty() && objInventario.getNumeroID() != null && !objInventario.getNumeroID().isEmpty()) {
                objInvConector = new Inventory(SOADoc.Action_Post);

                if (objInventario.getTipoTransaccion().equals("T1")) {
                    indentificadorProceso = "TRAS-" + iBatch + "-" + objInventario.getNumeroID();
                    fuenteProceso = "TRAS-InteroperaLX-DespachosService-operarTraslados";
                } else {
                    indentificadorProceso = "INV-" + iBatch + "-" + objInventario.getNumeroID();
                    fuenteProceso = "INV-InteroperaLX-TransaccionesService-operarTransaccionInv";
                }

                objInvConector.setAttibute("identifier", indentificadorProceso);
                objInvConector.setAttibute("sourceName", fuenteProceso);
                objInvConector.setTxTypeCode((objInventario.getTipoTransaccion() != null ? objInventario.getTipoTransaccion() : "")); // INTRAN
                objInvConector.setInventoryitemcode((objInventario.getCodigoProducto() != null ? objInventario.getCodigoProducto() : ""));
                objInvConector.setWarehouseCode((objInventario.getCodigoBodega() != null ? objInventario.getCodigoBodega() : ""));
                objInvConector.setLocationCode((objInventario.getUbicacionProducto() != null ? objInventario.getUbicacionProducto() : ""));
                objInvConector.setTxDate((objInventario.getFechaProceso() != null ? objInventario.getFechaProceso() : ""));
                objInvConector.setTxQty((objInventario.getCantidadProducto() != null ? objInventario.getCantidadProducto() : ""));
                objInvConector.setLotCode((objInventario.getNumeroLote() != null ? objInventario.getNumeroLote() : ""));
                objInvConector.setTotalCost((objInventario.getValorProducto() != null ? objInventario.getValorProducto() : ""));
                objInvConector.setOrderRef((objInventario.getOrdenCompra() != null ? objInventario.getOrdenCompra() : ""));
                objInvConector.setComment((objInventario.getTipoTransaccion() != null ? objInventario.getTipoTransaccion() : ""));
                objInvConector.setOrderRefLine((objInventario.getLineaOrden() != null ? objInventario.getLineaOrden() : ""));
                objInvConector.setAdviceNote(objInventario.getNumeroID());
                conexionLX.obtenerConector().sendMessage(objInvConector.getDocument());
                resultadoProceso = conexionLX.obtenerConector().sreply;

                if (resultadoProceso.contains("<Error>")) {
                    resultadoProceso = resultadoProceso + "\n <Respuesta>ERROR</Respuesta>";
                } else {
                    resultadoProceso = resultadoProceso + "\n <Respuesta>OK</Respuesta>";
                }

            } else {
                resultadoProceso = "No se envia a conector - Falta numero IBATCH o numero INMLOT";
            }

        } catch (Exception ex) {
            Logger.getLogger(OperadorLXConectors.class.getName()).log(Level.SEVERE, null, ex);
            resultadoProceso = "<Respuesta>Error generado Intert con Conector: Inventario: " + ex.getMessage() + "</Respuesta>";
            System.out.println(resultadoProceso);
        }
        return resultadoProceso;
    }

    // Metodos para Procesos de Pedidos y Lineas
    public String crearPedido(ReqPedidosWS pedidoInd, String idLog, String idContrato) {
        try {
            objOrdenCliente = new CustomerOrder(SOADoc.Action_Create);
            indentificadorProceso = "PED-" + idLog + "-" + idContrato + "-" + formatoFecha.format(new Date());
            fuenteProceso = "PED-InteroperaLX-PedidosService-crearPedido";
            objOrdenCliente.setAttibute("identifier", indentificadorProceso);
            objOrdenCliente.setAttibute("sourceName", fuenteProceso);

            objOrdenCliente.setOrderClassCode("400");
            objOrdenCliente.setUserOrderTypeCode("1");
            objOrdenCliente.setCustomerCode((pedidoInd.getCodigoCliente() != null ? pedidoInd.getCodigoCliente() : ""));
            objOrdenCliente.setCustomerPurchaseOrderCode(pedidoInd.getCodigoOrdenCompra());

            // Campos No Obligatorios
            objOrdenCliente.setShipToCode((pedidoInd.getCodigoEnvio() != null ? pedidoInd.getCodigoEnvio() : ""));
            objOrdenCliente.setFirstUserDefinedDate((pedidoInd.getFechaUsuario1() != null ? pedidoInd.getFechaUsuario1() : ""));
            objOrdenCliente.setSecondUserDefinedDate((pedidoInd.getFechaUsuario2() != null ? pedidoInd.getFechaUsuario2() : ""));
            objOrdenCliente.setRequestShipDate((pedidoInd.getFechaSolicitudEnvio() != null ? pedidoInd.getFechaSolicitudEnvio() : ""));
            objOrdenCliente.setWarehouseCode(pedidoInd.getBodegaPedido());

            fuenteProceso = "INV-InteroperaLX-PedidosService-crearPedido";
            objOrdenCliente.setAttibute("sourceName", fuenteProceso);

            notaLinea = new Note(objOrdenCliente);
            notaLinea.setDescription(pedidoInd.getNotaOrdenCompra());

            detallePedido = pedidoInd.getDetallePedido();

            detallePedido.stream().map((detalleInd) -> {
                lineaPedido = new Line(objOrdenCliente);
                notaLinea = new Note(lineaPedido);
                return detalleInd;
            }).map((detalleInd) -> {
                lineaPedido.setTxCurrSellingUnitNetPrice(detalleInd.getPrecioItem());
                return detalleInd;
            }).map((detalleInd) -> {
                lineaPedido.setItemCode(detalleInd.getCodigoItem());
                return detalleInd;
            }).map((detalleInd) -> {
                lineaPedido.setOrderedSellingUnitQty(detalleInd.getCantidadOrdenada());
                return detalleInd;
            }).forEachOrdered((detalleInd) -> {
                notaLinea.setDescription(detalleInd.getNotaLinea());
                lineaPedido.setInventoryReasonCode("01");
            });
            conexionLX.obtenerConector().sendMessage(objOrdenCliente.getDocument());
            resultadoProceso = conexionLX.obtenerConector().sreply;

            if (resultadoProceso.contains("<Error>")) {
                resultadoProceso = resultadoProceso + "\n <Respuesta>ERROR</Respuesta>";
            } else {
                resultadoProceso = resultadoProceso + "\n <Respuesta>OK</Respuesta>";
            }

            System.out.println(resultadoProceso);
        } catch (Exception ex) {
            Logger.getLogger(OperadorLXConectors.class.getName()).log(Level.SEVERE, null, ex);
            resultadoProceso = "<Respuesta>Error generado Intert con Conector: Pedido: " + ex.getMessage() + "</Respuesta>";
            System.out.println(resultadoProceso);
        }
        return resultadoProceso;
    }

    public String modificarPedido(ReqPedidosWS pedidoInd, String idLog, String idContrato) {
        try {
            objOrdenCliente = new CustomerOrder(SOADoc.Action_Change);

            indentificadorProceso = "PED-" + idLog + "-" + idContrato + "-" + formatoFecha.format(new Date());
            fuenteProceso = "PED-InteroperaLX-PedidosService-modificarPedido";
            objOrdenCliente.setAttibute("identifier", indentificadorProceso);
            objOrdenCliente.setAttibute("sourceName", fuenteProceso);

            objOrdenCliente.setCustomerCode(pedidoInd.getCodigoCliente());
            objOrdenCliente.setCustomerOrderCode(pedidoInd.getCodigoOrdenCompra());
            objOrdenCliente.setWarehouseCode(pedidoInd.getBodegaPedido());
            objOrdenCliente.setShipToCode((pedidoInd.getCodigoEnvio() != null ? pedidoInd.getCodigoEnvio() : ""));
            objOrdenCliente.setFirstUserDefinedDate((pedidoInd.getFechaUsuario1() != null ? pedidoInd.getFechaUsuario1() : ""));
            objOrdenCliente.setSecondUserDefinedDate((pedidoInd.getFechaUsuario2() != null ? pedidoInd.getFechaUsuario2() : ""));
            objOrdenCliente.setRequestShipDate((pedidoInd.getFechaSolicitudEnvio() != null ? pedidoInd.getFechaSolicitudEnvio() : ""));

            conexionLX.obtenerConector().sendMessage(objOrdenCliente.getDocument());
            resultadoProceso = conexionLX.obtenerConector().sreply;

            if (resultadoProceso.contains("<Error>")) {
                resultadoProceso = resultadoProceso + "\n <Respuesta>ERROR</Respuesta>";
            } else {
                resultadoProceso = resultadoProceso + "\n <Respuesta>OK</Respuesta>";
            }

        } catch (Exception ex) {
            resultadoProceso = "Error generado Intert con Conector: Pedido: " + ex.getMessage();
            Logger.getLogger(OperadorAS400.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(resultadoProceso);
        }
        return resultadoProceso;
    }

    public String eliminarPedido(String codigoPedido, String codigoCliente, String idLog, String idContrato) {
        try {
            objOrdenCliente = new CustomerOrder(SOADoc.Action_Delete);
            objOrdenCliente.setCustomerCode(codigoCliente);
            objOrdenCliente.setCustomerOrderCode(codigoPedido);

            indentificadorProceso = "PED-" + idLog + "-" + idContrato + "-" + formatoFecha.format(new Date());
            fuenteProceso = "PED-InteroperaLX-PedidosService-eliminarPedido";
            objOrdenCliente.setAttibute("identifier", indentificadorProceso);
            objOrdenCliente.setAttibute("sourceName", fuenteProceso);

            conexionLX.obtenerConector().sendMessage(objOrdenCliente.getDocument());
            resultadoProceso = conexionLX.obtenerConector().sreply;

            if (resultadoProceso.contains("<Error>")) {
                resultadoProceso = resultadoProceso + "\n <Respuesta>ERROR</Respuesta>";
            } else {
                resultadoProceso = resultadoProceso + "\n <Respuesta>OK</Respuesta>";
            }

        } catch (Exception ex) {
            resultadoProceso = "Error generado Intert con Conector: Pedido: " + ex.getMessage();
            Logger.getLogger(OperadorLXConectors.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(resultadoProceso);
        }
        return resultadoProceso;
    }

    public String agregarLineaPedido(List<ObjDetallePedido> detalleLinea, String codigoPedido, String codigoCliente, String idContrato, String idLog) {
        try {
            objOrdenCliente = new CustomerOrder(SOADoc.Action_Change);
            objOrdenCliente.setCustomerCode(codigoCliente);
            objOrdenCliente.setCustomerOrderCode(codigoPedido);
            indentificadorProceso = "PED-" + idLog + "-" + idContrato + "-" + formatoFecha.format(new Date());
            fuenteProceso = "PED-InteroperaLX-PedidosService-agregarLineaPedido";
            objOrdenCliente.setAttibute("identifier", indentificadorProceso);
            objOrdenCliente.setAttibute("sourceName", fuenteProceso);

            detalleLinea.stream().map((detalleInd) -> {
                lineaPedido = new Line(objOrdenCliente);
                return detalleInd;
            }).map((detalleInd) -> {
                notaLinea = new Note(lineaPedido);
                notaLinea.setDescription(detalleInd.getNotaLinea());
                return detalleInd;
            }).map((detalleInd) -> {
                lineaPedido.setTxCurrSellingUnitNetPrice(detalleInd.getPrecioItem());
                return detalleInd;
            }).map((detalleInd) -> {
                lineaPedido.setItemCode(detalleInd.getCodigoItem());
                return detalleInd;
            }).map((detalleInd) -> {
                lineaPedido.setOrderedSellingUnitQty(detalleInd.getCantidadOrdenada());
                return detalleInd;
            }).forEachOrdered((detalleInd) -> {
                lineaPedido.setLineCode(detalleInd.getLineaOperacion());
                lineaPedido.setInventoryReasonCode("01");
            });
            conexionLX.obtenerConector().sendMessage(objOrdenCliente.getDocument());
            resultadoProceso = conexionLX.obtenerConector().sreply;

            if (resultadoProceso.contains("<Error>")) {
                resultadoProceso = resultadoProceso + "\n <Respuesta>ERROR</Respuesta>";
            } else {
                resultadoProceso = resultadoProceso + "\n <Respuesta>OK</Respuesta>";
            }

        } catch (Exception ex) {
            resultadoProceso = "Error generado Intert con Conector: Pedido: " + ex.getMessage();
            Logger.getLogger(OperadorLXConectors.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(resultadoProceso);
        }
        return resultadoProceso;
    }

    public String modificarLineaPedido(List<ObjDetallePedido> detalleLinea, String codigoPedido, String codigoCliente, String idContrato, String idLog) {
        try {
            objOrdenCliente = new CustomerOrder(SOADoc.Action_Post);
            objOrdenCliente.setCustomerCode(codigoCliente);
            objOrdenCliente.setCustomerOrderCode(codigoPedido);
            indentificadorProceso = "PED-" + idLog + "-" + idContrato + "-" + formatoFecha.format(new Date());
            fuenteProceso = "PED-InteroperaLX-PedidosService-modificarLineaPedido";
            objOrdenCliente.setAttibute("identifier", indentificadorProceso);
            objOrdenCliente.setAttibute("sourceName", fuenteProceso);

            detalleLinea.stream().map((detalleInd) -> {
                lineaPedido = new Line(objOrdenCliente);
                return detalleInd;
            }).map((detalleInd) -> {
                notaLinea = new Note(lineaPedido);
                notaLinea.setDescription(detalleInd.getNotaLinea());
                return detalleInd;
            }).map((detalleInd) -> {
                lineaPedido.setTxCurrSellingUnitNetPrice(detalleInd.getPrecioItem());
                return detalleInd;
            }).map((detalleInd) -> {
                lineaPedido.setItemCode(detalleInd.getCodigoItem());
                return detalleInd;
            }).map((detalleInd) -> {
                lineaPedido.setOrderedSellingUnitQty(detalleInd.getCantidadOrdenada());
                return detalleInd;
            }).forEachOrdered((detalleInd) -> {
                lineaPedido.setLineCode(detalleInd.getLineaOperacion());
                lineaPedido.setInventoryReasonCode("01");
            });

            conexionLX.obtenerConector().sendMessage(objOrdenCliente.getDocument());
            resultadoProceso = conexionLX.obtenerConector().sreply;

            if (resultadoProceso.contains("<Error>")) {
                resultadoProceso = resultadoProceso + "\n <Respuesta>ERROR</Respuesta>";
            } else {
                resultadoProceso = resultadoProceso + "\n <Respuesta>OK</Respuesta>";
            }

        } catch (Exception ex) {
            resultadoProceso = "Error generado Intert con Conector: Pedido: " + ex.getMessage();
            Logger.getLogger(OperadorLXConectors.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(resultadoProceso);
        }
        return resultadoProceso;
    }

    public String eliminarLineaPedido(List<ObjDetallePedido> detalleLinea, String codigoPedido, String codigoCliente, String idContrato, String idLog) {
        try {

            objOrdenCliente = new CustomerOrder(SOADoc.Action_Change);
            objOrdenCliente.setCustomerOrderCode(codigoPedido);
            objOrdenCliente.setCustomerCode(codigoCliente);
            indentificadorProceso = "PED-" + idLog + "-" + idContrato + "-" + formatoFecha.format(new Date());
            fuenteProceso = "PED-InteroperaLX-PedidosService-eliminarLineaPedido";
            objOrdenCliente.setAttibute("identifier", indentificadorProceso);
            objOrdenCliente.setAttibute("sourceName", fuenteProceso);

            detalleLinea.stream().map((detalleInd) -> {
                lineaPedido = new Line(objOrdenCliente);
                return detalleInd;
            }).map((detalleInd) -> {
                lineaPedido.setLineCode(detalleInd.getLineaOperacion());
                return detalleInd;
            }).map((_item) -> {
                lineaPedido.setCustomerOrderCode(codigoPedido);
                return _item;
            }).forEachOrdered((_item) -> {
                lineaPedido.setInstanceStatus("ZL");
            });

            conexionLX.obtenerConector().sendMessage(objOrdenCliente.getDocument());
            resultadoProceso = conexionLX.obtenerConector().sreply;

            if (resultadoProceso.contains("<Error>")) {
                resultadoProceso = resultadoProceso + "\n <Respuesta>ERROR</Respuesta>";
            } else {
                resultadoProceso = resultadoProceso + "\n <Respuesta>OK</Respuesta>";
            }

        } catch (Exception ex) {
            resultadoProceso = "Error generado Intert con Conector: Pedido: " + ex.getMessage();
            Logger.getLogger(OperadorLXConectors.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(resultadoProceso);
        }
        return resultadoProceso;
    }

    // Metodos para Procesos de Productos
    public String crearProducto(ReqMaestrosWS productoInd) {
        try {
            objProducto = new Item(SOADoc.Action_Create);
            fuenteProceso = "PROD-InteroperaLX-MaestroService-crearProducto";
            objProducto.setAttibute("sourceName", fuenteProceso);

            objProducto.setitemCode(productoInd.getCodigo());
            objProducto.setdescription(productoInd.getDescripcion());
            objProducto.setextraDescription(productoInd.getDescripcionExtra());
            objProducto.settypeCode(productoInd.getTipoProducto());
            objProducto.setclassCode(productoInd.getClaseProducto());
            objProducto.setabcInventoryCode(productoInd.getCodigoABC());
            objProducto.setpurchasingUnitCode(productoInd.getUMCompra());
            objProducto.setsellingUOM(productoInd.getUMVenta());
            objProducto.setlotControlledInd(productoInd.getLoteControlado());
            objProducto.setactualMaterialUpdateCode(productoInd.getSegmentoCostos());

            // Con solo estos datos el conector funciona de manera correcta
            // Datos de aca para abajo pueden o no venir en el XML
            objProducto.settaxCode(productoInd.getGrupoImpositivo());
            objProducto.setcatalogNumberCode(productoInd.getFamiliaComunHT());
            // Falta SET DE POSICION            
            objProducto.setgroupSalesAnalysisRef1(productoInd.getLinea());
            objProducto.setgroupSalesAnalysisRef2(productoInd.getDestino());
            objProducto.setgroupSalesAnalysisRef3(productoInd.getUSO());
            objProducto.setgroupSalesAnalysisRef4(productoInd.getReferencia4());
            objProducto.setgroupSalesAnalysisRef5(productoInd.getSector());
            objProducto.setprimaryVendorCode(productoInd.getProvPrincipal());
            objProducto.setcountryOfOriginCode(productoInd.getPaisOrigen());
            objProducto.setdropShipAllowedInd(productoInd.getEnvioDirecto());
            objProducto.setmasterScheduledInd(productoInd.getMPS());
            objProducto.sethorizonDays(productoInd.getDiasHorizonte());
            objProducto.setdemandTimeFenceDays(productoInd.getDiasDemanda());
            objProducto.setfirstDemandCode(productoInd.getCodDemanda1());
            objProducto.setsecondDemandCode(productoInd.getCodDemanda2());
            objProducto.setrequirementsCode(productoInd.getCodNecesidadesPlaneacion());
            objProducto.setleadTimeDays(productoInd.getLeadTime());
            objProducto.setdailyLeadTimeRate(productoInd.getTasaDiariaAprovisionamiento());
            objProducto.settargetAnnualQty(productoInd.getCantAnualObj());
            objProducto.setmaxInventoryQty(productoInd.getInvMax());
            objProducto.setminBalanceQty(productoInd.getInvMinimo());
            objProducto.setminBalanceHorizonDays(productoInd.getDiasHorizonteMin());
            objProducto.setminBalanceDays(productoInd.getDiasExistencia());
            objProducto.setorderPolicy(productoInd.getPoliticaPlaneacion());
            // Datos de Volumen y Dimensiones de los Productos

            // Proceso del EAN
            conexionLX.obtenerConector().sendMessage(objProducto.getDocument());
            resultadoProceso = conexionLX.obtenerConector().sreply;
            System.out.println("Resultado del Proceso de Item: " + resultadoProceso);

            if (resultadoProceso.contains("<Error>")) {

                resultadoProceso = resultadoProceso + "\n <Respuesta>ERROR</Respuesta>";

            } else {
                resultadoProceso = "<Respuesta> Resultado Envio al Conector IIM: \n" + resultadoProceso;

                objProductoCIC = new ItemFacility(SOADoc.Action_Create);
                fuenteProceso = "PROD-InteroperaLX-MaestroService-crearProducto";
                objProductoCIC.setAttibute("sourceName", fuenteProceso);
                objProductoCIC.setItemCode(productoInd.getCodigo());
                objProductoCIC.setItemClass(productoInd.getClaseProducto());
                objProductoCIC.setLeadTimeDays(productoInd.getLeadTime());
                objProductoCIC.setMinimumBalanceQuantity(productoInd.getInvMinimo());
                objProductoCIC.setMasterScheduled(productoInd.getMPS());
                objProductoCIC.setOrderPolicy(productoInd.getPoliticaPlaneacion());
                objProductoCIC.setLotSize(productoInd.getMedidaLote());
                // ICORDC ESTE CAMPO SE LLENA CON 0 AUTOMATICAMENTE POR EL CONECTOR
                objProductoCIC.setABCInventoryCode(productoInd.getCodigoABC());
                objProductoCIC.setIncrementalOrderQty(productoInd.getIncrementoLote());
                objProductoCIC.setTargetAnnualQuantity(productoInd.getCantAnualObj());
                objProductoCIC.setMaximumInventoryQuantity(productoInd.getInvMax());
                objProductoCIC.setDemandTimeFenceDaysPeriods(productoInd.getDiasDemanda());
                objProductoCIC.setDemandCodeFirstPeriod(productoInd.getCodDemanda1());
                objProductoCIC.setDemandCodeSecondPeriod(productoInd.getCodDemanda2());
                objProductoCIC.setMinimumBalanceHorizonDays(productoInd.getDiasHorizonteMin());
                objProductoCIC.setMinimumBalanceDaysPeriod(productoInd.getDiasExistencia());
                objProductoCIC.setDailyLeadTimeRate(productoInd.getTasaDiariaAprovisionamiento());
                objProductoCIC.setRequirementsCode(productoInd.getCodNecesidadesPlaneacion());
                objProductoCIC.setFacilityCode(productoInd.getInstalacion());

                conexionLX.obtenerConector().sendMessage(objProductoCIC.getDocument());
                resultadoProceso = resultadoProceso + "\n Resultado Envio al Conector CIC: \n" + conexionLX.obtenerConector().sreply + "\n Proceso Enviado los archivos ESP - ZLI. </Respuesta>";

            }

            // Lanzar Proceso de Insert de Precios
        } catch (Exception ex) {
            resultadoProceso = "Error generado Intert con Conector: Producto: " + ex.getMessage();
            Logger.getLogger(OperadorLXConectors.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(resultadoProceso);
        }
        return resultadoProceso;
    }

    // Conexión ConectoLX
    public void obtenerConexionLX() throws Exception {
        try {
            conexionLX.generarConexion();
            conexionAbierta = true;
        } catch (Exception ex) {
            Logger.getLogger(OperadorLXConectors.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error generando la CONEXION CON LXConectors: \n" + ex.toString());
            throw new Exception(ex);
        }
    }

    public void cerrarConexionLX() {
        try {
            if (conexionLX != null) {
                conexionLX.desloguearConector();
                conexionAbierta = false;
            }
        } catch (Exception ex) {
            Logger.getLogger(OperadorLXConectors.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error CERRANDO la CONEXION CON LXConectors: \n" + ex.toString());
        }
    }

    public boolean esConexionAbierta() {
        return conexionAbierta;
    }

    public void setConexionAbierta(boolean conexionAbierta) {
        this.conexionAbierta = conexionAbierta;
    }

    public void generarAuditoria(String mensaje) {
        java.util.Date date = new java.util.Date();
        SimpleDateFormat formato = new SimpleDateFormat("hh:mm:ss:mmmm");
        System.out.println(mensaje + " - Hora: " + date);
    }

}
