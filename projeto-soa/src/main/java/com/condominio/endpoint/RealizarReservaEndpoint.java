package com.condominio.endpoint;

import com.condominio.model.IntervaloDatas;
import com.condominio.model.DataReserva;
import com.condominio.service.RealizarReservaService;
import com.condominio.service.RealizarReservaService.ResultadoReserva;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(serviceName = "RealizarReservaService")
public class RealizarReservaEndpoint {

    private RealizarReservaService service = new RealizarReservaService();

    @WebMethod(operationName = "realizarReserva")
    public String realizarReserva(
            @WebParam(name = "cpf")         String cpf,
            @WebParam(name = "espacoId")    int espacoId,
            @WebParam(name = "inicioDia")   int inicioDia,
            @WebParam(name = "inicioMes")   int inicioMes,
            @WebParam(name = "inicioAno")   int inicioAno,
            @WebParam(name = "fimDia")      int fimDia,
            @WebParam(name = "fimMes")      int fimMes,
            @WebParam(name = "fimAno")      int fimAno) {

        IntervaloDatas intervalo = new IntervaloDatas(
            new DataReserva(inicioDia, inicioMes, inicioAno),
            new DataReserva(fimDia, fimMes, fimAno)
        );

        ResultadoReserva resultado = service.reservar(cpf, espacoId, intervalo);
        return resultado.toString();
    }
}
