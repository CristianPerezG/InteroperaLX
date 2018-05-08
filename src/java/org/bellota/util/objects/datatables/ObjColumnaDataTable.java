/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.util.objects.datatables;

import java.util.List;

/**
 *
 * @author Cristian Alberto Perez
 */
public class ObjColumnaDataTable {

    private String nombreColumna;
    private String dataColumna;
    private ObjBuscarDataTable busquedaColumna;

    public String getNombreColumna() {
        return nombreColumna;
    }

    public void setNombreColumna(String nombreColumna) {
        this.nombreColumna = nombreColumna;
    }

    public String getDataColumna() {
        return dataColumna;
    }

    public void setDataColumna(String dataColumna) {
        this.dataColumna = dataColumna;
    }

    public ObjBuscarDataTable getBusquedaColumna() {
        return busquedaColumna;
    }

    public void setBusquedaColumna(ObjBuscarDataTable busquedaColumna) {
        this.busquedaColumna = busquedaColumna;
    } 

}
