/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.util.objects.WSRequest;

import java.util.List;
import org.bellota.util.objects.WSRequest.productos.ObjProductoEAN;
import org.bellota.util.objects.WSRequest.productos.ObjProductoPrecios;

/**
 *
 * @author Cristian Alberto Perez
 */
public class ReqMaestrosWS {

    private String Codigo;
    private String Descripcion;
    private String DescripcionExtra;
    private String TipoProducto;
    private String ClaseProducto;
    private String UMStock;
    private String LoteControlado;
    private String SegmentoCostos;
    private String GrupoImpositivo;
    private String Linea;
    private String Destino;
    private String USO;
    private String Referencia4;
    private String Sector;
    private String MPS;
    private String LeadTime;
    private String DiasDemanda;
    private String CodDemanda1;
    private String CodDemanda2;
    private String InvMinimo;
    private String MedidaLote;
    private String IncrementoLote;
    private String PoliticaPlaneacion;
    private String Instalacion;
    private String BodegaInventarios;
    private String BodegaPlaneacion;
    private String CodigoABC;
    private String UMCompra;
    private String UMVenta;

    private String GrupoTecnico1;
    private String CodigoBracket;

    private String ProvPrincipal;

    private String ProvAlternativo;
    private String PaisOrigen;
    private String InvMax;
    private String DiasHorizonte;
    private String CodNecesidadesPlaneacion;
    private String TasaDiariaAprovisionamiento;
    private String CantAnualObj;
    private String DiasHorizonteMin;
    private String DiasExistencia;
    private String PuntoCritico;
    private String EmpaqueDefecto;
    private String FamiliaComunHT;
    private String UNMinVenta;
    private String EnvioDirecto;
    private String ContraStock;
    private String UnEmpaque;
    private String PesoNeto;
    private String PesoCaja;
    private String UMPeso;
    private String Largo;
    private String UMLargo;
    private String Ancho;
    private String UMAncho;
    private String Alto;
    private String UMAlto;
    private String NoPlano;
    private List<ObjProductoEAN> EAN;
    private List<ObjProductoPrecios> Precios;

    public String getCodigo() {
        return Codigo;
    }

    public void setCodigo(String Codigo) {
        this.Codigo = Codigo;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String Descripcion) {
        this.Descripcion = Descripcion;
    }

    public String getDescripcionExtra() {
        return DescripcionExtra;
    }

    public void setDescripcionExtra(String DescripcionExtra) {
        this.DescripcionExtra = DescripcionExtra;
    }

    public String getTipoProducto() {
        return TipoProducto;
    }

    public void setTipoProducto(String TipoProducto) {
        this.TipoProducto = TipoProducto;
    }

    public String getClaseProducto() {
        return ClaseProducto;
    }

    public void setClaseProducto(String ClaseProducto) {
        this.ClaseProducto = ClaseProducto;
    }

    public String getUMStock() {
        return UMStock;
    }

    public void setUMStock(String UMStock) {
        this.UMStock = UMStock;
    }

    public String getLoteControlado() {
        return LoteControlado;
    }

    public void setLoteControlado(String LoteControlado) {
        this.LoteControlado = LoteControlado;
    }

    public String getSegmentoCostos() {
        return SegmentoCostos;
    }

    public void setSegmentoCostos(String SegmentoCostos) {
        this.SegmentoCostos = SegmentoCostos;
    }

    public String getGrupoImpositivo() {
        return GrupoImpositivo;
    }

    public void setGrupoImpositivo(String GrupoImpositivo) {
        this.GrupoImpositivo = GrupoImpositivo;
    }

    public String getLinea() {
        return Linea;
    }

    public void setLinea(String Linea) {
        this.Linea = Linea;
    }

    public String getDestino() {
        return Destino;
    }

    public void setDestino(String Destino) {
        this.Destino = Destino;
    }

    public String getUSO() {
        return USO;
    }

    public void setUSO(String USO) {
        this.USO = USO;
    }

    public String getReferencia4() {
        return Referencia4;
    }

    public void setReferencia4(String Referencia4) {
        this.Referencia4 = Referencia4;
    }

    public String getSector() {
        return Sector;
    }

    public void setSector(String Sector) {
        this.Sector = Sector;
    }

