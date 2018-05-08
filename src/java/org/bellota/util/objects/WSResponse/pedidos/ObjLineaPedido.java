/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.util.objects.WSResponse.pedidos;

/**
 *
 * @author Cristian Alberto Perez
 */
public class ObjLineaPedido {

    private String OrderLineNumber;
    private String DocLineNumber;
    private String LineErrCode;
    private String LineErrDescription;

    public String getOrderLineNumber() {
        return OrderLineNumber;
    }

    public void setOrderLineNumber(String OrderLineNumber) {
        this.OrderLineNumber = OrderLineNumber;
    }

    public String getDocLineNumber() {
        return DocLineNumber;
    }

    public void setDocLineNumber(String DocLineNumber) {
        this.DocLineNumber = DocLineNumber;
    }

    public String getLineErrCode() {
        return LineErrCode;
    }

    public void setLineErrCode(String LineErrCode) {
        this.LineErrCode = LineErrCode;
    }

    public String getLineErrDescription() {
        return LineErrDescription;
    }

    public void setLineErrDescription(String LineErrDescription) {
        this.LineErrDescription = LineErrDescription;
    }

}
