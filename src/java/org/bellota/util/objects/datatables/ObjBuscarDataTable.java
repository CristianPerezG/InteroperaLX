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
public class ObjBuscarDataTable {

    private String valorBusqueda;
    private boolean activaRegex;

    public String getValorBusqueda() {
        return valorBusqueda;
    }

    public void setValorBusqueda(String valorBusqueda) {
        this.valorBusqueda = valorBusqueda;
    }

    public boolean isActivaRegex() {
        return activaRegex;
    }

    public void setActivaRegex(boolean activaRegex) {
        this.activaRegex = activaRegex;
    }
}