    public String getMPS() {
        return MPS;
    }

    public void setMPS(String MPS) {
        this.MPS = MPS;
    }

    public String getLeadTime() {
        return LeadTime;
    }

    public void setLeadTime(String LeadTime) {
        this.LeadTime = LeadTime;
    }

    public String getDiasDemanda() {
        return DiasDemanda;
    }

    public void setDiasDemanda(String DiasDemanda) {
        this.DiasDemanda = DiasDemanda;
    }

    public String getCodDemanda1() {
        return CodDemanda1;
    }

    public void setCodDemanda1(String CodDemanda1) {
        this.CodDemanda1 = CodDemanda1;
    }

    public String getCodDemanda2() {
        return CodDemanda2;
    }

    public void setCodDemanda2(String CodDemanda2) {
        this.CodDemanda2 = CodDemanda2;
    }

    public String getInvMinimo() {
        return InvMinimo;
    }

    public void setInvMinimo(String InvMinimo) {
        this.InvMinimo = InvMinimo;
    }

    public String getMedidaLote() {
        return MedidaLote;
    }

    public void setMedidaLote(String MedidaLote) {
        this.MedidaLote = MedidaLote;
    }

    public String getIncrementoLote() {
        return IncrementoLote;
    }

    public void setIncrementoLote(String IncrementoLote) {
        this.IncrementoLote = IncrementoLote;
    }

    public String getPoliticaPlaneacion() {
        return PoliticaPlaneacion;
    }

    public void setPoliticaPlaneacion(String PoliticaPlaneacion) {
        this.PoliticaPlaneacion = PoliticaPlaneacion;
    }

    public String getInstalacion() {
        return Instalacion;
    }

    public void setInstalacion(String Instalacion) {
        this.Instalacion = Instalacion;
    }

    public String getBodegaInventarios() {
        return BodegaInventarios;
    }

    public void setBodegaInventarios(String BodegaInventarios) {
        this.BodegaInventarios = BodegaInventarios;
    }

    public String getBodegaPlaneacion() {
        return BodegaPlaneacion;
    }

    public void setBodegaPlaneacion(String BodegaPlaneacion) {
        this.BodegaPlaneacion = BodegaPlaneacion;
    }

    public String getCodigoABC() {
        return CodigoABC;
    }

    public void setCodigoABC(String CodigoABC) {
        this.CodigoABC = CodigoABC;
    }

    public String getUMCompra() {
        return UMCompra;
    }

    public void setUMCompra(String UMCompra) {
        this.UMCompra = UMCompra;
    }

    public String getUMVenta() {
        return UMVenta;
    }

    public void setUMVenta(String UMVenta) {
        this.UMVenta = UMVenta;
    }

    public String getGrupoTecnico1() {
        return GrupoTecnico1;
    }

    public void setGrupoTecnico1(String GrupoTecnico1) {
        this.GrupoTecnico1 = GrupoTecnico1;
    }

    public String getCodigoBracket() {
        return CodigoBracket;
    }

    public void setCodigoBracket(String CodigoBracket) {
        this.CodigoBracket = CodigoBracket;
    }

    public String getProvPrincipal() {
        return ProvPrincipal;
    }

    public void setProvPrincipal(String ProvPrincipal) {
        this.ProvPrincipal = ProvPrincipal;
    }

    public String getProvAlternativo() {
        return ProvAlternativo;
    }

    public void setProvAlternativo(String ProvAlternativo) {
        this.ProvAlternativo = ProvAlternativo;
    }

    public String getPaisOrigen() {
        return PaisOrigen;
    }

    public void setPaisOrigen(String PaisOrigen) {
        this.PaisOrigen = PaisOrigen;
    }

    public String getInvMax() {
        return InvMax;
    }

    public void setInvMax(String InvMax) {
        this.InvMax = InvMax;
    }

    public String getDiasHorizonte() {
        return DiasHorizonte;
    }

    public void setDiasHorizonte(String DiasHorizonte) {
        this.DiasHorizonte = DiasHorizonte;
    }

    public String getCodNecesidadesPlaneacion() {
        return CodNecesidadesPlaneacion;
    }

    public void setCodNecesidadesPlaneacion(String CodNecesidadesPlaneacion) {
        this.CodNecesidadesPlaneacion = CodNecesidadesPlaneacion;
    }

