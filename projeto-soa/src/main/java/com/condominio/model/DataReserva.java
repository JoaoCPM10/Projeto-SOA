package com.condominio.model;

public class DataReserva {

    private int dia;
    private int mes;
    private int ano;

    public DataReserva(int dia, int mes, int ano) {
        this.dia = dia;
        this.mes = mes;
        this.ano = ano;
    }

    // Retorna true se essa data vem antes da data passada como argumento
    public boolean isAnteriorA(DataReserva outra) {
        if (this.ano != outra.ano) return this.ano < outra.ano;
        if (this.mes != outra.mes) return this.mes < outra.mes;
        return this.dia < outra.dia;
    }

    // Retorna true se essa data vem depois da data passada como argumento
    public boolean isPosteriorA(DataReserva outra) {
        if (this.ano != outra.ano) return this.ano > outra.ano;
        if (this.mes != outra.mes) return this.mes > outra.mes;
        return this.dia > outra.dia;
    }

    // Retorna true se as duas datas representam o mesmo dia
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DataReserva)) return false;
        DataReserva outra = (DataReserva) obj;
        return this.dia == outra.dia && this.mes == outra.mes && this.ano == outra.ano;
    }

    @Override
    public String toString() {
        return String.format("%02d/%02d/%04d", dia, mes, ano);
    }

    public int getDia() { return dia; }
    public int getMes() { return mes; }
    public int getAno() { return ano; }
}
