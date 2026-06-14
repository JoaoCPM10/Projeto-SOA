package com.condominio;

import javax.swing.*;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import java.awt.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.time.LocalDate;
import java.util.Map;

public class Interface extends JFrame {

    private static final String BASE = "http://localhost:8080/";

    public Interface() {
        setTitle("Sistema de Reservas - Condomínio");
        setSize(700, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("App do Morador", criarAbaMorador());
        abas.addTab("Sistema da Portaria", criarAbaPortaria());

        add(abas);
        setVisible(true);
    }

    // -------------------------------------------------------
    // VALIDAÇÕES COMPARTILHADAS
    // -------------------------------------------------------
    private String validarDatas(String inicioDia, String inicioMes, String inicioAno,
                                String fimDia,    String fimMes,    String fimAno) {
        if (inicioDia.isBlank() || inicioMes.isBlank() || inicioAno.isBlank() ||
            fimDia.isBlank()    || fimMes.isBlank()    || fimAno.isBlank()) {
            return "ERRO: Preencha todos os campos de data.";
        }

        int iDia, iMes, iAno, fDia, fMes, fAno;
        try {
            iDia = Integer.parseInt(inicioDia);
            iMes = Integer.parseInt(inicioMes);
            iAno = Integer.parseInt(inicioAno);
            fDia = Integer.parseInt(fimDia);
            fMes = Integer.parseInt(fimMes);
            fAno = Integer.parseInt(fimAno);
        } catch (NumberFormatException e) {
            return "ERRO: Os campos de data devem conter apenas números.";
        }

        if (iDia < 1 || iDia > 31 || fDia < 1 || fDia > 31) {
            return "ERRO: Dia inválido. Use valores entre 1 e 31.";
        }
        if (iMes < 1 || iMes > 12 || fMes < 1 || fMes > 12) {
            return "ERRO: Mês inválido. Use valores entre 1 e 12.";
        }
        if (iAno < 2000 || fAno < 2000) {
            return "ERRO: Ano inválido. Use um ano a partir de 2000.";
        }

        int dataInicio = iAno * 10000 + iMes * 100 + iDia;
        int dataFim    = fAno * 10000 + fMes * 100 + fDia;

        if (dataFim < dataInicio) {
            return "ERRO: A data de fim não pode ser anterior à data de início.";
        }

        return null;
    }

    // Preenche os campos de data com a data atual
    private void preencherDataAtual(JTextField dia, JTextField mes, JTextField ano) {
        LocalDate hoje = LocalDate.now();
        dia.setText(String.valueOf(hoje.getDayOfMonth()));
        mes.setText(String.valueOf(hoje.getMonthValue()));
        ano.setText(String.valueOf(hoje.getYear()));
    }

    private JPanel criarAbaMorador() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTabbedPane operacoes = new JTabbedPane();
        operacoes.addTab("Consultar Disponibilidade", criarPainelDisponibilidade());
        operacoes.addTab("Realizar Reserva",           criarPainelRealizarReserva());
        operacoes.addTab("Minhas Reservas",            criarPainelMinhasReservas());
        operacoes.addTab("Cancelar Reserva",           criarPainelCancelarReserva());

        painel.add(operacoes);
        return painel;
    }

    private JPanel criarAbaPortaria() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTabbedPane operacoes = new JTabbedPane();
        operacoes.addTab("Consultar Disponibilidade", criarPainelDisponibilidade());
        operacoes.addTab("Realizar Reserva",           criarPainelRealizarReserva());
        operacoes.addTab("Minhas Reservas",            criarPainelMinhasReservas());
        operacoes.addTab("Relatório Admin",            criarPainelRelatorio());
        operacoes.addTab("Cancelar Reserva",           criarPainelCancelarReserva());

