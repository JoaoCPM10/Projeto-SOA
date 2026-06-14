package com.condominio;

import com.condominio.enums.StatusEspaco;
import com.condominio.enums.TipoEspaco;
import com.condominio.model.*;
import com.condominio.repository.EspacoRepositoryImpl;
import com.condominio.repository.ReservaRepositoryImpl;
import com.condominio.service.ConsultaDisponibilidadeService;
import com.condominio.service.ConsultarMinhasReservasService;
import com.condominio.service.RealizarReservaService;
import com.condominio.service.RelatorioAdminService;
import com.condominio.enums.StatusReserva;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("  TESTE 1: Models e Enums");
        System.out.println("========================================");

        // Testando DataReserva
        DataReserva data1 = new DataReserva(1, 6, 2026);
        DataReserva data2 = new DataReserva(10, 6, 2026);
        DataReserva data3 = new DataReserva(1, 6, 2026);

        System.out.println("Data 1: " + data1);
        System.out.println("Data 2: " + data2);
        System.out.println("Data 1 é anterior à Data 2? " + data1.isAnteriorA(data2));   // true
        System.out.println("Data 2 é anterior à Data 1? " + data2.isAnteriorA(data1));   // false
        System.out.println("Data 1 é igual à Data 3?    " + data1.equals(data3));         // true

        System.out.println();

        // Testando IntervaloDatas
        IntervaloDatas intervalo1 = new IntervaloDatas(data1, data2);
        IntervaloDatas intervalo2 = new IntervaloDatas(
            new DataReserva(5, 6, 2026),
            new DataReserva(15, 6, 2026)
        );
        IntervaloDatas intervalo3 = new IntervaloDatas(
            new DataReserva(11, 6, 2026),
            new DataReserva(20, 6, 2026)
        );

        System.out.println("Intervalo 1: " + intervalo1);
        System.out.println("Intervalo 2: " + intervalo2);
        System.out.println("Intervalo 3: " + intervalo3);
        System.out.println("Intervalo 1 é válido?              " + intervalo1.isValido());                  // true
        System.out.println("Intervalo 1 contém 05/06/2026?     " + intervalo1.contemData(new DataReserva(5, 6, 2026)));  // true
        System.out.println("Intervalo 1 contém 20/06/2026?     " + intervalo1.contemData(new DataReserva(20, 6, 2026))); // false
        System.out.println("Intervalo 1 se sobrepõe com 2?     " + intervalo1.sobrepoeCom(intervalo2));    // true
        System.out.println("Intervalo 1 se sobrepõe com 3?     " + intervalo1.sobrepoeCom(intervalo3));    // false

        System.out.println();

        // Testando Espaco e EspacoFactory
        Espaco churrasqueira = EspacoFactory.criarChurrasqueira("Churrasqueira 1");
        Espaco salao         = EspacoFactory.criarSalaoDeFesta("Salão de Festas");
        System.out.println("Espaço criado: " + churrasqueira);
        System.out.println("Espaço criado: " + salao);

        System.out.println();

        // Testando Reserva
        Espaco espacoTeste = new Espaco(1, "Churrasqueira 1", TipoEspaco.CHURRASQUEIRA, 30);
        Reserva reserva = new Reserva(0, "123.456.789-00", "Jhon Doe", espacoTeste, intervalo1, StatusReserva.CONFIRMADA);
        System.out.println("Reserva criada: " + reserva);
        System.out.println("Reserva está ativa? " + reserva.isAtiva()); // true
        reserva.cancelar();
        System.out.println("Após cancelar, está ativa? " + reserva.isAtiva()); // false

        System.out.println();

        // Testando ResultadoDisponibilidade
        ResultadoDisponibilidade resultado = new ResultadoDisponibilidade(espacoTeste, StatusEspaco.DISPONIVEL);
        System.out.println("Resultado: " + resultado);

        System.out.println();
        System.out.println("========================================");
        System.out.println("  TESTE 2: Banco de Dados");
        System.out.println("========================================");

        // Testando EspacoRepository (lê os espaços que o data.sql inseriu)
        EspacoRepositoryImpl espacoRepo = new EspacoRepositoryImpl();
        List<Espaco> espacos = espacoRepo.findAll();

        System.out.println("Espaços encontrados no banco: " + espacos.size());
        for (Espaco e : espacos) {
            System.out.println("  " + e);
        }

        System.out.println();

        // Testando busca por tipo
        List<Espaco> churrasqueiras = espacoRepo.findByTipo(TipoEspaco.CHURRASQUEIRA);
        System.out.println("Churrasqueiras encontradas: " + churrasqueiras.size());

        System.out.println();

        // Testando ReservaRepository (salva e busca uma reserva)
        ReservaRepositoryImpl reservaRepo = new ReservaRepositoryImpl();

        Espaco espacoBanco = espacoRepo.findById(1);
        IntervaloDatas periodo = new IntervaloDatas(
            new DataReserva(20, 6, 2026),
            new DataReserva(20, 6, 2026)
        );
        Reserva novaReserva = new Reserva(0, "111.222.333-44", "John Doe", espacoBanco, periodo, StatusReserva.CONFIRMADA);
        reservaRepo.save(novaReserva);
        System.out.println("Reserva salva com id gerado pelo banco: " + novaReserva.getId());

        // Busca a reserva que acabou de salvar
        List<Reserva> reservasCpf = reservaRepo.findByCpf("111.222.333-44", new IntervaloDatas(
            new DataReserva(1, 6, 2026),
            new DataReserva(30, 6, 2026)
        ));
        System.out.println("Reservas encontradas para o CPF 111.222.333-44: " + reservasCpf.size());
        for (Reserva r : reservasCpf) {
            System.out.println("  " + r);
        }

        System.out.println("\n========================================");
        System.out.println("  TESTE 3: RealizarReservaService");
        System.out.println("========================================");

        RealizarReservaService reservaService = new RealizarReservaService();

        // Deve funcionar
        IntervaloDatas periodo1 = new IntervaloDatas(new DataReserva(1, 7, 2026), new DataReserva(1, 7, 2026));
        System.out.println(reservaService.reservar("111.222.333-44", "John Doe", 1, periodo1));

        // Deve dar conflito (mesmo espaço e período)
        System.out.println(reservaService.reservar("999.888.777-66", "Jane Smith", 1, periodo1));

        // Deve dar erro de CPF inválido
        System.out.println(reservaService.reservar("cpf-errado", "Invalid User", 1, periodo1));

        System.out.println("\n========================================");
        System.out.println("  TESTE 4: ConsultaDisponibilidadeService");
        System.out.println("========================================");

        ConsultaDisponibilidadeService disponibilidadeService = new ConsultaDisponibilidadeService();

        // Consulta no período em que já existe uma reserva (espaço 1 no dia 01/07)
        IntervaloDatas periodoConsulta = new IntervaloDatas(
            new DataReserva(1, 7, 2026),
            new DataReserva(1, 7, 2026)
        );
        System.out.println(disponibilidadeService.consultar(periodoConsulta));

        // Consulta em período sem nenhuma reserva
        IntervaloDatas periodoLivre = new IntervaloDatas(
            new DataReserva(1, 8, 2026),
            new DataReserva(1, 8, 2026)
        );
        System.out.println(disponibilidadeService.consultar(periodoLivre));

        System.out.println("\n========================================");
        System.out.println("  TESTE 5: ConsultarMinhasReservasService");
        System.out.println("========================================");

        ConsultarMinhasReservasService minhasReservasService = new ConsultarMinhasReservasService();

        // CPF que tem reserva no sistema (criada no Teste 3)
        IntervaloDatas periodoJunho = new IntervaloDatas(
            new DataReserva(1, 7, 2026),
            new DataReserva(31, 7, 2026)
        );
        System.out.println(minhasReservasService.consultar("111.222.333-44", periodoJunho));

        // CPF que não tem nenhuma reserva
        System.out.println(minhasReservasService.consultar("000.000.000-00", periodoJunho));

        // CPF inválido
        System.out.println(minhasReservasService.consultar("cpf-invalido", periodoJunho));

        System.out.println("\n========================================");
        System.out.println("  TESTE 6: RelatorioAdminService");
        System.out.println("========================================");

        RelatorioAdminService relatorioService = new RelatorioAdminService();

        // Deve retornar todas as reservas feitas nos testes anteriores
        IntervaloDatas periodoRelatorio = new IntervaloDatas(
            new DataReserva(1, 6, 2026),
            new DataReserva(31, 12, 2026)
        );
        System.out.println(relatorioService.gerar(periodoRelatorio));

        // Intervalo inválido
        IntervaloDatas periodoInvalido = new IntervaloDatas(
            new DataReserva(31, 12, 2026),
            new DataReserva(1, 1, 2026)
        );
        System.out.println(relatorioService.gerar(periodoInvalido));










        System.out.println();
        System.out.println("========================================");
        System.out.println("  Todos os testes concluídos!");
        System.out.println("========================================");
    }
}
