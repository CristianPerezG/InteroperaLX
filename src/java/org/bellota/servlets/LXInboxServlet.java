/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bellota.as400.operators.OperadorAS400;
import org.bellota.util.UtilReintentos;
import org.bellota.util.objects.ObjLogLXC;
import org.bellota.util.objects.datatables.ObjBuscarDataTable;
import org.bellota.util.objects.datatables.ObjColumnaDataTable;
import org.bellota.util.objects.datatables.ObjDataTables;
import org.bellota.util.objects.datatables.ObjOrdenDataTable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Cristian Alberto Perez
 */
public class LXInboxServlet extends HttpServlet {

    private final OperadorAS400 operadoAS400 = new OperadorAS400();
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

    private Map<String, String[]> parametrosVista;

    private JSONArray arregloJSON;
    private JSONObject objetoRespuesta;
    private List<ObjLogLXC> listadoLogLXC;
    private JSONObject objOperadorJSON;

    private int cantiadadRegistros;

    private Object[] resultadoLXCInbox = new Object[2];
    private ObjDataTables objDataTables;
    private ObjBuscarDataTable objBuscarGlobalData;
    private ObjBuscarDataTable objBuscarDataColumna;
    private ObjOrdenDataTable objOrdenDataTables;
    private ObjColumnaDataTable objColumnaDataTable;
    private List<ObjColumnaDataTable> columnasVista;

    private String nombreColumna = "", dataColumna = "", valorBusqueda = "";
    private boolean esRegex, hiloPrendido = false;

