package com.condominio.endpoint;

import com.condominio.model.IntervaloDatas;
import com.condominio.model.DataReserva;
import com.condominio.service.RelatorioAdminService;
import com.condominio.service.RelatorioAdminService.ResultadoRelatorio;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(serviceName = "RelatorioAdminService")
public class RelatorioAdminEndpoint {

    private RelatorioAdminService service = new RelatorioAdminService();

    @WebMethod(operationName = "gerarRelatorio")
    public String gerarRelatorio(
            @WebParam(name = "inicioDia") int inicioDia,
            @WebParam(name = "inicioMes") int inicioMes,
            @WebParam(name = "inicioAno") int inicioAno,
            @WebParam(name = "fimDia")    int fimDia,
            @WebParam(name = "fimMes")    int fimMes,
            @WebParam(name = "fimAno")    int fimAno,
            @WebParam(name = "status")    String status) {

        IntervaloDatas intervalo = new IntervaloDatas(
            new DataReserva(inicioDia, inicioMes, inicioAno),
            new DataReserva(fimDia, fimMes, fimAno)
        );

        ResultadoRelatorio resultado = service.gerar(intervalo, status);
        return resultado.toString();
    }

    @WebMethod(operationName = "gerarRelatorioCompleto")
    public String gerarRelatorioCompleto(
            @WebParam(name = "status") String status) {

        ResultadoRelatorio resultado = service.gerarCompleto(status);
        return resultado.toString();
    }
}