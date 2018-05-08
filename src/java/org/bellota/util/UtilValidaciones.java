/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bellota.util;

/**
 *
 * @author Cristian Alberto Perez
 */
public class UtilValidaciones {

    private boolean resultadoValidacion;
    private double numeroConvertido = 0;

    public boolean validarBlancoNulo(String campoSolilcitado) {
        resultadoValidacion = campoSolilcitado != null && !campoSolilcitado.isEmpty() && !campoSolilcitado.equals("");
        return resultadoValidacion;
    }

    public boolean validaEntero(String numeroSolcitado) {
        try {
            numeroConvertido = Double.valueOf(numeroSolcitado);
            resultadoValidacion = true;
        } catch (NumberFormatException e) {
            resultadoValidacion = false;
        }
        return resultadoValidacion;
    }

}
