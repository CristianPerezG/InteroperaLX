/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.as400.LXConectors;

import com.infor.lx.xmg.bean.LxIntegratorConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bellota.util.objects.ObjConexionLXC;
import org.bellota.config.PropiedadesAPP;

/**
 *
 * @author Cristian Alberto Perez
 */
public class ConexionLXC {

    private String instanciaProceso;
    private LxIntegratorConnection conectorIntegracion;
    private String msjError = null, hostConector, libreriaConector, passConector;
    private ObjConexionLXC objLogueo = new ObjConexionLXC();
    private PropiedadesAPP utilPropiedades = new PropiedadesAPP();
    private int puertoConector;

    public ConexionLXC() {
        try {
            instanciaProceso = utilPropiedades.obtenerPropiedades().getProperty("InstanciaConectorLXPA");
            System.out.println("Instancia Remota: " + instanciaProceso);
            conectorIntegracion = new LxIntegratorConnection(instanciaProceso);
        } catch (Exception ex) {
            this.msjError = ex.getMessage();
        }
    }

    public void generarConexion() {

        System.out.println("Instancia Local: " + instanciaProceso.replace("/instance.xml", "").replace("file:/", ""));

        hostConector = utilPropiedades.obtenerPropiedades().getProperty("HostConectorLXP");
        libreriaConector = utilPropiedades.obtenerPropiedades().getProperty("ECLibrary");
        passConector = utilPropiedades.obtenerPropiedades().getProperty("PassConectorLXP");
        puertoConector = Integer.valueOf(utilPropiedades.obtenerPropiedades().getProperty("PuertoConectorLXP"));

        conectorIntegracion.setPass(passConector);
        conectorIntegracion.logon(puertoConector, hostConector, libreriaConector);
        conectorIntegracion.setWaitForReply(true);

        if (conectorIntegracion.isLoggedon()) {

            System.out.println(conectorIntegracion.getReplyAsString());
            System.out.println(conectorIntegracion.getHome());
            System.out.println(conectorIntegracion.getInstance());
            System.out.println(conectorIntegracion.getStatus());

            objLogueo.setRespuestaConector(conectorIntegracion.getReplyAsString());
            objLogueo.setHomeConector(conectorIntegracion.getHome());
            objLogueo.setInstanaciaConector(conectorIntegracion.getInstance());

        } else {
            msjError = "Error de Logue en Conector " + conectorIntegracion.getReplyAsString();
        }
    }

    public String obtenerError() {
        return this.msjError;
    }

    public LxIntegratorConnection obtenerConector() {
        return this.conectorIntegracion;
    }

    public void desloguearConector() {
        try {
            conectorIntegracion.disconnect();
        } catch (Exception ex) {
            Logger.getLogger(ConexionLXC.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error CERRANDO la CONEXION CON LX: \n" + ex.toString());
        }
    }

}
