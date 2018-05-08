/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.servlets;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bellota.as400.operators.OperadorAS400;

/**
 *
 * @author Cristian Alberto Perez
 */
public class QuerysCSV extends HttpServlet {

    private final OperadorAS400 operadoAS400 = new OperadorAS400();
    private final DateFormat formatoFecha = new SimpleDateFormat("yyyyMMdd-hhmmssSSS");
    
    private String separadorProceso, querySolicitado, textoCSV;
    private Date fechaActual;

    private File nuevoCSV;
    private FileWriter escritorArchivo;
    private FileInputStream streamEntradaArchivos;

    private ServletOutputStream streamSalidaServlet;

    private ZipOutputStream streamZIPSalida;

    private byte bytesArchivo[];
    private ByteArrayOutputStream arregloBytesSalida;

    private BufferedInputStream bufferStreamEntrada;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {

            separadorProceso = request.getParameter("separadorProceso");
            querySolicitado = request.getParameter("querySolicitado");

            fechaActual = new Date();
            textoCSV = operadoAS400.crearCSVQuery(querySolicitado, separadorProceso);
            nuevoCSV = new File("ArchivoCSV" + formatoFecha.format(fechaActual) + ".csv");
            escritorArchivo = new FileWriter(nuevoCSV, true);
            escritorArchivo.write(textoCSV);
            escritorArchivo.flush();

            if (escritorArchivo != null) {
                escritorArchivo.close();
            }

            streamSalidaServlet = response.getOutputStream();
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=\"ArchivoZIP" + formatoFecha.format(fechaActual) + ".zip\"");
            streamSalidaServlet.write(generarZIP(nuevoCSV));
            streamSalidaServlet.flush();

        } catch (Exception ex) {
            Logger.getLogger(OperadorAS400.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error generando la consulta de Dinamica de CSV: \n" + ex.toString());
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private byte[] generarZIP(File archivoCSV) {

        arregloBytesSalida = new ByteArrayOutputStream();

        try {
            streamZIPSalida = new ZipOutputStream(arregloBytesSalida);
            bytesArchivo = new byte[2048];
            streamEntradaArchivos = new FileInputStream(archivoCSV);
            bufferStreamEntrada = new BufferedInputStream(streamEntradaArchivos);
            streamZIPSalida.putNextEntry(new ZipEntry(archivoCSV.getName()));

            int bytesRead;

            while ((bytesRead = bufferStreamEntrada.read(bytesArchivo)) != -1) {
                streamZIPSalida.write(bytesArchivo, 0, bytesRead);
            }

            streamZIPSalida.closeEntry();
            bufferStreamEntrada.close();
            streamEntradaArchivos.close();

            streamZIPSalida.flush();
            arregloBytesSalida.flush();
            streamZIPSalida.close();
            arregloBytesSalida.close();

        } catch (IOException ex) {
            Logger.getLogger(OperadorAS400.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error generando ZIP de CSV: \n" + ex.toString());
        }

        return arregloBytesSalida.toByteArray();
    }

}
