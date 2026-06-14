package com.condominio.service;

import com.condominio.enums.StatusReserva;
import com.condominio.model.Espaco;
import com.condominio.model.IntervaloDatas;
import com.condominio.model.Reserva;
import com.condominio.repository.EspacoRepository;
import com.condominio.repository.EspacoRepositoryImpl;
import com.condominio.repository.ReservaRepository;
import com.condominio.repository.ReservaRepositoryImpl;

import java.util.List;

public class RealizarReservaService {

    private ReservaRepository reservaRepo;
    private EspacoRepository  espacoRepo;

    public RealizarReservaService() {
        this.reservaRepo = new ReservaRepositoryImpl();
        this.espacoRepo  = new EspacoRepositoryImpl();
    }

    // Método principal: tenta realizar uma reserva e retorna o resultado
    public ResultadoReserva reservar(String cpf, String nome, int espacoId, IntervaloDatas intervalo) {

        // Passo 1: valida o CPF
        if (!cpfValido(cpf)) {
            return ResultadoReserva.erro("CPF inválido: " + cpf);
        }

        // Passo 2: valida o intervalo
        if (!intervalo.isValido()) {
            return ResultadoReserva.erro("Intervalo inválido: a data de início deve ser anterior à data de fim.");
        }

        // Passo 3: busca o espaço pelo id
        Espaco espaco = espacoRepo.findById(espacoId);
        if (espaco == null) {
            return ResultadoReserva.erro("Espaço não encontrado com id: " + espacoId);
        }

        // Passo 4: verifica se o espaço está disponível no período
        if (!estaDisponivel(espaco, intervalo)) {
            return ResultadoReserva.erro("Espaço '" + espaco.getNome() + "' não está disponível no período informado.");
        }

        // Passo 5: cria e salva a reserva
        Reserva novaReserva = new Reserva(0, cpf, nome, espaco, intervalo, StatusReserva.CONFIRMADA);
        reservaRepo.save(novaReserva);

        // Passo 6: retorna sucesso com a reserva criada
        return ResultadoReserva.sucesso(novaReserva);
    }

    // Verifica se existe alguma reserva ativa que conflita com o período pedido
    private boolean estaDisponivel(Espaco espaco, IntervaloDatas intervalo) {
        List<Reserva> reservasAtivas = reservaRepo.findByEspaco(espaco);

        for (Reserva reservaExistente : reservasAtivas) {
            if (reservaExistente.getPeriodo().sobrepoeCom(intervalo)) {
                return false; // Encontrou conflito
            }
        }

        return true; // Nenhum conflito encontrado
    }

    // Valida se o CPF tem formato correto (11 dígitos, com ou sem pontuação)
    private boolean cpfValido(String cpf) {
        if (cpf == null || cpf.isBlank()) return false;

        // Remove pontuação para contar só os dígitos
        String soDigitos = cpf.replaceAll("[^0-9]", "");
        return soDigitos.length() == 11;
    }

    // Classe interna que representa o resultado da operação (sucesso ou erro)
    // Assim o Service consegue retornar tanto a reserva quanto uma mensagem de erro
    public static class ResultadoReserva {

        private boolean sucesso;
        private String  mensagem;
        private Reserva reserva;

        private ResultadoReserva(boolean sucesso, String mensagem, Reserva reserva) {
            this.sucesso  = sucesso;
            this.mensagem = mensagem;
            this.reserva  = reserva;
        }

        public static ResultadoReserva sucesso(Reserva reserva) {
            return new ResultadoReserva(true, "Reserva realizada com sucesso!", reserva);
        }

        public static ResultadoReserva erro(String mensagem) {
            return new ResultadoReserva(false, mensagem, null);
        }

        public boolean isSucesso()   { return sucesso;  }
        public String getMensagem()  { return mensagem; }
        public Reserva getReserva()  { return reserva;  }

        @Override
        public String toString() {
            if (sucesso) {
                return "SUCESSO: " + mensagem + "\n  " + reserva;
            }
            return "ERRO: " + mensagem;
        }
    }
}
