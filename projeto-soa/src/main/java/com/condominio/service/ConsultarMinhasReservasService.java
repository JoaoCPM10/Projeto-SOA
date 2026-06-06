package com.condominio.service;

import com.condominio.model.IntervaloDatas;
import com.condominio.model.Reserva;
import com.condominio.repository.ReservaRepository;
import com.condominio.repository.ReservaRepositoryImpl;

import java.util.List;

public class ConsultarMinhasReservasService {

    private ReservaRepository reservaRepo;

    public ConsultarMinhasReservasService() {
        this.reservaRepo = new ReservaRepositoryImpl();
    }

    // Método principal: retorna todas as reservas de um morador no intervalo
    public ResultadoMinhasReservas consultar(String cpf, IntervaloDatas intervalo) {

        // Passo 1: valida o CPF
        if (!cpfValido(cpf)) {
            return ResultadoMinhasReservas.erro("CPF inválido: " + cpf);
        }

        // Passo 2: valida o intervalo
        if (!intervalo.isValido()) {
            return ResultadoMinhasReservas.erro("Intervalo inválido: a data de início deve ser anterior à data de fim.");
        }

        // Passo 3: busca as reservas do morador no intervalo
        List<Reserva> reservas = reservaRepo.findByCpf(cpf, intervalo);

        // Retorna a lista (pode ser vazia se não houver reservas no período)
        return ResultadoMinhasReservas.sucesso(reservas);
    }

    // Valida se o CPF tem formato correto (11 dígitos, com ou sem pontuação)
    private boolean cpfValido(String cpf) {
        if (cpf == null || cpf.isBlank()) return false;
        String soDigitos = cpf.replaceAll("[^0-9]", "");
        return soDigitos.length() == 11;
    }

    // Classe interna que representa o resultado da consulta
    public static class ResultadoMinhasReservas {

        private boolean       sucesso;
        private String        mensagem;
        private List<Reserva> reservas;

        private ResultadoMinhasReservas(boolean sucesso, String mensagem, List<Reserva> reservas) {
            this.sucesso  = sucesso;
            this.mensagem = mensagem;
            this.reservas = reservas;
        }

        public static ResultadoMinhasReservas sucesso(List<Reserva> reservas) {
            String msg = reservas.isEmpty()
                ? "Nenhuma reserva encontrada para o período informado."
                : "Consulta realizada com sucesso! " + reservas.size() + " reserva(s) encontrada(s).";
            return new ResultadoMinhasReservas(true, msg, reservas);
        }

        public static ResultadoMinhasReservas erro(String mensagem) {
            return new ResultadoMinhasReservas(false, mensagem, null);
        }

        public boolean isSucesso()        { return sucesso;  }
        public String getMensagem()       { return mensagem; }
        public List<Reserva> getReservas(){ return reservas; }

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
