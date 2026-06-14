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
        return String.format("Espaco: %d - %s | Status: %s",
           espaco.getId(), espaco.getNome(), status);
}
}
