package com.condominio.model;

import com.condominio.enums.StatusEspaco;

public class ResultadoDisponibilidade {

    private Espaco espaco;
    private StatusEspaco status;

    public ResultadoDisponibilidade(Espaco espaco, StatusEspaco status) {
        this.espaco = espaco;
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("Espaco: %s | Status: %s", espaco.getNome(), status);
    }

    public Espaco getEspaco()        { return espaco; }
    public StatusEspaco getStatus()  { return status; }
}
