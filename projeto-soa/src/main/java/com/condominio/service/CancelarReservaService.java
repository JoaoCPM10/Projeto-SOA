package com.condominio.service;

import com.condominio.model.IntervaloDatas;
import com.condominio.model.Reserva;
import com.condominio.repository.ReservaRepository;
import com.condominio.repository.ReservaRepositoryImpl;

import java.util.List;

public class CancelarReservaService {

    private ReservaRepository reservaRepo;

    public CancelarReservaService() {
        this.reservaRepo = new ReservaRepositoryImpl();
    }

    public ResultadoCancelamento cancelar(String cpf, long idReserva) {

        // Valida o CPF
        if (cpf == null || cpf.isBlank()) {
            return ResultadoCancelamento.erro("CPF inválido.");
        }

        // Busca as reservas do morador em um intervalo amplo para localizar a reserva
        IntervaloDatas intervaloAmplo = new IntervaloDatas(
            new com.condominio.model.DataReserva(1, 1, 2000),
            new com.condominio.model.DataReserva(31, 12, 2100)
        );
        List<Reserva> reservas = reservaRepo.findByCpf(cpf, intervaloAmplo);

        // Verifica se a reserva pertence ao CPF informado
        Reserva reservaEncontrada = null;
        for (Reserva r : reservas) {
            if (r.getId() == idReserva) {
                reservaEncontrada = r;
                break;
            }
        }

        if (reservaEncontrada == null) {
            return ResultadoCancelamento.erro("Reserva de id " + idReserva + " não encontrada para o CPF informado.");
        }

        if (!reservaEncontrada.isAtiva()) {
            return ResultadoCancelamento.erro("Reserva de id " + idReserva + " já está " + reservaEncontrada.getStatus() + ".");
        }

        // Cancela a reserva
        reservaRepo.cancelar(idReserva);
        return ResultadoCancelamento.sucesso(idReserva);
    }

    public static class ResultadoCancelamento {

        private boolean sucesso;
        private String  mensagem;

        private ResultadoCancelamento(boolean sucesso, String mensagem) {
            this.sucesso  = sucesso;
            this.mensagem = mensagem;
        }

        public static ResultadoCancelamento sucesso(long id) {
            return new ResultadoCancelamento(true, "Reserva " + id + " cancelada com sucesso!");
        }

        public static ResultadoCancelamento erro(String mensagem) {
            return new ResultadoCancelamento(false, mensagem);
        }

        public boolean isSucesso()  { return sucesso;  }
        public String getMensagem() { return mensagem; }

        @Override
        public String toString() {
            return (sucesso ? "SUCESSO: " : "ERRO: ") + mensagem;
        }
    }
}
