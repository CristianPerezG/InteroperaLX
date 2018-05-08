/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.util.objects.WSRequest.productos;

/**
 *
 * @author Cristian Alberto Perez
 */
public class ObjProductoPrecios {
 
    private String Company;
    private String Region;
    private String Precio;
    private String Moneda;
    private String FechaDesde;
    private String FechaHasta;

    public String getCompany() {
        return Company;
    }

    public void setCompany(String Company) {
        this.Company = Company;
    }

    public String getRegion() {
        return Region;
    }

    public void setRegion(String Region) {
        this.Region = Region;
    }

    public String getPrecio() {
        return Precio;
    }

    public void setPrecio(String Precio) {
        this.Precio = Precio;
    }

    public String getMoneda() {
        return Moneda;
    }

    public void setMoneda(String Moneda) {
        this.Moneda = Moneda;
    }

    public String getFechaDesde() {
        return FechaDesde;
    }

    public void setFechaDesde(String FechaDesde) {
        this.FechaDesde = FechaDesde;
    }

    public String getFechaHasta() {
        return FechaHasta;
    }

    public void setFechaHasta(String FechaHasta) {
        this.FechaHasta = FechaHasta;
    }    
    
}
