package com.condominio.service;

import com.condominio.enums.StatusEspaco;
import com.condominio.model.Espaco;
import com.condominio.model.IntervaloDatas;
import com.condominio.model.Reserva;
import com.condominio.model.ResultadoDisponibilidade;
import com.condominio.repository.EspacoRepository;
import com.condominio.repository.EspacoRepositoryImpl;
import com.condominio.repository.ReservaRepository;
import com.condominio.repository.ReservaRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

public class ConsultaDisponibilidadeService {

    private EspacoRepository  espacoRepo;
    private ReservaRepository reservaRepo;

    public ConsultaDisponibilidadeService() {
        this.espacoRepo  = new EspacoRepositoryImpl();
        this.reservaRepo = new ReservaRepositoryImpl();
    }

    // Método principal: retorna a disponibilidade de todos os espaços no intervalo
    public ResultadoConsulta consultar(IntervaloDatas intervalo) {

        // Passo 1: valida o intervalo
        if (!intervalo.isValido()) {
            return ResultadoConsulta.erro("Intervalo inválido: a data de início deve ser anterior à data de fim.");
        }

        // Passo 2: busca todos os espaços cadastrados
        List<Espaco> espacos = espacoRepo.findAll();

        // Passo 3: para cada espaço, verifica se há conflito no período
        List<ResultadoDisponibilidade> resultados = new ArrayList<>();

        for (Espaco espaco : espacos) {
            StatusEspaco status = verificarDisponibilidade(espaco, intervalo);
            resultados.add(new ResultadoDisponibilidade(espaco, status));
        }

        return ResultadoConsulta.sucesso(resultados);
    }

    // Verifica se um espaço específico está disponível no intervalo
    private StatusEspaco verificarDisponibilidade(Espaco espaco, IntervaloDatas intervalo) {
        List<Reserva> reservasAtivas = reservaRepo.findByEspaco(espaco);

        for (Reserva reserva : reservasAtivas) {
            if (reserva.getPeriodo().sobrepoeCom(intervalo)) {
                return StatusEspaco.INDISPONIVEL; // Encontrou conflito
            }
        }

        return StatusEspaco.DISPONIVEL; // Nenhum conflito encontrado
    }

    // Classe interna que representa o resultado da consulta
    public static class ResultadoConsulta {

        private boolean                      sucesso;
        private String                       mensagem;
        private List<ResultadoDisponibilidade> disponibilidades;

        private ResultadoConsulta(boolean sucesso, String mensagem, List<ResultadoDisponibilidade> disponibilidades) {
            this.sucesso          = sucesso;
            this.mensagem         = mensagem;
            this.disponibilidades = disponibilidades;
        }

        public static ResultadoConsulta sucesso(List<ResultadoDisponibilidade> disponibilidades) {
            return new ResultadoConsulta(true, "Consulta realizada com sucesso!", disponibilidades);
        }

        public static ResultadoConsulta erro(String mensagem) {
            return new ResultadoConsulta(false, mensagem, null);
        }

        public boolean isSucesso()                              { return sucesso;          }
        public String getMensagem()                             { return mensagem;         }
        public List<ResultadoDisponibilidade> getDisponibilidades() { return disponibilidades; }

        @Override
        public String toString() {
            if (!sucesso) return "ERRO: " + mensagem;

            StringBuilder sb = new StringBuilder();
            sb.append("SUCESSO: ").append(mensagem).append("\n");
            for (ResultadoDisponibilidade rd : disponibilidades) {
                sb.append("  ").append(rd).append("\n");
            }
            return sb.toString();
        }
    }
}
