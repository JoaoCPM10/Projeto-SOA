package com.condominio.command;

import com.condominio.enums.StatusReserva;
import com.condominio.model.DataReserva;
import com.condominio.model.Reserva;
import com.condominio.repository.ReservaRepository;
import com.condominio.repository.ReservaRepositoryImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ConcluirReservasCommand implements Command {

    private ReservaRepository reservaRepo;

    public ConcluirReservasCommand() {
        this.reservaRepo = new ReservaRepositoryImpl();
    }

    @Override
    public void execute() {
        System.out.println("[ConcluirReservasCommand] Verificando reservas para concluir...");

        // Pega a data de hoje usando LocalDate do Java
        LocalDate hoje     = LocalDate.now();
        DataReserva ontem  = new DataReserva(
            hoje.minusDays(1).getDayOfMonth(),
            hoje.minusDays(1).getMonthValue(),
            hoje.minusDays(1).getYear()
        );

        // Busca reservas confirmadas com fim anterior a hoje
        List<Reserva> reservasParaConcluir = reservaRepo.findConfirmadasComFimAntes(ontem);

        if (reservasParaConcluir.isEmpty()) {
            System.out.println("[ConcluirReservasCommand] Nenhuma reserva para concluir.");
            return;
        }

        // Coleta os ids e atualiza o status para CONCLUIDA
        List<Long> ids = new ArrayList<>();
        for (Reserva r : reservasParaConcluir) {
            ids.add(r.getId());
        }

        reservaRepo.atualizarStatus(ids, StatusReserva.CONCLUIDA);
        System.out.println("[ConcluirReservasCommand] " + ids.size() + " reserva(s) concluída(s).");
    }
}