/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.as400.operators;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bellota.as400.AS400Conectors.ActualizadorAS400;

import org.bellota.as400.AS400Conectors.ConectorAS400;
import org.bellota.as400.AS400Conectors.ConectorConsAS400;
import org.bellota.as400.conexions.ConexionAS400;
import org.bellota.config.PropiedadesAPP;
import org.bellota.util.objects.ObjLogLXC;
import org.bellota.util.objects.WSRequest.asignacion.ObjConsultaAsignacion;
import org.bellota.util.objects.datatables.ObjColumnaDataTable;
import org.bellota.util.objects.datatables.ObjDataTables;

/**
 *
 * @author Cristian Alberto Perez
 */
public class OperadorAS400 {

    private final PropiedadesAPP utilPropiedades = new PropiedadesAPP();
    private List<ObjLogLXC> listadoLXC;
    private ObjLogLXC objLogLXC;
    private final Object[] resultadoLXCInbox = new Object[2];

    private final String hostConector = utilPropiedades.obtenerPropiedades().getProperty("HostConectorLXP");
    private final String libreriaConector = utilPropiedades.obtenerPropiedades().getProperty("FLibrary");
    private final String passConector = utilPropiedades.obtenerPropiedades().getProperty("PassConectorLXP");
    private final String usarioConector = utilPropiedades.obtenerPropiedades().getProperty("UsuarioConectorLXP");
    private final String libreriaBFDESPA = utilPropiedades.obtenerPropiedades().getProperty("BFPEDLibrary");

    private String iBatch, querySQL;
    private ConexionAS400 conexionAS400;
    private ConectorConsAS400 conectorConsAS400;
    private ConectorAS400 conectorAS400;
    private ActualizadorAS400 actualizadorAS400;

    private ResultSet resultadoConsultaAS400;
    private int diferenciaDigitos, ultimoConsecutivo, topConsecutivo, minConsecutivo, cantidadRegLXC, flagAsignacion;

    private String whereSQL, resultadoInsercion, bodegaPedido, resultadoActualizacion, textoCSV;
    private String[] consultaFechas, nombresColumnasDin;

    private CallableStatement sentenciaLlamadoPGM;

    private byte[] xmlConsultado = null;
    private boolean conexionAbierta = false;

    private final DateFormat convertirFechaString = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    private final SimpleDateFormat formatoAS400 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private boolean confirmaAsignacion;

    private List<ObjConsultaAsignacion> listadoAsignaciones;
    private ObjConsultaAsignacion asignacionInd;

