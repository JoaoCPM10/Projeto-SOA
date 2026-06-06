package com.condominio.model;

public class IntervaloDatas {

    private DataReserva inicio;
    private DataReserva fim;

    public IntervaloDatas(DataReserva inicio, DataReserva fim) {
        this.inicio = inicio;
        this.fim = fim;
    }

    // Retorna true se o intervalo é válido (início anterior ou igual ao fim)
    public boolean isValido() {
        return inicio.isAnteriorA(fim) || inicio.equals(fim);
    }

    // Retorna true se uma data específica está dentro desse intervalo
    public boolean contemData(DataReserva data) {
        boolean depoisOuIgualInicio = data.equals(inicio) || data.isPosteriorA(inicio);
        boolean antesOuIgualFim    = data.equals(fim)    || data.isAnteriorA(fim);
        return depoisOuIgualInicio && antesOuIgualFim;
    }

    // Retorna true se esse intervalo se sobrepõe com outro intervalo
    // Dois intervalos se sobrepõem quando um começa antes do outro terminar
    public boolean sobrepoeCom(IntervaloDatas outro) {
        // Não se sobrepõem somente se um termina antes do outro começar
        boolean esteTerninaAntesDoOutroComecar  = this.fim.isAnteriorA(outro.inicio);
        boolean outroTerminaAntesDeEsteComecar  = outro.fim.isAnteriorA(this.inicio);
        return !esteTerninaAntesDoOutroComecar && !outroTerminaAntesDeEsteComecar;
    }

    // Calcula quantos dias há no intervalo (inclusivo)
    public int quantidadeDias() {
        // Conversão simples para dias totais usando fórmula aproximada
        int diasInicio = inicio.getAno() * 365 + inicio.getMes() * 30 + inicio.getDia();
        int diasFim    = fim.getAno()    * 365 + fim.getMes()    * 30 + fim.getDia();
        return diasFim - diasInicio + 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof IntervaloDatas)) return false;
        IntervaloDatas outro = (IntervaloDatas) obj;
        return this.inicio.equals(outro.inicio) && this.fim.equals(outro.fim);
    }

    @Override
    public String toString() {
        return "de " + inicio.toString() + " até " + fim.toString();
    }

    public DataReserva getInicio() { return inicio; }
    public DataReserva getFim()    { return fim;    }
}
