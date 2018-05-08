/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.util.objects.WSResponse.pedidos;

import java.util.List;

/**
 *
 * @author Cristian Alberto Perez
 */
public class ObjOrdenPedido {

    private String OrderNumber;
    private String TotalLines;
    private String ErrCode;
    private String ErrDescription;  // Recivido Exitosamente
    private List<ObjLineaPedido> Lines;

    public String getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(String OrderNumber) {
        this.OrderNumber = OrderNumber;
    }

    public String getTotalLines() {
        return TotalLines;
    }

    public void setTotalLines(String TotalLines) {
        this.TotalLines = TotalLines;
    }

    public String getErrCode() {
        return ErrCode;
    }

    public void setErrCode(String ErrCode) {
        this.ErrCode = ErrCode;
    }

    public String getErrDescription() {
        return ErrDescription;
    }

    public void setErrDescription(String ErrDescription) {
        this.ErrDescription = ErrDescription;
    }

    public List<ObjLineaPedido> getLines() {
        return Lines;
    }

    public void setLines(List<ObjLineaPedido> Lines) {
        this.Lines = Lines;
    }
    
}
