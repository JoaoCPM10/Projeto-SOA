package com.condominio;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class ClienteMorador {

    // Endereço base do servidor
    private static final String BASE = "http://localhost:8080/";

    public static void main(String[] args) throws Exception {

        System.out.println("========================================");
        System.out.println("  APP DO MORADOR");
        System.out.println("========================================\n");

        // -------------------------------------------------------
        // PASSO 1: Consultar disponibilidade dos espaços
        // -------------------------------------------------------
        System.out.println("-- Consultando disponibilidade de 10/07/2026 a 10/07/2026 --");

        String resultadoDisponibilidade = chamarServico(
            BASE + "disponibilidade?wsdl",
            "ConsultaDisponibilidadeService",
            "consultarDisponibilidade",
            new Object[]{ 10, 7, 2026, 10, 7, 2026 }
        );
        System.out.println(resultadoDisponibilidade);

        // -------------------------------------------------------
        // PASSO 2: Realizar uma reserva
        // -------------------------------------------------------
        System.out.println("-- Realizando reserva na Churrasqueira 1 (id=1) para 10/07/2026 --");

        String resultadoReserva = chamarServico(
            BASE + "reserva?wsdl",
            "RealizarReservaService",
            "realizarReserva",
            new Object[]{ "111.222.333-44", 1, 10, 7, 2026, 10, 7, 2026 }
        );
        System.out.println(resultadoReserva);

        // -------------------------------------------------------
        // PASSO 3: Tentar reservar o mesmo espaço e período (deve dar conflito)
        // -------------------------------------------------------
        System.out.println("-- Tentando reservar o mesmo espaço e período novamente --");

        String resultadoConflito = chamarServico(
            BASE + "reserva?wsdl",
            "RealizarReservaService",
            "realizarReserva",
            new Object[]{ "999.888.777-66", 1, 10, 7, 2026, 10, 7, 2026 }
        );
        System.out.println(resultadoConflito);

        // -------------------------------------------------------
        // PASSO 4: Consultar minhas reservas
        // -------------------------------------------------------
        System.out.println("-- Consultando reservas do CPF 111.222.333-44 em julho/2026 --");

        String resultadoMinhasReservas = chamarServico(
            BASE + "minhasReservas?wsdl",
            "ConsultarMinhasReservasService",
            "consultarMinhasReservas",
            new Object[]{ "111.222.333-44", 1, 7, 2026, 31, 7, 2026 }
        );
        System.out.println(resultadoMinhasReservas);
    }

    // Método genérico que conecta em um endpoint SOAP e chama uma operação
    private static String chamarServico(String wsdlUrl, String serviceName,
                                    String operationName, Object[] parametros) {
    try {
        URL url     = new URL(wsdlUrl);
        QName qname = new QName("http://endpoint.condominio.com/", serviceName);

        Service service = Service.create(url, qname);
        javax.xml.ws.Dispatch<javax.xml.transform.Source> dispatch = service.createDispatch(
            new QName("http://endpoint.condominio.com/",
                      serviceName.replace("Service", "Endpoint") + "Port"),
            javax.xml.transform.Source.class,
            Service.Mode.PAYLOAD
        );

        // Monta o XML da requisição
        StringBuilder xml = new StringBuilder();
        xml.append("<ns2:").append(operationName)
           .append(" xmlns:ns2=\"http://endpoint.condominio.com/\">");

        String[] nomes = obterNomesParametros(operationName);
        for (int i = 0; i < parametros.length; i++) {
            xml.append("<").append(nomes[i]).append(">")
               .append(parametros[i])
               .append("</").append(nomes[i]).append(">");
        }
        xml.append("</ns2:").append(operationName).append(">");

        javax.xml.transform.Source source = new javax.xml.transform.stream.StreamSource(
            new java.io.StringReader(xml.toString())
        );

        javax.xml.transform.Source resposta = dispatch.invoke(source);

        // Converte a resposta XML em texto
        javax.xml.transform.TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
        javax.xml.transform.Transformer transformer = tf.newTransformer();
        java.io.StringWriter writer = new java.io.StringWriter();
        transformer.transform(resposta, new javax.xml.transform.stream.StreamResult(writer));

        return writer.toString();

    } catch (Exception e) {
        return "ERRO ao chamar " + operationName + ": " + e.getMessage();
    }
}

    // Retorna os nomes dos parâmetros de cada operação na ordem correta
    private static String[] obterNomesParametros(String operationName) {
        switch (operationName) {
            case "realizarReserva":
                return new String[]{ "cpf", "espacoId", "inicioDia", "inicioMes", "inicioAno",
                                     "fimDia", "fimMes", "fimAno" };
            case "consultarDisponibilidade":
                return new String[]{ "inicioDia", "inicioMes", "inicioAno",
                                     "fimDia", "fimMes", "fimAno" };
            case "consultarMinhasReservas":
                return new String[]{ "cpf", "inicioDia", "inicioMes", "inicioAno",
                                     "fimDia", "fimMes", "fimAno" };
            case "gerarRelatorio":
                return new String[]{ "inicioDia", "inicioMes", "inicioAno",
                                     "fimDia", "fimMes", "fimAno" };
            default:
                return new String[]{};
        }
    }
}
