package com.condominio.command;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ReservaScheduler {

    private Command command;
    private ScheduledExecutorService scheduler;

    public ReservaScheduler(Command command) {
        this.command   = command;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    // Agenda o comando para rodar todo dia à meia-noite
    public void agendarComando() {

        // Calcula quantos segundos faltam para meia-noite
        LocalDateTime agora       = LocalDateTime.now();
        LocalDateTime meianoite   = agora.toLocalDate().plusDays(1).atStartOfDay();
        long segundosAteEMeiaNoite = java.time.Duration.between(agora, meianoite).getSeconds();

        System.out.println("[ReservaScheduler] Comando agendado. " +
                           "Primeira execução em " + segundosAteEMeiaNoite + " segundos.");

        // Primeira execução no próximo dia, depois repete a cada 24 horas
        scheduler.scheduleAtFixedRate(
            () -> command.execute(),
            segundosAteEMeiaNoite,
            TimeUnit.DAYS.toSeconds(1),
            TimeUnit.SECONDS
        );
    }

    // Para o scheduler quando o servidor encerrar
    public void parar() {
        scheduler.shutdown();
        System.out.println("[ReservaScheduler] Scheduler encerrado.");
    }
}