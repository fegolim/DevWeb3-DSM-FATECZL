package com.fatec.sig1.adapters;

import java.util.Optional;
import javax.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import com.fatec.sig1.model.Produto;
import com.fatec.sig1.ports.MantemProduto;

@SuppressWarnings("unused")
@Controller
@RequestMapping(path = "/sig")
public class GUIProdutoController {
	Logger logger = LogManager.getLogger(GUIProdutoController.class);
	@Autowired
	MantemProduto servico1;

	@GetMapping("/produto")
	public ModelAndView retornaFormDeConsultaTodosProdutos() {
		ModelAndView modelAndView = new ModelAndView("consultarProduto");
		modelAndView.addObject("produto", servico1.consultaTodos());
		return modelAndView;
	}

	@GetMapping("/produtos")
	public ModelAndView retornaFormDeCadastroTodosProdutos(Produto produto) {
		ModelAndView mv = new ModelAndView("cadastrarProduto");
		mv.addObject("produtos", produto);
		return mv;
	}

	@GetMapping("/produto/{cpf}") // diz ao metodo que ira responder a uma requisicao do tipo get
	public ModelAndView retornaFormParaEditarProduto(@PathVariable("cpf") String cpf) {
		ModelAndView modelAndView = new ModelAndView("atualizarProduto");
		modelAndView.addObject("produto", servico1.consultaPorCpf(cpf).get()); // retorna um objeto do tipo cliente
		return modelAndView; // addObject adiciona objetos para view
	}

	@GetMapping("/produto/id/{id}")
	public ModelAndView excluirNoFormDeConsultaProduto(@PathVariable("id") Long id) {
		servico1.delete(id);
		logger.info(">>>>>> 1. servico de exclusao chamado para o id => " + id);
		ModelAndView modelAndView = new ModelAndView("consultarProduto");
		modelAndView.addObject("produto", servico1.consultaTodos());
		return modelAndView;
	}

	@PostMapping("/produto")
	public ModelAndView save(@Valid Produto produto, BindingResult result) {
		ModelAndView modelAndView = new ModelAndView("consultarProduto");
		if (result.hasErrors()) {
			modelAndView.setViewName("cadastrarProduto");
		} else {
			if (servico1.save(produto).isPresent()) {
				logger.info(">>>>>> controller chamou adastrar e consulta todos");
				modelAndView.addObject("produto", servico1.consultaTodos());
			} else {
				logger.info(">>>>>> controller cadastrar com dados invalidos");
				modelAndView.setViewName("cadastrarProduto");
				modelAndView.addObject("message", "Dados invalidos");
			}
		}
		return modelAndView;
	}

	@PostMapping("/produto/id/{id}")
	public ModelAndView atualizaProduto(@PathVariable("id") Long id, @Valid Produto produto, BindingResult result) {
		ModelAndView modelAndView = new ModelAndView("consultarProduto");
		logger.info(">>>>>> servico para atualizacao de dados chamado para o id => " + id);
		if (result.hasErrors()) {
			logger.info(">>>>>> servico para atualizacao de dados com erro => " + result.getFieldError().toString());
			produto.setId(id);
			return new ModelAndView("atualizarProduto");
		} else {
			servico1.altera(produto);
			modelAndView.addObject("produto", servico1.consultaTodos());
		}
		return modelAndView;
	}
}