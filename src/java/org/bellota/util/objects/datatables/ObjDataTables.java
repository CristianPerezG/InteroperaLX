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
public class ObjDataTables {

    private int inicioVista;
    private int cantidadRegistros;
    private ObjOrdenDataTable ordenGlobal;
    private List<ObjColumnaDataTable> columnasVista;
    private ObjBuscarDataTable busquedaGlobal;
    private int paginaSolicitada;

    public int getInicioVista() {
        return inicioVista;
    }

    public void setInicioVista(int inicioVista) {
        this.inicioVista = inicioVista;
    }

    public int getCantidadRegistros() {
        return cantidadRegistros;
    }

    public void setCantidadRegistros(int cantidadRegistros) {
        this.cantidadRegistros = cantidadRegistros;
    }

    public ObjOrdenDataTable getOrdenGlobal() {
        return ordenGlobal;
    }

    public void setOrdenGlobal(ObjOrdenDataTable ordenGlobal) {
        this.ordenGlobal = ordenGlobal;
    }

    public List<ObjColumnaDataTable> getColumnasVista() {
        return columnasVista;
    }

    public void setColumnasVista(List<ObjColumnaDataTable> columnasVista) {
        this.columnasVista = columnasVista;
    }

    public ObjBuscarDataTable getBusquedaGlobal() {
        return busquedaGlobal;
    }

    public void setBusquedaGlobal(ObjBuscarDataTable busquedaGlobal) {
        this.busquedaGlobal = busquedaGlobal;
    }

    public int getPaginaSolicitada() {
        return paginaSolicitada;
    }

    public void setPaginaSolicitada(int paginaSolicitada) {
        this.paginaSolicitada = paginaSolicitada;
    }
}
