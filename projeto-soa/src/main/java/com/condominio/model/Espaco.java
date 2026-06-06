package com.condominio.model;

import com.condominio.enums.TipoEspaco;

public class Espaco {

    private int id;
    private String nome;
    private TipoEspaco tipo;
    private int capacidade;

    public Espaco(int id, String nome, TipoEspaco tipo, int capacidade) {
        this.id         = id;
        this.nome       = nome;
        this.tipo       = tipo;
        this.capacidade = capacidade;
    }

    @Override
    public String toString() {
        return String.format("Espaco{id=%d, nome='%s', tipo=%s, capacidade=%d}",
                id, nome, tipo, capacidade);
    }

    public int getId()            { return id;         }
    public String getNome()       { return nome;       }
    public TipoEspaco getTipo()   { return tipo;       }
    public int getCapacidade()    { return capacidade; }
}
