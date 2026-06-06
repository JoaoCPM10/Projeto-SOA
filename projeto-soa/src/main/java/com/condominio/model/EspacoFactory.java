package com.condominio.model;

import com.condominio.enums.TipoEspaco;

public class EspacoFactory {

    // Capacidades padrão por tipo de espaço
    public static int capacidadeDefault(TipoEspaco tipo) {
        switch (tipo) {
            case CHURRASQUEIRA:   return 30;
            case SALAO_DE_FESTA:  return 100;
            case PISCINA:         return 50;
            case CAMPO_DE_FUTEBOL: return 22;
            default:              return 10;
        }
    }

    // Métodos de criação por tipo (id 0 pois será definido pelo banco ao salvar)
    public static Espaco criarChurrasqueira(String nome) {
        return new Espaco(0, nome, TipoEspaco.CHURRASQUEIRA, capacidadeDefault(TipoEspaco.CHURRASQUEIRA));
    }

    public static Espaco criarSalaoDeFesta(String nome) {
        return new Espaco(0, nome, TipoEspaco.SALAO_DE_FESTA, capacidadeDefault(TipoEspaco.SALAO_DE_FESTA));
    }

    public static Espaco criarPiscina(String nome) {
        return new Espaco(0, nome, TipoEspaco.PISCINA, capacidadeDefault(TipoEspaco.PISCINA));
    }

    public static Espaco criarCampoDeFutebol(String nome) {
        return new Espaco(0, nome, TipoEspaco.CAMPO_DE_FUTEBOL, capacidadeDefault(TipoEspaco.CAMPO_DE_FUTEBOL));
    }

    // Método genérico caso queiram criar com tipo dinâmico
    public static Espaco criar(String nome, TipoEspaco tipo) {
        return new Espaco(0, nome, tipo, capacidadeDefault(tipo));
    }
}
