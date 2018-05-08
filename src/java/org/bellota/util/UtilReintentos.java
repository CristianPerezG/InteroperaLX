/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import org.bellota.config.PropiedadesAPP;
import org.bellota.util.log.UtilLogs;
import org.w3c.dom.DOMException;

/**
 *
 * @author Cristian Alberto Perez
 */
public class UtilReintentos {

    private final UtilLogs utilLogs = new UtilLogs();
    private final PropiedadesAPP utilPropiedades = new PropiedadesAPP();
    private String endpointSOAP;
    private String accionSOAP;

    private final String archivoXMLNuevo = utilPropiedades.obtenerPropiedades().getProperty("RutaArchivosRemota");

    private SOAPMessage mensajeSOAP;
    private SOAPMessage respuestaSOAP;
    private SOAPConnection conexionSOAP;
    private SOAPConnectionFactory fabricaConexionSOAP;
    private MessageFactory fabricaMensajesSOAP;
    private MimeHeaders cabezerasSOAP;

    private Path rutaArchivo;
    private DirectoryStream<Path> listadoArchivos;
    private InputStream streamEntradaArchivo;
    private Timer timerReintentos;

    public UtilReintentos() {
        try {

            fabricaConexionSOAP = SOAPConnectionFactory.newInstance();
            timerReintentos = new Timer();

            timerReintentos.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        utilLogs.escribirLog("Inicio Proceso de Reintentos SOAP", 1);

                        fabricaMensajesSOAP = MessageFactory.newInstance();
                        conexionSOAP = fabricaConexionSOAP.createConnection();

                        rutaArchivo = Paths.get(archivoXMLNuevo);
                        listadoArchivos = Files.newDirectoryStream(rutaArchivo);

                        for (Path archivoInd : listadoArchivos) {

                            if (archivoInd.toFile().getName().contains("-P")) {
                                endpointSOAP = utilPropiedades.obtenerPropiedades().getProperty("WFCBodegaEndPointP");
                                accionSOAP = utilPropiedades.obtenerPropiedades().getProperty("WFCBodegaResMethodP");
                            } else {
                                endpointSOAP = utilPropiedades.obtenerPropiedades().getProperty("WFCBatchEndPointP");
                                accionSOAP = utilPropiedades.obtenerPropiedades().getProperty("WFCBatchResMethodP");
                            }

                            streamEntradaArchivo = new FileInputStream(archivoInd.toFile());
                            mensajeSOAP = fabricaMensajesSOAP.createMessage(null, streamEntradaArchivo);

                            cabezerasSOAP = mensajeSOAP.getMimeHeaders();

                            cabezerasSOAP.addHeader("SOAPAction", accionSOAP);

                            mensajeSOAP.saveChanges();

                            System.out.println("Reintento enviado a Servicio:");
                            mensajeSOAP.writeTo(System.out);

                            System.out.println("\n */*/*/*/*/*/*/*/*/*/*/ \n");

                            streamEntradaArchivo.close();

                            respuestaSOAP = conexionSOAP.call(mensajeSOAP, endpointSOAP);

                            System.out.println("Respuesta Reintento enviado a Servicio:");
                            respuestaSOAP.writeTo(System.out);

                            System.out.println("\n");

                            if (!respuestaSOAP.getSOAPBody().getFirstChild().getTextContent().contains("Transacciones procesadas")) {
                                utilLogs.escribirLog("Error presentando en el servicio VISUAL: " + respuestaSOAP.getSOAPBody().getFirstChild().getTextContent(), 2);
                            } else {
                                archivoInd.toFile().deleteOnExit();
                                utilLogs.escribirLog("Archivo Eliminado Exitosamente:\n Archivo: " + archivoInd.getFileName(), 1);
                            }
                        }

                        utilLogs.escribirLog("Finalizo Proceso de Reintentos SOAP correctamente", 1);
                        conexionSOAP.close();

                    } catch (IOException | UnsupportedOperationException | SOAPException | DOMException ex) {
                        Logger.getLogger(UtilReintentos.class.getName()).log(Level.SEVERE, null, ex);
                        utilLogs.escribirLog("Se ha generado un Error en los Reintentos de SOAP: " + ex.toString() + "\n Puede ser posible que el Servicio no se encuentre Disponible", 2);
                        utilLogs.escribirLog("Finalizo Proceso de Reintentos SOAP Con Errores", 1);
                    }
                }
            }, 0, 300000);

        } catch (UnsupportedOperationException | SOAPException ex) {
            Logger.getLogger(UtilReintentos.class.getName()).log(Level.SEVERE, null, ex);
            utilLogs.escribirLog("Se ha generado un Error en los Reintentos de SOAP: " + ex.toString() + "\n Puede ser posible que el Servicio no se encuentre Disponible", 2);
            utilLogs.escribirLog("Finalizo Proceso de Reintentos SOAP Con Errores", 1);
        }
    }
}
