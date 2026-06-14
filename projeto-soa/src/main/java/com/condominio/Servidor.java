package com.condominio;

import com.condominio.command.ConcluirReservasCommand;
import com.condominio.command.ReservaScheduler;
import com.condominio.endpoint.ConsultaDisponibilidadeEndpoint;
import com.condominio.endpoint.ConsultarMinhasReservasEndpoint;
import com.condominio.endpoint.RealizarReservaEndpoint;
import com.condominio.endpoint.RelatorioAdminEndpoint;
import com.condominio.endpoint.CancelarReservaEndpoint;

import javax.xml.ws.Endpoint;

public class Servidor {

    public static void main(String[] args) {

        String base = "http://localhost:8080/";

        // Sobe os quatro endpoints SOAP
        Endpoint.publish(base + "disponibilidade",  new ConsultaDisponibilidadeEndpoint());
        Endpoint.publish(base + "reserva",           new RealizarReservaEndpoint());
        Endpoint.publish(base + "minhasReservas",    new ConsultarMinhasReservasEndpoint());
        Endpoint.publish(base + "relatorio",         new RelatorioAdminEndpoint());
        Endpoint.publish(base + "cancelar",   new CancelarReservaEndpoint());

        System.out.println("Servidor rodando. Endpoints disponíveis:");
        System.out.println("  " + base + "disponibilidade?wsdl");
        System.out.println("  " + base + "reserva?wsdl");
        System.out.println("  " + base + "minhasReservas?wsdl");
        System.out.println("  " + base + "relatorio?wsdl");
        System.out.println("  " + base + "cancelar?wsdl");

        // Inicia o scheduler do padrão Command
        ReservaScheduler scheduler = new ReservaScheduler(new ConcluirReservasCommand());
        scheduler.agendarComando();

        // Garante que o scheduler para quando o servidor for encerrado
        Runtime.getRuntime().addShutdownHook(new Thread(scheduler::parar));

        System.out.println("\nPressione Ctrl+C para encerrar.");
    }
}