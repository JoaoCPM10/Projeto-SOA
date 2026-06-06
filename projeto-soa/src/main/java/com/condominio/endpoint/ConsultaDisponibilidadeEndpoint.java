package com.condominio.endpoint;

import com.condominio.model.IntervaloDatas;
import com.condominio.model.DataReserva;
import com.condominio.service.ConsultaDisponibilidadeService;
import com.condominio.service.ConsultaDisponibilidadeService.ResultadoConsulta;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(serviceName = "ConsultaDisponibilidadeService")
public class ConsultaDisponibilidadeEndpoint {

    private ConsultaDisponibilidadeService service = new ConsultaDisponibilidadeService();

    @WebMethod(operationName = "consultarDisponibilidade")
    public String consultarDisponibilidade(
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

        ResultadoConsulta resultado = service.consultar(intervalo);
        return resultado.toString();
    }
}
