package com.fatec.sig1.services;

import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import com.fatec.sig1.model.Produto;
import com.fatec.sig1.model.Endereco;
import com.fatec.sig1.ports.ProdutoRepository;
import com.fatec.sig1.ports.MantemProduto;

@Service
public class MantemProdutoI implements MantemProduto {
	Logger logger = LogManager.getLogger(this.getClass());
	@Autowired
	ProdutoRepository repository;

	public List<Produto> consultaTodos() {
		logger.info(">>>>>> servico consultaTodos chamado");
		return repository.findAll();
	}

	@Override
	public Optional<Produto> consultaPorCpf(String cpf) {
		logger.info(">>>>>> servico consultaPorCpf chamado");
		return repository.findByCpf(cpf);
	}

	@Override
	public Optional<Produto> consultaPorId(Long id) {
		logger.info(">>>>>> servico consultaPorId chamado");
		return repository.findById(id);
	}

	@Override
	public Optional<Produto> save(Produto produto) {
		logger.info(">>>>>> servico save chamado ");
		Optional<Produto> umProduto = consultaPorCpf(produto.getCpf());
		Endereco endereco = obtemEndereco(produto.getCep());
		if (umProduto.isEmpty() & endereco != null) {
			logger.info(">>>>>> servico save - dados validos");
			produto.obtemDataAtual(new DateTime());
			produto.setEndereco(endereco.getLogradouro());
			return Optional.ofNullable(repository.save(produto));
		} else {
			return Optional.empty();
		}
	}

	@Override
	public void delete(Long id) {
		logger.info(">>>>>> servico delete por id chamado");
		repository.deleteById(id);
	}

	@Override
	public Optional<Produto> altera(Produto produto) {
		logger.info(">>>>>> 1.servico altera produto chamado");
		Optional<Produto> umProduto = consultaPorId(produto.getId());
		Endereco endereco = obtemEndereco(produto.getCep());
		if (umProduto.isPresent() & endereco != null) {
			Produto produtoModificado = new Produto(produto.getNome(), produto.getDataNascimento(), produto.getSexo(),
					produto.getCpf(), produto.getCep(), produto.getComplemento());
			produtoModificado.setId(produto.getId());
			produtoModificado.obtemDataAtual(new DateTime());
			produtoModificado.setEndereco(endereco.getLogradouro());
			logger.info(">>>>>> 2. servico altera produto cep valido para o id => " + produtoModificado.getId());
			return Optional.ofNullable(repository.save(produtoModificado));
		} else {
			return Optional.empty();
		}
	}

	public Endereco obtemEndereco(String cep) {
		RestTemplate template = new RestTemplate();
		String url = "https://viacep.com.br/ws/{cep}/json/";
		logger.info(">>>>>> servico consultaCep - " + cep);
		ResponseEntity<Endereco> resposta = null;
		try {
			resposta = template.getForEntity(url, Endereco.class, cep);
			return resposta.getBody();
		} catch (ResourceAccessException e) {
			logger.info(">>>>>> consulta CEP erro nao esperado ");
			return null;
		} catch (HttpClientErrorException e) {
			logger.info(">>>>>> consulta CEP inválido erro HttpClientErrorException =>" + e.getMessage());
			return null;
		}
	}
}