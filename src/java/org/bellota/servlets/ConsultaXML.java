/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.servlets;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bellota.as400.operators.OperadorAS400;

/**
 *
 * @author Cristian Alberto Perez
 */
public class ConsultaXML extends HttpServlet {

    private ByteArrayInputStream inputStreamBytes;
    private BufferedInputStream buf = null;
    private PrintWriter out;
    private int idTransaccion;
    private String tipoSolicitud;
    private final OperadorAS400 operadorAS400 = new OperadorAS400();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            idTransaccion = Integer.valueOf(request.getParameter("idTransaccion"));
            tipoSolicitud = request.getParameter("tipoSolicitud");
            out = response.getWriter();
            response.setContentType("text/xml");
            inputStreamBytes = new ByteArrayInputStream(operadorAS400.consultarXMLLX(tipoSolicitud, idTransaccion));
            int readBytes = 0;
            while ((readBytes = inputStreamBytes.read()) != -1) {
                out.write(readBytes);
            }
        } catch (Exception ex) {
            
        } finally {
            if (out != null) {
                out.close();
            }
            if (buf != null) {
                buf.close();
            }
            if (operadorAS400.esConexionAbierta()) {
                operadorAS400.cerrarConexion();
            }
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

}
