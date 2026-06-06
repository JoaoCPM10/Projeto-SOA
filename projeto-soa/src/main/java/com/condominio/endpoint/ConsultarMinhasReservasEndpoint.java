package com.condominio.endpoint;

import com.condominio.model.IntervaloDatas;
import com.condominio.model.DataReserva;
import com.condominio.service.ConsultarMinhasReservasService;
import com.condominio.service.ConsultarMinhasReservasService.ResultadoMinhasReservas;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(serviceName = "ConsultarMinhasReservasService")
public class ConsultarMinhasReservasEndpoint {

    private ConsultarMinhasReservasService service = new ConsultarMinhasReservasService();

    @WebMethod(operationName = "consultarMinhasReservas")
    public String consultarMinhasReservas(
            @WebParam(name = "cpf")       String cpf,
            @WebParam(name = "inicioDia") int inicioDia,
            @WebParam(name = "inicioMes") int inicioMes,
            @WebParam(name = "inicioAno") int inicioAno,
            @WebParam(name = "fimDia")    int fimDia,
            @WebParam(name = "fimMes")    int fimMes,
            @WebParam(name = "fimAno")    int fimAno) {

        IntervaloDatas intervalo = new IntervaloDatas(
            new DataReserva(inicioDia, inicioMes, inicioAno),
            new DataReserva(fimDia, fimMes, fimAno)
        );

        ResultadoMinhasReservas resultado = service.consultar(cpf, intervalo);
        return resultado.toString();
    }
}
