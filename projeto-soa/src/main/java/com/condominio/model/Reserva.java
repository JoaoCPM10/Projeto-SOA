package com.condominio.model;

import com.condominio.enums.StatusReserva;

public class Reserva {

    private long id;
    private String cpfMorador;
    private String nomeMorador;
    private Espaco espaco;
    private IntervaloDatas periodo;
    private StatusReserva status;

    public Reserva(long id, String cpfMorador, String nomeMorador, Espaco espaco, IntervaloDatas periodo, StatusReserva status) {
        this.id          = id;
        this.cpfMorador  = cpfMorador;
        this.nomeMorador = nomeMorador;
        this.espaco      = espaco;
        this.periodo     = periodo;
        this.status      = status;
    }

    // Retorna true se a reserva está ativa (não foi cancelada nem concluída)
    public boolean isAtiva() {
        return this.status == StatusReserva.CONFIRMADA;
    }

    // Verifica se essa reserva conflita com outra reserva do mesmo espaço
    public boolean conflitaCom(Reserva outra) {
        if (this.espaco.getId() != outra.espaco.getId()) return false;
        if (!outra.isAtiva()) return false;
        return this.periodo.sobrepoeCom(outra.periodo);
    }

    // Muda o status para CONFIRMADA
    public void confirmar() {
        this.status = StatusReserva.CONFIRMADA;
    }

    // Muda o status para CANCELADA
    public void cancelar() {
        this.status = StatusReserva.CANCELADA;
    }

    // Muda o status para CONCLUIDA
    public void concluir() {
        this.status = StatusReserva.CONCLUIDA;
    }

    @Override
    public String toString() {
        return String.format("Reserva{id=%d, cpf='%s', nome='%s', espaco='%s', periodo=%s, status=%s}",
                id, cpfMorador, nomeMorador, espaco.getNome(), periodo.toString(), status);
    }

    public long getId()                  { return id;         }
    public String getCpfMorador()        { return cpfMorador; }
    public String getNomeMorador()       { return nomeMorador; }
    public Espaco getEspaco()            { return espaco;     }
    public IntervaloDatas getPeriodo()   { return periodo;    }
    public StatusReserva getStatus()     { return status;     }

    public void setId(long id)           { this.id = id;      }
    public void setStatus(StatusReserva status) { this.status = status; }
}
