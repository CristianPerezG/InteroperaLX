/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.util.objects.datatables;

/**
 *
 * @author Cristian Alberto Perez
 */
public class ObjOrdenDataTable {
 
    private int indexColumna;
    private String direccionOrden;
    private String nombreColumnaSQL;

    public int getIndexColumna() {
        return indexColumna;
    }

    public void setIndexColumna(int indexColumna) {
        this.indexColumna = indexColumna;
    }

    public String getDireccionOrden() {
        return direccionOrden;
    }

    public void setDireccionOrden(String direccionOrden) {
        this.direccionOrden = direccionOrden;
    }

    public String getNombreColumnaSQL() {
        return nombreColumnaSQL;
    }

    public void setNombreColumnaSQL(String nombreColumnaSQL) {
        this.nombreColumnaSQL = nombreColumnaSQL;
    }    
    
}