    public String getTasaDiariaAprovisionamiento() {
        return TasaDiariaAprovisionamiento;
    }

    public void setTasaDiariaAprovisionamiento(String TasaDiariaAprovisionamiento) {
        this.TasaDiariaAprovisionamiento = TasaDiariaAprovisionamiento;
    }

    public String getCantAnualObj() {
        return CantAnualObj;
    }

    public void setCantAnualObj(String CantAnualObj) {
        this.CantAnualObj = CantAnualObj;
    }

    public String getDiasHorizonteMin() {
        return DiasHorizonteMin;
    }

    public void setDiasHorizonteMin(String DiasHorizonteMin) {
        this.DiasHorizonteMin = DiasHorizonteMin;
    }

    public String getDiasExistencia() {
        return DiasExistencia;
    }

    public void setDiasExistencia(String DiasExistencia) {
        this.DiasExistencia = DiasExistencia;
    }

    public String getPuntoCritico() {
        return PuntoCritico;
    }

    public void setPuntoCritico(String PuntoCritico) {
        this.PuntoCritico = PuntoCritico;
    }

    public String getEmpaqueDefecto() {
        return EmpaqueDefecto;
    }

    public void setEmpaqueDefecto(String EmpaqueDefecto) {
        this.EmpaqueDefecto = EmpaqueDefecto;
    }

    public String getFamiliaComunHT() {
        return FamiliaComunHT;
    }

    public void setFamiliaComunHT(String FamiliaComunHT) {
        this.FamiliaComunHT = FamiliaComunHT;
    }

    public String getUNMinVenta() {
        return UNMinVenta;
    }

    public void setUNMinVenta(String UNMinVenta) {
        this.UNMinVenta = UNMinVenta;
    }

    public String getEnvioDirecto() {
        return EnvioDirecto;
    }

    public void setEnvioDirecto(String EnvioDirecto) {
        this.EnvioDirecto = EnvioDirecto;
    }

    public String getContraStock() {
        return ContraStock;
    }

    public void setContraStock(String ContraStock) {
        this.ContraStock = ContraStock;
    }

    public String getUnEmpaque() {
        return UnEmpaque;
    }

    public void setUnEmpaque(String UnEmpaque) {
        this.UnEmpaque = UnEmpaque;
    }

    public String getPesoNeto() {
        return PesoNeto;
    }

    public void setPesoNeto(String PesoNeto) {
        this.PesoNeto = PesoNeto;
    }

    public String getPesoCaja() {
        return PesoCaja;
    }

    public void setPesoCaja(String PesoCaja) {
        this.PesoCaja = PesoCaja;
    }

    public String getUMPeso() {
        return UMPeso;
    }

    public void setUMPeso(String UMPeso) {
        this.UMPeso = UMPeso;
    }

    public String getLargo() {
        return Largo;
    }

    public void setLargo(String Largo) {
        this.Largo = Largo;
    }

    public String getUMLargo() {
        return UMLargo;
    }

    public void setUMLargo(String UMLargo) {
        this.UMLargo = UMLargo;
    }

    public String getAncho() {
        return Ancho;
    }

    public void setAncho(String Ancho) {
        this.Ancho = Ancho;
    }

    public String getUMAncho() {
        return UMAncho;
    }

    public void setUMAncho(String UMAncho) {
        this.UMAncho = UMAncho;
    }

    public String getAlto() {
        return Alto;
    }

    public void setAlto(String Alto) {
        this.Alto = Alto;
    }

    public String getUMAlto() {
        return UMAlto;
    }

    public void setUMAlto(String UMAlto) {
        this.UMAlto = UMAlto;
    }

    public String getNoPlano() {
        return NoPlano;
    }

    public void setNoPlano(String NoPlano) {
        this.NoPlano = NoPlano;
    }

    public List<ObjProductoEAN> getEAN() {
        return EAN;
    }

    public void setEAN(List<ObjProductoEAN> EAN) {
        this.EAN = EAN;
    }

    public List<ObjProductoPrecios> getPrecios() {
        return Precios;
    }

    public void setPrecios(List<ObjProductoPrecios> Precios) {
        this.Precios = Precios;
    }
}
