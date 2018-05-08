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
public class ResPedidosWS {

    private String BidContractId;
    private String TotalOrders;
    private String idLog;
    private List<ObjOrdenPedido> Orders;

    public String getBidContractId() {
        return BidContractId;
    }

    public void setBidContractId(String BidContractId) {
        this.BidContractId = BidContractId;
    }

    public String getTotalOrders() {
        return TotalOrders;
    }

    public void setTotalOrders(String TotalOrders) {
        this.TotalOrders = TotalOrders;
    }

    public List<ObjOrdenPedido> getOrders() {
        return Orders;
    }

    public void setOrders(List<ObjOrdenPedido> Orders) {
        this.Orders = Orders;
    }

    public String getIdLog() {
        return idLog;
    }

    public void setIdLog(String idLog) {
        this.idLog = idLog;
    }
}
