/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.util.log;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bellota.config.PropiedadesAPP;

/**
 *
 * @author Cristian Alberto Perez
 */
public class UtilLogs {

    private final PropiedadesAPP utilPropiedades = new PropiedadesAPP();
    private final String archivoLogProcesos = utilPropiedades.obtenerPropiedades().getProperty("LogProcesosoPA");
    private final String archivoLogError = utilPropiedades.obtenerPropiedades().getProperty("LogErroresPA");
    private TimeZone zonaTiempo;
    private Date fechaAhora;
    private DateFormat formatoFecha;
    private String tiempoAhora;
    private FileWriter objetoEscribeArchivo;
    private String mensajeProceso;

    public void escribirLog(String mensajeProceso, int tipoProceso) {

        try {
            zonaTiempo = TimeZone.getTimeZone("EST");
            fechaAhora = new Date();
            formatoFecha = new SimpleDateFormat("yyyy.mm.dd hh:mm:ss ");
            formatoFecha.setTimeZone(zonaTiempo);
            tiempoAhora = formatoFecha.format(fechaAhora);

            switch (tipoProceso) {
                case 1:
                    objetoEscribeArchivo = new FileWriter(archivoLogProcesos, true);
                    this.mensajeProceso = "*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/\n";
                    this.mensajeProceso += "Mensaje: " + mensajeProceso + "\n";
                    this.mensajeProceso += "Hora: " + tiempoAhora + "\n";
                    this.mensajeProceso += "Tipo de Proceso: Reintento de envio al servicio VISUAL. - Proceso Normal\n";
                    this.mensajeProceso += "*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/\n";
                    objetoEscribeArchivo.write(this.mensajeProceso);
                    break;
                case 2:
                    objetoEscribeArchivo = new FileWriter(archivoLogError, true);
                    this.mensajeProceso = "*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/\n";
                    this.mensajeProceso += "Error: " + mensajeProceso + "\n";
                    this.mensajeProceso += "Hora: " + tiempoAhora + "\n";
                    this.mensajeProceso += "Tipo de Proceso: Reintento de envio al servicio VISUAL. - Error en el Proceso\n";
                    this.mensajeProceso += "*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/\n";
                    objetoEscribeArchivo.write(this.mensajeProceso);
                    break;
                default:
                    break;
            }

            objetoEscribeArchivo.flush();

            if (objetoEscribeArchivo != null) {
                objetoEscribeArchivo.close();
            }

        } catch (IOException ex) {
            Logger.getLogger(UtilLogs.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error guardando datos en LOG del hilo de reintentos: " + ex);
        }
    }
}