        painel.add(operacoes);
        return painel;
    }

    // -------------------------------------------------------
    // PAINEL: CONSULTAR DISPONIBILIDADE
    // -------------------------------------------------------
    private JPanel criarPainelDisponibilidade() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel campos = new JPanel(new GridLayout(3, 4, 5, 5));

        campos.add(new JLabel("Início (dia/mês/ano):"));
        JTextField inicioDia = new JTextField();
        JTextField inicioMes = new JTextField();
        JTextField inicioAno = new JTextField();
        campos.add(inicioDia);
        campos.add(inicioMes);
        campos.add(inicioAno);

        campos.add(new JLabel("Fim (dia/mês/ano):"));
        JTextField fimDia = new JTextField();
        JTextField fimMes = new JTextField();
        JTextField fimAno = new JTextField();
        campos.add(fimDia);
        campos.add(fimMes);
        campos.add(fimAno);

        JButton btnConsultar = new JButton("Consultar Disponibilidade");
        campos.add(btnConsultar);
        campos.add(new JLabel());
        campos.add(new JLabel());
        campos.add(new JLabel());

        // Preenche com a data atual
        preencherDataAtual(inicioDia, inicioMes, inicioAno);
        preencherDataAtual(fimDia, fimMes, fimAno);

        JTextArea resultado = new JTextArea(10, 50);
        resultado.setEditable(false);
        resultado.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(resultado);

        btnConsultar.addActionListener(e -> {
            String erroData = validarDatas(
                inicioDia.getText(), inicioMes.getText(), inicioAno.getText(),
                fimDia.getText(),    fimMes.getText(),    fimAno.getText()
            );
            if (erroData != null) {
                resultado.setText(erroData);
                return;
            }
            try {
                String resposta = chamarServico(
                    BASE + "disponibilidade?wsdl",
                    "ConsultaDisponibilidadeService",
                    "consultarDisponibilidade",
                    new Object[]{
                        Integer.parseInt(inicioDia.getText()),
                        Integer.parseInt(inicioMes.getText()),
                        Integer.parseInt(inicioAno.getText()),
                        Integer.parseInt(fimDia.getText()),
                        Integer.parseInt(fimMes.getText()),
                        Integer.parseInt(fimAno.getText())
                    }
                );
                resultado.setText(resposta);
            } catch (Exception ex) {
                resultado.setText("ERRO: " + ex.getMessage());
            }
        });

        painel.add(campos, BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);
        return painel;
    }

    // -------------------------------------------------------
    // PAINEL: REALIZAR RESERVA
    // -------------------------------------------------------
    private JPanel criarPainelRealizarReserva() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel campos = new JPanel(new GridLayout(6, 4, 5, 5));

        campos.add(new JLabel("CPF:"));
        JTextField cpf = new JTextField();
        campos.add(cpf);
        campos.add(new JLabel());
        campos.add(new JLabel());

        campos.add(new JLabel("Nome:"));
        JTextField nome = new JTextField();
        campos.add(nome);
        campos.add(new JLabel());
        campos.add(new JLabel());

        campos.add(new JLabel("Espaço:"));
        JComboBox<String> espacoCombo = new JComboBox<>();
        Map<String, Integer> mapaEspacos = new java.util.LinkedHashMap<>();

        try {
            String resposta = chamarServico(
                BASE + "disponibilidade?wsdl",
                "ConsultaDisponibilidadeService",
                "consultarDisponibilidade",
                new Object[]{ 1, 1, 2020, 31, 12, 2030 }
            );
            int[] idCounter = {1};
            for (String linha : resposta.split("\n")) {
                linha = linha.trim();
                if (linha.startsWith("Espaco:")) {
                    String nomeLinha = linha.replace("Espaco:", "").split("\\|")[0].trim();
                    mapaEspacos.put(nomeLinha, idCounter[0]++);
                    espacoCombo.addItem(nomeLinha);
                }
            }
            if (espacoCombo.getItemCount() == 0) {
                espacoCombo.addItem("Nenhum espaço encontrado");
            }
        } catch (Exception ex) {
            espacoCombo.addItem("Erro ao carregar espaços");
            System.out.println("[DEBUG] Exceção: " + ex.getMessage());
        }

        campos.add(espacoCombo);
        campos.add(new JLabel());
        campos.add(new JLabel());

        campos.add(new JLabel("Início (dia/mês/ano):"));
        JTextField inicioDia = new JTextField();
        JTextField inicioMes = new JTextField();
        JTextField inicioAno = new JTextField();
        campos.add(inicioDia);
        campos.add(inicioMes);
        campos.add(inicioAno);

        campos.add(new JLabel("Fim (dia/mês/ano):"));
        JTextField fimDia = new JTextField();
        JTextField fimMes = new JTextField();
        JTextField fimAno = new JTextField();
        campos.add(fimDia);
        campos.add(fimMes);
        campos.add(fimAno);

        JButton btnReservar = new JButton("Realizar Reserva");
        campos.add(btnReservar);
        campos.add(new JLabel());
        campos.add(new JLabel());
        campos.add(new JLabel());

        // Preenche com a data atual
        preencherDataAtual(inicioDia, inicioMes, inicioAno);
        preencherDataAtual(fimDia, fimMes, fimAno);

        JTextArea resultado = new JTextArea(10, 50);
        resultado.setEditable(false);
        resultado.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(resultado);

        btnReservar.addActionListener(e -> {
            if (cpf.getText().isBlank()) {
                resultado.setText("ERRO: Preencha o CPF.");
                return;
            }
            if (nome.getText().isBlank()) {
                resultado.setText("ERRO: Preencha o nome.");
                return;
            }
            String erroData = validarDatas(
                inicioDia.getText(), inicioMes.getText(), inicioAno.getText(),
                fimDia.getText(),    fimMes.getText(),    fimAno.getText()
            );
            if (erroData != null) {
                resultado.setText(erroData);
                return;
            }
            try {
                String nomeSelecionado = (String) espacoCombo.getSelectedItem();
                int espacoId = mapaEspacos.getOrDefault(nomeSelecionado, -1);
                if (espacoId == -1) {
                    resultado.setText("ERRO: Selecione um espaço válido.");
                    return;
                }
                String resposta = chamarServico(
                    BASE + "reserva?wsdl",
                    "RealizarReservaService",
                    "realizarReserva",
                    new Object[]{
                        cpf.getText(),
                        nome.getText(),
                        espacoId,
                        Integer.parseInt(inicioDia.getText()),
                        Integer.parseInt(inicioMes.getText()),
                        Integer.parseInt(inicioAno.getText()),
                        Integer.parseInt(fimDia.getText()),
                        Integer.parseInt(fimMes.getText()),
                        Integer.parseInt(fimAno.getText())
                    }
                );
                resultado.setText(resposta);

                // Limpa os campos após reserva bem-sucedida
                if (resposta.startsWith("SUCESSO")) {
                    cpf.setText("");
                    nome.setText("");
                    espacoCombo.setSelectedIndex(0);
                    preencherDataAtual(inicioDia, inicioMes, inicioAno);
                    preencherDataAtual(fimDia, fimMes, fimAno);
                }
            } catch (Exception ex) {
                resultado.setText("ERRO: " + ex.getMessage());
            }
        });

        painel.add(campos, BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);
        return painel;
    }

    // -------------------------------------------------------
    // PAINEL: MINHAS RESERVAS
    // -------------------------------------------------------
    private JPanel criarPainelMinhasReservas() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel campos = new JPanel(new GridLayout(4, 4, 5, 5));

        campos.add(new JLabel("CPF:"));
        JTextField cpf = new JTextField();
        campos.add(cpf);
        campos.add(new JLabel());
        campos.add(new JLabel());

        campos.add(new JLabel("Início (dia/mês/ano):"));
        JTextField inicioDia = new JTextField();
        JTextField inicioMes = new JTextField();
        JTextField inicioAno = new JTextField();
        campos.add(inicioDia);
        campos.add(inicioMes);
        campos.add(inicioAno);

        campos.add(new JLabel("Fim (dia/mês/ano):"));
        JTextField fimDia = new JTextField();
        JTextField fimMes = new JTextField();
        JTextField fimAno = new JTextField();
        campos.add(fimDia);
        campos.add(fimMes);
        campos.add(fimAno);

        JButton btnConsultar = new JButton("Consultar Minhas Reservas");
        campos.add(btnConsultar);
        campos.add(new JLabel());
        campos.add(new JLabel());
        campos.add(new JLabel());

        // Preenche com a data atual
        preencherDataAtual(inicioDia, inicioMes, inicioAno);
        preencherDataAtual(fimDia, fimMes, fimAno);

        JTextArea resultado = new JTextArea(10, 50);
        resultado.setEditable(false);
        resultado.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(resultado);

        btnConsultar.addActionListener(e -> {
            if (cpf.getText().isBlank()) {
                resultado.setText("ERRO: Preencha o CPF.");
                return;
            }
            String erroData = validarDatas(
                inicioDia.getText(), inicioMes.getText(), inicioAno.getText(),
                fimDia.getText(),    fimMes.getText(),    fimAno.getText()
            );
            if (erroData != null) {
                resultado.setText(erroData);
                return;
            }
            try {
                String resposta = chamarServico(
                    BASE + "minhasReservas?wsdl",
                    "ConsultarMinhasReservasService",
                    "consultarMinhasReservas",
                    new Object[]{
                        cpf.getText(),
                        Integer.parseInt(inicioDia.getText()),
                        Integer.parseInt(inicioMes.getText()),
                        Integer.parseInt(inicioAno.getText()),
                        Integer.parseInt(fimDia.getText()),
                        Integer.parseInt(fimMes.getText()),
                        Integer.parseInt(fimAno.getText())
                    }
                );
                resultado.setText(resposta);
            } catch (Exception ex) {
                resultado.setText("ERRO: " + ex.getMessage());
            }
        });

        painel.add(campos, BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);
        return painel;
    }

    // -------------------------------------------------------
    // PAINEL: RELATÓRIO ADMIN
    // -------------------------------------------------------
    private JPanel criarPainelRelatorio() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel campos = new JPanel(new GridLayout(3, 4, 5, 5));

        campos.add(new JLabel("Início (dia/mês/ano):"));
        JTextField inicioDia = new JTextField();
        JTextField inicioMes = new JTextField();
        JTextField inicioAno = new JTextField();
        campos.add(inicioDia);
        campos.add(inicioMes);
        campos.add(inicioAno);

        campos.add(new JLabel("Fim (dia/mês/ano):"));
        JTextField fimDia = new JTextField();
        JTextField fimMes = new JTextField();
        JTextField fimAno = new JTextField();
        campos.add(fimDia);
        campos.add(fimMes);
        campos.add(fimAno);

        JButton btnGerar = new JButton("Gerar Relatório");
        campos.add(btnGerar);
        campos.add(new JLabel());
        campos.add(new JLabel());
        campos.add(new JLabel());

        // Preenche com a data atual
        preencherDataAtual(inicioDia, inicioMes, inicioAno);
        preencherDataAtual(fimDia, fimMes, fimAno);

        JTextArea resultado = new JTextArea(10, 50);
        resultado.setEditable(false);
        resultado.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(resultado);

        btnGerar.addActionListener(e -> {
            String erroData = validarDatas(
                inicioDia.getText(), inicioMes.getText(), inicioAno.getText(),
                fimDia.getText(),    fimMes.getText(),    fimAno.getText()
            );
            if (erroData != null) {
                resultado.setText(erroData);
                return;
            }
            try {
                String resposta = chamarServico(
                    BASE + "relatorio?wsdl",
                    "RelatorioAdminService",
                    "gerarRelatorio",
                    new Object[]{
                        Integer.parseInt(inicioDia.getText()),
                        Integer.parseInt(inicioMes.getText()),
                        Integer.parseInt(inicioAno.getText()),
                        Integer.parseInt(fimDia.getText()),
                        Integer.parseInt(fimMes.getText()),
                        Integer.parseInt(fimAno.getText())
                    }
                );
                resultado.setText(resposta);
            } catch (Exception ex) {
                resultado.setText("ERRO: " + ex.getMessage());
            }
        });

        painel.add(campos, BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);
        return painel;
    }

    // -------------------------------------------------------
    // PAINEL: CANCELAR RESERVA
    // -------------------------------------------------------
    private JPanel criarPainelCancelarReserva() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel campos = new JPanel(new GridLayout(3, 4, 5, 5));

        campos.add(new JLabel("CPF:"));
        JTextField cpf = new JTextField();
        campos.add(cpf);
        campos.add(new JLabel());
        campos.add(new JLabel());

        campos.add(new JLabel("ID da Reserva:"));
        JTextField idReserva = new JTextField();
        campos.add(idReserva);
        campos.add(new JLabel());
        campos.add(new JLabel());

        JButton btnCancelar = new JButton("Cancelar Reserva");
        campos.add(btnCancelar);
        campos.add(new JLabel());
        campos.add(new JLabel());
        campos.add(new JLabel());

        JTextArea resultado = new JTextArea(10, 50);
        resultado.setEditable(false);
        resultado.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(resultado);

        btnCancelar.addActionListener(e -> {
            if (cpf.getText().isBlank()) {
                resultado.setText("ERRO: Preencha o CPF.");
                return;
            }
            if (idReserva.getText().isBlank()) {
                resultado.setText("ERRO: Preencha o ID da reserva.");
                return;
            }
            try {
                long id = Long.parseLong(idReserva.getText());
                String resposta = chamarServico(
                    BASE + "cancelar?wsdl",
                    "CancelarReservaService",
                    "cancelarReserva",
                    new Object[]{ cpf.getText(), id }
                );
                resultado.setText(resposta);

                // Limpa os campos após cancelamento bem-sucedido
                if (resposta.startsWith("SUCESSO")) {
                    cpf.setText("");
                    idReserva.setText("");
                }
            } catch (NumberFormatException ex) {
                resultado.setText("ERRO: O ID da reserva deve ser um número.");
            } catch (Exception ex) {
                resultado.setText("ERRO: " + ex.getMessage());
            }
        });

        painel.add(campos, BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);
        return painel;
    }

    // -------------------------------------------------------
    // MÉTODO DE COMUNICAÇÃO SOAP
    // -------------------------------------------------------
    private String chamarServico(String wsdlUrl, String serviceName,
                                 String operationName, Object[] parametros) {
        try {
            URL url     = new URL(wsdlUrl);
            QName qname = new QName("http://endpoint.condominio.com/", serviceName);

            Service service = Service.create(url, qname);
            javax.xml.ws.Dispatch<Source> dispatch = service.createDispatch(
                new QName("http://endpoint.condominio.com/",
                          serviceName.replace("Service", "Endpoint") + "Port"),
                Source.class,
                Service.Mode.PAYLOAD
            );

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

            Source source   = new StreamSource(new StringReader(xml.toString()));
            Source resposta = dispatch.invoke(source);

            TransformerFactory tf     = TransformerFactory.newInstance();
            Transformer transformer   = tf.newTransformer();
            StringWriter writer       = new StringWriter();
            transformer.transform(resposta, new StreamResult(writer));

            return writer.toString().replaceAll("<[^>]+>", "").trim();

        } catch (Exception e) {
            return "ERRO: " + e.getMessage();
        }
    }

    private String[] obterNomesParametros(String operationName) {
        switch (operationName) {
            case "realizarReserva":
                return new String[]{ "cpf", "nome", "espacoId", "inicioDia", "inicioMes", "inicioAno",
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
            case "cancelarReserva":
                return new String[]{ "cpf", "idReserva" };
            default:
                return new String[]{};
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Interface::new);
    }
}