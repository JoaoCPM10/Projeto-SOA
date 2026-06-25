package com.condominio.service;

import com.condominio.enums.StatusReserva;
import com.condominio.model.IntervaloDatas;
import com.condominio.model.Reserva;
import com.condominio.repository.ReservaRepository;
import com.condominio.repository.ReservaRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

public class RelatorioAdminService {

    private ReservaRepository reservaRepo;

    public RelatorioAdminService() {
        this.reservaRepo = new ReservaRepositoryImpl();
    }

    public ResultadoRelatorio gerar(IntervaloDatas intervalo, String status) {
        if (!intervalo.isValido()) {
            return ResultadoRelatorio.erro("Intervalo inválido: a data de início deve ser anterior à data de fim.");
        }

        List<Reserva> reservas = reservaRepo.findAll(intervalo);
        List<Reserva> filtradas = filtrarPorStatus(reservas, status);
        return ResultadoRelatorio.sucesso(filtradas, status);
    }

    public ResultadoRelatorio gerarCompleto(String status) {
        List<Reserva> reservas = reservaRepo.findTodas();
        List<Reserva> filtradas = filtrarPorStatus(reservas, status);
        return ResultadoRelatorio.sucesso(filtradas, status);
    }

    // Filtra a lista por status. Se status for "TODOS" ou nulo, retorna tudo.
    private List<Reserva> filtrarPorStatus(List<Reserva> reservas, String status) {
        if (status == null || status.isBlank() || status.equals("TODOS")) {
            return reservas;
        }
        List<Reserva> filtradas = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.getStatus().name().equals(status)) {
                filtradas.add(r);
            }
        }
        return filtradas;
    }

    public static class ResultadoRelatorio {

        private boolean       sucesso;
        private String        mensagem;
        private List<Reserva> reservas;

        private ResultadoRelatorio(boolean sucesso, String mensagem, List<Reserva> reservas) {
            this.sucesso  = sucesso;
            this.mensagem = mensagem;
            this.reservas = reservas;
        }

        public static ResultadoRelatorio sucesso(List<Reserva> reservas, String status) {
            String filtroMsg = (status == null || status.equals("TODOS")) ? "" : " com status " + status;
            String msg = reservas.isEmpty()
                ? "Nenhuma reserva encontrada" + filtroMsg + "."
                : "Relatório gerado com sucesso! " + reservas.size() + " reserva(s) encontrada(s)" + filtroMsg + ".";
            return new ResultadoRelatorio(true, msg, reservas);
        }

        public static ResultadoRelatorio erro(String mensagem) {
            return new ResultadoRelatorio(false, mensagem, null);
        }

        public boolean isSucesso()         { return sucesso;  }
        public String getMensagem()        { return mensagem; }
        public List<Reserva> getReservas() { return reservas; }

        @Override
        public String toString() {
            if (!sucesso) return "ERRO: " + mensagem;

            if (reservas == null || reservas.isEmpty()) {
                return "SUCESSO: " + mensagem;
            }

            // Agrupa por espaço
            java.util.Map<String, java.util.List<Reserva>> porEspaco = new java.util.LinkedHashMap<>();
            for (Reserva r : reservas) {
                String nomeEspaco = r.getEspaco().getNome();
                porEspaco.computeIfAbsent(nomeEspaco, k -> new java.util.ArrayList<>()).add(r);
            }

            StringBuilder sb = new StringBuilder();
            sb.append("SUCESSO: ").append(mensagem).append("\n");

            for (java.util.Map.Entry<String, java.util.List<Reserva>> entry : porEspaco.entrySet()) {
                sb.append("\n[ ").append(entry.getKey()).append(" ]\n");
                for (Reserva r : entry.getValue()) {
                    sb.append("  ").append(r).append("\n");
                }
            }

            return sb.toString();
        }
    }
}