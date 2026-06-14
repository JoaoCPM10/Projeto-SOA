package com.condominio.endpoint;

import com.condominio.service.CancelarReservaService;
import com.condominio.service.CancelarReservaService.ResultadoCancelamento;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(serviceName = "CancelarReservaService")
public class CancelarReservaEndpoint {

    private CancelarReservaService service = new CancelarReservaService();

    @WebMethod(operationName = "cancelarReserva")
    public String cancelarReserva(
            @WebParam(name = "cpf")       String cpf,
            @WebParam(name = "idReserva") long idReserva) {

        ResultadoCancelamento resultado = service.cancelar(cpf, idReserva);
        return resultado.toString();
    }
}