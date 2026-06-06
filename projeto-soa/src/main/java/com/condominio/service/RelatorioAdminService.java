package com.condominio.service;

import com.condominio.model.IntervaloDatas;
import com.condominio.model.Reserva;
import com.condominio.repository.ReservaRepository;
import com.condominio.repository.ReservaRepositoryImpl;

import java.util.List;

public class RelatorioAdminService {

    private ReservaRepository reservaRepo;

    public RelatorioAdminService() {
        this.reservaRepo = new ReservaRepositoryImpl();
    }

    // Método principal: retorna todas as reservas de todos os moradores no intervalo
    public ResultadoRelatorio gerar(IntervaloDatas intervalo) {

        // Passo 1: valida o intervalo
        if (!intervalo.isValido()) {
            return ResultadoRelatorio.erro("Intervalo inválido: a data de início deve ser anterior à data de fim.");
        }

        // Passo 2: busca todas as reservas no intervalo
        // A ordenação por data já é feita pelo próprio banco no findAll
        List<Reserva> reservas = reservaRepo.findAll(intervalo);

        // Retorna a lista (pode ser vazia se não houver reservas no período)
        return ResultadoRelatorio.sucesso(reservas);
    }

    // Classe interna que representa o resultado do relatório
    public static class ResultadoRelatorio {

        private boolean       sucesso;
        private String        mensagem;
        private List<Reserva> reservas;

        private ResultadoRelatorio(boolean sucesso, String mensagem, List<Reserva> reservas) {
            this.sucesso  = sucesso;
            this.mensagem = mensagem;
            this.reservas = reservas;
        }

        public static ResultadoRelatorio sucesso(List<Reserva> reservas) {
            String msg = reservas.isEmpty()
                ? "Nenhuma reserva encontrada para o período informado."
                : "Relatório gerado com sucesso! " + reservas.size() + " reserva(s) encontrada(s).";
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

            StringBuilder sb = new StringBuilder();
            sb.append("SUCESSO: ").append(mensagem).append("\n");
            if (reservas != null) {
                for (Reserva r : reservas) {
                    sb.append("  ").append(r).append("\n");
                }
            }
            return sb.toString();
        }
    }
}
