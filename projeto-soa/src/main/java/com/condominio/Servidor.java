package com.condominio;

import com.condominio.endpoint.ConsultaDisponibilidadeEndpoint;
import com.condominio.endpoint.ConsultarMinhasReservasEndpoint;
import com.condominio.endpoint.RealizarReservaEndpoint;
import com.condominio.endpoint.RelatorioAdminEndpoint;

import javax.xml.ws.Endpoint;

public class Servidor {

    public static void main(String[] args) {

        String base = "http://localhost:8080/";

        Endpoint.publish(base + "disponibilidade",  new ConsultaDisponibilidadeEndpoint());
        Endpoint.publish(base + "reserva",           new RealizarReservaEndpoint());
        Endpoint.publish(base + "minhasReservas",    new ConsultarMinhasReservasEndpoint());
        Endpoint.publish(base + "relatorio",         new RelatorioAdminEndpoint());

        System.out.println("Servidor rodando. Endpoints disponíveis:");
        System.out.println("  " + base + "disponibilidade?wsdl");
        System.out.println("  " + base + "reserva?wsdl");
        System.out.println("  " + base + "minhasReservas?wsdl");
        System.out.println("  " + base + "relatorio?wsdl");
        System.out.println("\nPressione Ctrl+C para encerrar.");
    }
}