    private UtilReintentos utilReintentos;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {

            if (!hiloPrendido) {
                utilReintentos = new UtilReintentos();
                hiloPrendido = true;
            }
            objDataTables = new ObjDataTables();
            cantiadadRegistros = 0;
            parametrosVista = request.getParameterMap();
            objBuscarGlobalData = new ObjBuscarDataTable();
            objOrdenDataTables = new ObjOrdenDataTable();
            columnasVista = new ArrayList<>();
            objColumnaDataTable = new ObjColumnaDataTable();

            parametrosVista.entrySet().stream().filter(parametroInd -> parametroInd.getKey().equals("order[0][dir]")).forEachOrdered(parametroInd -> {
                objOrdenDataTables.setDireccionOrden(parametroInd.getValue()[0]);
            });

            parametrosVista.entrySet().stream().filter(parametroInd -> parametroInd.getKey().equals("draw")).forEachOrdered(parametroInd -> {
                objDataTables.setPaginaSolicitada(Integer.valueOf(parametroInd.getValue()[0]));
            });

            parametrosVista.entrySet().stream().filter(parametroInd -> parametroInd.getKey().equals("length")).forEachOrdered(parametroInd -> {
                objDataTables.setCantidadRegistros(Integer.valueOf(parametroInd.getValue()[0]));
            });

            parametrosVista.entrySet().stream().filter(parametroInd -> parametroInd.getKey().equals("start")).forEachOrdered(parametroInd -> {
                objDataTables.setInicioVista(Integer.valueOf(parametroInd.getValue()[0]));
            });

            parametrosVista.entrySet().stream().filter(parametroInd -> parametroInd.getKey().contains("columns")).forEachOrdered(parametroInd -> {

                parametrosVista.entrySet().stream().filter(parametroInd2 -> parametroInd2.getKey().equals("columns[" + cantiadadRegistros + "][data]")).forEachOrdered(parametroInd2 -> {
                    dataColumna = parametroInd2.getValue()[0];
                });

                parametrosVista.entrySet().stream().filter(parametroInd2 -> parametroInd2.getKey().equals("columns[" + cantiadadRegistros + "][name]")).forEachOrdered(parametroInd2 -> {
                    nombreColumna = parametroInd2.getValue()[0];
                });

                parametrosVista.entrySet().stream().filter(parametroInd2 -> parametroInd2.getKey().equals("columns[" + cantiadadRegistros + "][search][value]")).forEachOrdered(parametroInd2 -> {
                    if (parametroInd2.getValue()[0] != null && !parametroInd2.getValue()[0].isEmpty()) {
                        valorBusqueda = parametroInd2.getValue()[0];
                    } else {
                        valorBusqueda = "SIN BUSQUEDA";
                    }
                });

                parametrosVista.entrySet().stream().filter(parametroInd2 -> parametroInd2.getKey().equals("columns[" + cantiadadRegistros + "][search][regex]")).forEachOrdered(parametroInd2 -> {
                    esRegex = Boolean.valueOf(parametroInd2.getValue()[0]);
                });

                if (!nombreColumna.equals("") && !valorBusqueda.equals("") && !dataColumna.equals("")) {
                    objBuscarDataColumna = new ObjBuscarDataTable();
                    objBuscarDataColumna.setActivaRegex(esRegex);
                    objBuscarDataColumna.setValorBusqueda(valorBusqueda);

                    objColumnaDataTable = new ObjColumnaDataTable();
                    objColumnaDataTable.setDataColumna(dataColumna);
                    objColumnaDataTable.setNombreColumna(nombreColumna);
                    objColumnaDataTable.setBusquedaColumna(objBuscarDataColumna);

                    columnasVista.add(objColumnaDataTable);

                    cantiadadRegistros++;
                    nombreColumna = "";
                    valorBusqueda = "";
                    dataColumna = "";
                }
            });

            parametrosVista.entrySet().stream().filter(parametroInd -> parametroInd.getKey().equals("order[0][column]")).forEachOrdered(parametroInd -> {
                objOrdenDataTables.setIndexColumna(Integer.valueOf(parametroInd.getValue()[0]));
                objOrdenDataTables.setNombreColumnaSQL(columnasVista.get(Integer.valueOf(parametroInd.getValue()[0])).getNombreColumna());
            });

            objDataTables.setBusquedaGlobal(objBuscarGlobalData);
            objDataTables.setColumnasVista(columnasVista);
            objDataTables.setOrdenGlobal(objOrdenDataTables);
            objDataTables.setColumnasVista(columnasVista);

            arregloJSON = new JSONArray();
            objetoRespuesta = new JSONObject();
            resultadoLXCInbox = operadoAS400.consultarLXCInbox(objDataTables);

            listadoLogLXC = (List<ObjLogLXC>) resultadoLXCInbox[0];

            for (ObjLogLXC resIndv : listadoLogLXC) {

                objOperadorJSON = new JSONObject();

                objOperadorJSON.put("IdentidadObjeto", resIndv.getIdObjeto());
                objOperadorJSON.put("MensajeProceso", resIndv.getMensajeProceso());
                objOperadorJSON.put("XMLEnviado", "ConsultaXML?idTransaccion=" + resIndv.getIdProceso() + "&tipoSolicitud=Entrada");
                objOperadorJSON.put("XMLResultado", "ConsultaXML?idTransaccion=" + resIndv.getIdProceso() + "&tipoSolicitud=Salida");
                objOperadorJSON.put("FechaProceso", formatoFecha.format(resIndv.getFechaproceso()));
                objOperadorJSON.put("Usuario", resIndv.getUsuarioProceso());
                objOperadorJSON.put("EstadoProceso", resIndv.getEstadoProceso());
                objOperadorJSON.put("NombreFuente", resIndv.getNombreFuente());

                arregloJSON.put(objOperadorJSON);
            }

            objetoRespuesta.put("data", arregloJSON);
            objetoRespuesta.put("recordsTotal", Integer.valueOf(resultadoLXCInbox[1].toString()));
            objetoRespuesta.put("recordsFiltered", Integer.valueOf(resultadoLXCInbox[1].toString()));
            objetoRespuesta.put("draw", Integer.valueOf(request.getParameter("draw")));

            response.setContentType("application/json");

            PrintWriter out = response.getWriter();
            out.println(objetoRespuesta);
            out.flush();

        } catch (IOException | NumberFormatException | JSONException ex) {
            Logger.getLogger(LXInboxServlet.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error en LXCINBOX: " + ex);
        } finally {
            if (operadoAS400.esConexionAbierta()) {
                operadoAS400.cerrarConexion();
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet para Consultas LXCINBOX";
    }

}
