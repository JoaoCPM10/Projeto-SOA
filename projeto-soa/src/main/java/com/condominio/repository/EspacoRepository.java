package com.condominio.repository;

import com.condominio.enums.TipoEspaco;
import com.condominio.model.Espaco;

import java.util.List;

public interface EspacoRepository {

    // Retorna todos os espaços cadastrados no condomínio
    List<Espaco> findAll();

    // Retorna um espaço pelo seu id, ou null se não existir
    Espaco findById(int id);

    // Retorna todos os espaços de um tipo específico (ex: todas as churrasqueiras)
    List<Espaco> findByTipo(TipoEspaco tipo);
}