    // Metodo para calcular y generar numero IBATCH para la transaccion
    public String generarIbatch(String procesoSolicitado) throws Exception {

        try {

            querySQL = "SELECT * FROM RFCONSEC WHERE PROCESO = '" + procesoSolicitado + "'";
            conectorConsAS400 = new ConectorConsAS400(obtenerConexion(), querySQL);

            if (conectorConsAS400.obtenerError() == null) {

                resultadoConsultaAS400 = conectorConsAS400.obtenerResultado();

                while (resultadoConsultaAS400.next()) {

                    ultimoConsecutivo = resultadoConsultaAS400.getInt("CONSECUTIVO");
                    topConsecutivo = resultadoConsultaAS400.getInt("TOPCONS");
                    minConsecutivo = resultadoConsultaAS400.getInt("INICONS");

                    if (ultimoConsecutivo == topConsecutivo) {
                        resultadoConsultaAS400.updateInt("CONSECUTIVO", 0);
                        resultadoConsultaAS400.updateRow();
                    } else if (ultimoConsecutivo == minConsecutivo) {
                        ultimoConsecutivo = 1;
                        resultadoConsultaAS400.updateInt("CONSECUTIVO", ultimoConsecutivo);
                        resultadoConsultaAS400.updateRow();
                    } else {
                        resultadoConsultaAS400.updateInt("CONSECUTIVO", ultimoConsecutivo + 1);
                        resultadoConsultaAS400.updateRow();
                    }

                    diferenciaDigitos = String.valueOf(topConsecutivo).length() - String.valueOf(ultimoConsecutivo).length();
                    iBatch = String.valueOf(ultimoConsecutivo);

                    for (int i = 0; i < diferenciaDigitos; i++) {
                        iBatch = "0" + iBatch;
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(OperadorAS400.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error generando IBATCH en el OperadorAS400: \n" + ex.toString());
            throw new Exception(ex);
        } finally {
            cerrarConexion();
        }

        return iBatch;
    }

    // Metodos de Ayuda LX    
    public String actualizarLote(String numeroLote) throws Exception {
        try {
            resultadoActualizacion = "";
            querySQL = "UPDATE ILN SET LMRB = 'A' WHERE LLOT = '" + numeroLote + "'";
            actualizadorAS400 = new ActualizadorAS400(obtenerConexion(), querySQL);
            if (actualizadorAS400.obtenerMensajeError() != null) {
                resultadoActualizacion = actualizadorAS400.obtenerMensajeError();
            } else {
                resultadoActualizacion = "Lote Actualizado Exitosamente.";
            }
        } catch (Exception ex) {
            Logger.getLogger(OperadorAS400.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error Insertando en RFPEDID AS400: \n" + ex.toString());
            throw new Exception(ex);
        }
        return resultadoActualizacion;
    }

    public String obtenerBodega(Connection conexionSQL, String codigoProducto, String codigoCliente) {
        bodegaPedido = "";
        try {
            querySQL = "SELECT Iwhs FROM " + libreriaConector + ".IIM B WHERE B.Iprod = '" + codigoProducto + "'";
            conectorAS400 = new ConectorAS400(conexionSQL, querySQL);
            if (conectorAS400.obtenerError() == null) {
                resultadoConsultaAS400 = conectorAS400.obtenerResultado();
                while (resultadoConsultaAS400.next()) {
                    bodegaPedido = resultadoConsultaAS400.getString("Iwhs");
                }
                if (bodegaPedido.trim().equals("") || bodegaPedido.trim().isEmpty() || bodegaPedido.trim().equals(" ")) {
                    querySQL = "SELECT Cccode3 FROM COLLX835F.RFPARAM B WHERE Cctabl = 'BBODCLI' AND Cccode =(SELECT Ctype FROM RCM D WHERE D.Ccust = '" + codigoCliente + "') AND Cccode2 = '" + codigoProducto + "'";
                    conectorAS400 = new ConectorAS400(conexionSQL, querySQL);
                    if (conectorAS400.obtenerError() == null) {
                        resultadoConsultaAS400 = conectorAS400.obtenerResultado();
                        while (resultadoConsultaAS400.next()) {
                            bodegaPedido = resultadoConsultaAS400.getString("Cccode3");
                        }
                        if (bodegaPedido.trim().equals("") || bodegaPedido.trim().isEmpty() || bodegaPedido.trim().equals(" ")) {
                            querySQL = "SELECT Cwhse FROM COLLX835F.RCM C WHERE C.Ccust = '" + codigoCliente + "'";
                            conectorAS400 = new ConectorAS400(conexionSQL, querySQL);
                            if (conectorAS400.obtenerError() == null) {
                                resultadoConsultaAS400 = conectorAS400.obtenerResultado();
                                while (resultadoConsultaAS400.next()) {
                                    bodegaPedido = resultadoConsultaAS400.getString("Cwhse");
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            bodegaPedido = "Error generando la CONEXION CON AS400: \n" + ex.toString();
            Logger.getLogger(OperadorAS400.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error generando la CONEXION CON AS400: \n" + ex.toString());
        }
        return bodegaPedido;
    }

    // Metodos de Servlets
    public byte[] consultarXMLLX(String tipoSolicitud, int idTransaccion) {
        try {

            if (tipoSolicitud.equals("Entrada")) {
                querySQL = "SELECT C_XML FROM " + libreriaConector + ".LXCINBOX WHERE C_ID =" + idTransaccion;
            } else {
                querySQL = "SELECT C_REPLY FROM " + libreriaConector + ".LXCINBOX WHERE C_ID =" + idTransaccion;
            }

            conectorAS400 = new ConectorAS400(obtenerConexion(), querySQL);

            if (conectorAS400.obtenerError() == null) {
                resultadoConsultaAS400 = conectorAS400.obtenerResultado();
                while (resultadoConsultaAS400.next()) {
                    if (tipoSolicitud.equals("Entrada")) {
                        xmlConsultado = resultadoConsultaAS400.getBytes("C_XML");
                    } else {
                        xmlConsultado = resultadoConsultaAS400.getBytes("C_REPLY");
                    }
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(OperadorAS400.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error generando la CONEXION CON AS400: \n" + ex.toString());
        } finally {
            cerrarConexion();
        }

        return xmlConsultado;
    }

    private int cantidadRegLXCInbox(String whereSQL) {

        try {
            querySQL = "SELECT COUNT(C_ID) CantidadRegistros FROM " + libreriaConector + ".LXCINBOX " + whereSQL;

            conectorAS400 = new ConectorAS400(obtenerConexion(), querySQL);

            if (conectorAS400.obtenerError() == null) {
                resultadoConsultaAS400 = conectorAS400.obtenerResultado();
                while (resultadoConsultaAS400.next()) {
                    cantidadRegLXC = resultadoConsultaAS400.getInt("CantidadRegistros");
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(OperadorAS400.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error generando la CONEXION CON AS400: \n" + ex.toString());
        } finally {
            cerrarConexion();
        }

        return cantidadRegLXC;
    }

    public Object[] consultarLXCInbox(ObjDataTables objDataTable) {

        try {

            listadoLXC = new ArrayList<>();

            whereSQL = (construirWhereLXC(objDataTable.getColumnasVista()).equals("WHERE ") ? "" : construirWhereLXC(objDataTable.getColumnasVista()));

            querySQL = "SELECT C_ID, C_NOUN_ID, C_MESSAGEID, C_CREATED_DATE_TIME, C_USER, C_PASS_FAIL, C_SOURCE_NAME "
                    + "FROM " + libreriaConector + ".LXCINBOX "
                    + whereSQL
                    + "ORDER BY " + objDataTable.getOrdenGlobal().getNombreColumnaSQL() + " " + objDataTable.getOrdenGlobal().getDireccionOrden().toUpperCase() + " "
                    + "LIMIT " + objDataTable.getInicioVista() + "," + objDataTable.getCantidadRegistros() + "";

            System.out.println(querySQL);
            conectorAS400 = new ConectorAS400(obtenerConexion(), querySQL);

            if (conectorAS400.obtenerError() == null) {
                resultadoConsultaAS400 = conectorAS400.obtenerResultado();
                while (resultadoConsultaAS400.next()) {
                    objLogLXC = new ObjLogLXC();
                    objLogLXC.setIdProceso(resultadoConsultaAS400.getInt("C_ID"));
                    objLogLXC.setFechaproceso(resultadoConsultaAS400.getTimestamp("C_CREATED_DATE_TIME", Calendar.getInstance()));
                    objLogLXC.setIdObjeto(resultadoConsultaAS400.getString("C_NOUN_ID"));
                    objLogLXC.setMensajeProceso(resultadoConsultaAS400.getString("C_MESSAGEID"));
                    objLogLXC.setEstadoProceso(resultadoConsultaAS400.getString("C_PASS_FAIL").trim());
                    objLogLXC.setNombreFuente(resultadoConsultaAS400.getString("C_SOURCE_NAME"));
                    objLogLXC.setUsuarioProceso(resultadoConsultaAS400.getString("C_USER"));
                    listadoLXC.add(objLogLXC);
                }
            }

            resultadoLXCInbox[0] = listadoLXC;
            resultadoLXCInbox[1] = cantidadRegLXCInbox(whereSQL);

        } catch (Exception ex) {
            Logger.getLogger(OperadorAS400.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error generando la consulta de LXCInbox: \n" + ex.toString());
        } finally {
            cerrarConexion();
        }

        return resultadoLXCInbox;
    }

    private String construirWhereLXC(List<ObjColumnaDataTable> columnasSolicitadas) throws ParseException, Exception {

        try {
            whereSQL = "WHERE ";

            for (ObjColumnaDataTable colIndv : columnasSolicitadas) {

                if (colIndv.getDataColumna().equals("FechaProceso")) {
                    if (!colIndv.getBusquedaColumna().getValorBusqueda().equals("SIN BUSQUEDA")) {

                        if (colIndv.getBusquedaColumna().getValorBusqueda().endsWith("-sep")) {

                            if (whereSQL.contains("LIKE")) {
                                whereSQL = whereSQL + "AND (" + colIndv.getNombreColumna() + " > '" + formatoAS400.format(convertirFechaString.parse(colIndv.getBusquedaColumna().getValorBusqueda().replaceAll("-sep", ""))) + "') ";
                            } else {
                                whereSQL = whereSQL + "(" + colIndv.getNombreColumna() + " > '" + formatoAS400.format(convertirFechaString.parse(colIndv.getBusquedaColumna().getValorBusqueda().replaceAll("-sep", ""))) + "') ";
                            }

                        } else if (colIndv.getBusquedaColumna().getValorBusqueda().startsWith("-sep")) {

                            if (whereSQL.contains("LIKE")) {
                                whereSQL = whereSQL + "AND (" + colIndv.getNombreColumna() + " < '" + formatoAS400.format(convertirFechaString.parse(colIndv.getBusquedaColumna().getValorBusqueda().replaceAll("-sep", ""))) + "') ";
                            } else {
                                whereSQL = whereSQL + "(" + colIndv.getNombreColumna() + " < '" + formatoAS400.format(convertirFechaString.parse(colIndv.getBusquedaColumna().getValorBusqueda().replaceAll("-sep", ""))) + "') ";
                            }
                        } else {
                            consultaFechas = colIndv.getBusquedaColumna().getValorBusqueda().split("-sep");
                            if (whereSQL.contains("LIKE")) {
                                whereSQL = whereSQL + "AND (" + colIndv.getNombreColumna() + " BETWEEN '" + formatoAS400.format(convertirFechaString.parse(consultaFechas[0])) + "' AND '" + formatoAS400.format(convertirFechaString.parse(consultaFechas[1])) + "') ";
                            } else {
                                whereSQL = whereSQL + "(" + colIndv.getNombreColumna() + " BETWEEN '" + formatoAS400.format(convertirFechaString.parse(consultaFechas[0])) + "' AND '" + formatoAS400.format(convertirFechaString.parse(consultaFechas[1])) + "') ";
                            }
                        }
                    }
                } else {
                    if (!colIndv.getBusquedaColumna().getValorBusqueda().equals("SIN BUSQUEDA")) {
                        if (whereSQL.contains("LIKE")) {
                            whereSQL = whereSQL + "AND " + colIndv.getNombreColumna() + " LIKE '%" + colIndv.getBusquedaColumna().getValorBusqueda() + "%' ";
                        } else {
                            whereSQL = whereSQL + colIndv.getNombreColumna() + " LIKE '%" + colIndv.getBusquedaColumna().getValorBusqueda() + "%' ";
                        }
                    }
                }
            }

            System.out.println("WHERE SQL: \n" + whereSQL);

        } catch (Exception ex) {
            Logger.getLogger(OperadorAS400.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error generando la consulta de LXCInbox: \n" + ex.toString());
            throw new Exception(ex);
        }
        return whereSQL;
    }

    public String crearCSVQuery(String querySolicitado, String separadorProceso) {
        try {

            textoCSV = "";
            conectorAS400 = new ConectorAS400(obtenerConexion(), querySolicitado);

            if (conectorAS400.obtenerError() == null) {
                nombresColumnasDin = conectorAS400.obtenerNombresColumnas();
                resultadoConsultaAS400 = conectorAS400.obtenerResultado();
                for (String nombreColumna : nombresColumnasDin) {
                    textoCSV += nombreColumna + separadorProceso;
                }
                textoCSV += "\n";
                while (resultadoConsultaAS400.next()) {
                    for (String nombreColumna : nombresColumnasDin) {
                        textoCSV += resultadoConsultaAS400.getNString(nombreColumna) + separadorProceso;
                    }
                    textoCSV += "\n";
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(OperadorAS400.class.getName()).log(Level.SEVERE, null, ex);
            textoCSV = "Error generando la consulta de Querys Dinamicos: \n" + ex.toString();
            System.out.println(textoCSV);
        } finally {
            if (esConexionAbierta()) {
                cerrarConexion();
            }
        }

        return textoCSV;
    }

    // Inserts a AS400
    public void insertAsync400(String insertSQL, Connection conexionSQL) throws Exception {
        try {
            actualizadorAS400 = new ActualizadorAS400(obtenerConexion(), insertSQL);

            if (actualizadorAS400.obtenerMensajeError() != null) {
                resultadoInsercion = actualizadorAS400.obtenerMensajeError();
            } else {
                resultadoInsercion = "Insertado Exitosamente.";
            }

            System.out.println("Resultado Insert: " + resultadoInsercion);

        } catch (Exception ex) {
            Logger.getLogger(OperadorAS400.class.getName()).log(Level.SEVERE, null, ex);
            resultadoInsercion = "Error Insertando en AS400: \n" + ex.toString();
            System.out.println(resultadoInsercion);
            throw new Exception(ex);
        }
    }

    public String insertSync400(String insertSQL, Connection conexionSQL) {
        try {
            actualizadorAS400 = new ActualizadorAS400(obtenerConexion(), insertSQL);

            if (actualizadorAS400.obtenerMensajeError() != null) {
                resultadoInsercion = actualizadorAS400.obtenerMensajeError();
            } else {
                resultadoInsercion = "Insertado Exitosamente.";
            }

            System.out.println("Resultado Insert AS400: " + resultadoInsercion);

        } catch (Exception ex) {
            Logger.getLogger(OperadorAS400.class.getName()).log(Level.SEVERE, null, ex);
            resultadoInsercion = "Error Insertando en RFPEDID AS400: \n" + ex.toString();
            System.out.println(resultadoInsercion);
        }
        return resultadoInsercion;
    }

    // Asignaciones
    public boolean validarAsignacion(String iBatch) {

        try {
            confirmaAsignacion = true;
            querySQL = "SELECT DFLAG FROM " + libreriaConector + ".BFDESPA WHERE DBATCH =" + iBatch;
            conectorAS400 = new ConectorAS400(obtenerConexion(), querySQL);

            if (conectorAS400.obtenerError() == null) {
                resultadoConsultaAS400 = conectorAS400.obtenerResultado();
                while (resultadoConsultaAS400.next()) {
                    flagAsignacion = resultadoConsultaAS400.getInt("DFLAG");
                    if (flagAsignacion == 0) {
                        confirmaAsignacion = false;
                    }
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(OperadorAS400.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error Consultando Asiganción AS400: \n" + ex.toString());
            confirmaAsignacion = false;
        } finally {
            cerrarConexion();
        }

        return confirmaAsignacion;

    }

    public List<ObjConsultaAsignacion> consultarAsiganciones(String iBatch) {
        try {
            listadoAsignaciones = new ArrayList<>();
            querySQL = "SELECT DID, DFLAG, DDESER FROM " + libreriaConector + ".BFDESPA WHERE DBATCH =" + iBatch;
            conectorAS400 = new ConectorAS400(obtenerConexion(), querySQL);
            if (conectorAS400.obtenerError() == null) {
                resultadoConsultaAS400 = conectorAS400.obtenerResultado();
                while (resultadoConsultaAS400.next()) {
                    asignacionInd = new ObjConsultaAsignacion();
                    asignacionInd.setIdAsignacion(resultadoConsultaAS400.getInt("DID"));
                    asignacionInd.setFlagProceso(resultadoConsultaAS400.getInt("DFLAG"));
                    asignacionInd.setMensajeProceso(resultadoConsultaAS400.getString("DDESER"));
                    listadoAsignaciones.add(asignacionInd);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(OperadorAS400.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error Consultando Asignación AS400: \n" + ex.toString());
            listadoAsignaciones = null;
        } finally {
            cerrarConexion();
        }
        return listadoAsignaciones;
    }
    
    //Metodos Utiles para Procesos de Conexion y Auditoria  
    // Conexion AS400
    public Connection obtenerConexion() throws Exception {
        try {
            if (conexionAS400 == null) {
                conexionAS400 = new ConexionAS400(usarioConector, passConector, libreriaConector, hostConector);
                conexionAbierta = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(OperadorAS400.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error generando la CONEXION CON AS400: \n" + ex.toString());
            throw new Exception(ex);
        }
        return conexionAS400.obtenerConector();
    }

    public void cerrarConexion() {
        try {
            if (conexionAS400 != null) {
                conexionAS400.cerrarConexion();
                conexionAbierta = false;
                conexionAS400 = null;
            }
        } catch (Exception ex) {
            Logger.getLogger(OperadorAS400.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error CERRANDO la CONEXION CON AS400: \n" + ex.toString());
        }
    }

    public boolean esConexionAbierta() {
        return conexionAbierta;
    }

    public void setConexionAbierta(boolean conexionAbierta) {
        this.conexionAbierta = conexionAbierta;
    }

    // LLamado a programa AS400
    public void llamarPGM(String iBatch) throws Exception {
        try {
            if (iBatch.length() < 8) {
                for (int i = iBatch.length(); i < 8; i++) {
                    iBatch = "0" + iBatch;
                }
            }
            sentenciaLlamadoPGM = obtenerConexion().prepareCall("{ CALL BCCONFIR('" + iBatch + "') }");
            sentenciaLlamadoPGM.execute();
            sentenciaLlamadoPGM.close();
        } catch (Exception ex) {
            Logger.getLogger(OperadorAS400.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error en el llamado PGM: " + ex.toString());
            throw new Exception(ex);
        } finally {
            if (esConexionAbierta()) {
                cerrarConexion();
            }
        }
    }

    // Auditoria Local
    public void generarAuditoria(String mensaje) {
        java.util.Date date = new java.util.Date();
        SimpleDateFormat formato = new SimpleDateFormat("hh:mm:ss:mmmm");
        System.out.println(mensaje + " - Hora: " + date);
    }

}
