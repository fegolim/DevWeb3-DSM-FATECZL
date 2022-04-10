package com.fatec.sig1.ports;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.sig1.model.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
	Optional<Produto> findByCpf(String cpf);

	List<Produto> findAllByNomeIgnoreCaseContaining(String nome);
}