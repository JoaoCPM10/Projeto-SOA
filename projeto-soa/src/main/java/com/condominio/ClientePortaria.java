package com.condominio;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class ClientePortaria {

    private static final String BASE = "http://localhost:8080/";

    public static void main(String[] args) throws Exception {

        System.out.println("========================================");
        System.out.println("  SISTEMA DA PORTARIA");
        System.out.println("========================================\n");

        // -------------------------------------------------------
        // PASSO 1: Consultar disponibilidade geral
        // -------------------------------------------------------
        System.out.println("-- Consultando disponibilidade de 01/07/2026 a 31/07/2026 --");

        String disponibilidade = chamarServico(
            BASE + "disponibilidade?wsdl",
            "ConsultaDisponibilidadeService",
            "consultarDisponibilidade",
            new Object[]{ 1, 7, 2026, 31, 7, 2026 }
        );
        System.out.println(disponibilidade);

        // -------------------------------------------------------
        // PASSO 2: Realizar reserva em nome de um morador
        // -------------------------------------------------------
        System.out.println("-- Realizando reserva do Salão de Festas (id=3) para 15/07/2026 --");

        String reserva = chamarServico(
            BASE + "reserva?wsdl",
            "RealizarReservaService",
            "realizarReserva",
            new Object[]{ "222.333.444-55", 3, 15, 7, 2026, 15, 7, 2026 }
        );
        System.out.println(reserva);

        // -------------------------------------------------------
        // PASSO 3: Consultar reservas de um morador específico
        // -------------------------------------------------------
        System.out.println("-- Consultando reservas do CPF 222.333.444-55 --");

        String minhasReservas = chamarServico(
            BASE + "minhasReservas?wsdl",
            "ConsultarMinhasReservasService",
            "consultarMinhasReservas",
            new Object[]{ "222.333.444-55", 1, 7, 2026, 31, 7, 2026 }
        );
        System.out.println(minhasReservas);

        // -------------------------------------------------------
        // PASSO 4: Gerar relatório administrativo (exclusivo da portaria)
        // -------------------------------------------------------
        System.out.println("-- Gerando relatório de todas as reservas de julho/2026 --");

        String relatorio = chamarServico(
            BASE + "relatorio?wsdl",
            "RelatorioAdminService",
            "gerarRelatorio",
            new Object[]{ 1, 7, 2026, 31, 7, 2026 }
        );
        System.out.println(relatorio);
    }

    // Mesmo método do ClienteMorador, reutilizado aqui
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
