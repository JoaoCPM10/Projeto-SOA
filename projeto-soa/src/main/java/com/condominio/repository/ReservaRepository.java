package com.condominio.repository;

import com.condominio.enums.StatusReserva;
import com.condominio.model.DataReserva;
import com.condominio.model.Espaco;
import com.condominio.model.IntervaloDatas;
import com.condominio.model.Reserva;

import java.util.List;

public interface ReservaRepository {

    // Salva uma nova reserva no banco e retorna ela com o id gerado
    Reserva save(Reserva reserva);

    // Retorna todas as reservas ativas de um espaço específico
    List<Reserva> findByEspaco(Espaco espaco);

    // Retorna todas as reservas de um morador em um intervalo de datas
    List<Reserva> findByCpf(String cpf, IntervaloDatas intervalo);

    // Retorna todas as reservas de todos os moradores em um intervalo de datas
    List<Reserva> findAll(IntervaloDatas intervalo);

    // Atualiza o status de uma lista de reservas (ex: concluir reservas passadas)
    void atualizarStatus(List<Long> ids, StatusReserva novoStatus);

    // Retorna reservas confirmadas cujo fim já passou da data informada
    List<Reserva> findConfirmadasComFimAntes(DataReserva data);

    // Cancela uma reserva pelo id
    void cancelar(long id);
}
