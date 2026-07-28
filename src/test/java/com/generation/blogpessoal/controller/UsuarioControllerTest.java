package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;
import com.generation.blogpessoal.util.JwtHelper;
import com.generation.blogpessoal.util.TestBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class UsuarioControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	private static final String BASE_URL = "/usuarios";
	private static final String USUARIO = "root@root.com";
	private static final String SENHA = "rootroot";

	@BeforeEach
	void inicio() {
		usuarioRepository.deleteAll(); 
		usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Root", USUARIO, SENHA));
	}
	
	@Test
	@DisplayName("01 - Deve cadastrar um novo usuário com sucesso")
	void deveCadastrarUsuario() {
		// GIVEN
		Usuario usuario = TestBuilder.criarUsuario(null, "Raquel Barcheta", "raquel@gmail.com.br", "quel1234");
		
		// WHEN
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuario);
		ResponseEntity<Usuario> resposta = testRestTemplate.exchange(BASE_URL + "/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		// THEN
		assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
		assertNotNull(resposta.getBody());
	}
	
	@Test
	@DisplayName("02 - Não deve cadastrar usuário duplicado")
	void naoDeveCadastrarUsuarioDuplicado() {
		// GIVEN
		Usuario usuario = TestBuilder.criarUsuario(null, "Luiza Guimarães", "luiza@gmail.com.br", "luiza1234");
		usuarioService.cadastrarUsuario(usuario);
						
		// WHEN
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuario);
		ResponseEntity<Usuario> resposta = testRestTemplate.exchange(BASE_URL + "/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
				
		// THEN
		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode()); 
		assertNull(resposta.getBody()); 
	}
	
	@Test
	@DisplayName("03 - Deve listar todos os usuários")
	void deveListarTodosUsuarios() {
		// GIVEN
		usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Kaue Dota", "kaue@gmail.com.br", "kaue1234"));
		usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Edson Nascimento", "edson@gmail.com.br", "edson1234"));
	
		// WHEN
		String token = JwtHelper.obterToken(testRestTemplate, USUARIO, SENHA);
		HttpEntity<Void> cabecalhoRequisicao = JwtHelper.criarRequisicaoComToken(token);
		ResponseEntity<Usuario[]> resposta = testRestTemplate.exchange(BASE_URL + "/all", HttpMethod.GET, cabecalhoRequisicao, Usuario[].class);
				
		// THEN
		assertEquals(HttpStatus.OK, resposta.getStatusCode()); 
		assertNotNull(resposta.getBody()); 
	}
	
	@Test
	@DisplayName("04 - Deve atualizar os dados do usuário com sucesso")
	void deveAtualizarUsuario() {
		// GIVEN
		Usuario usuario = TestBuilder.criarUsuario(null, "Daniel", "daniel@gmail.com.br", "daniel1234");
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(usuario);
		
		Usuario usuarioUpdate = TestBuilder.criarUsuario(usuarioCadastrado.get().getId(), "Daniel Araujo", "daniel_araujo@gmail.com.br", "abcd1234");
		
		// WHEN
		String token = JwtHelper.obterToken(testRestTemplate, USUARIO, SENHA);
		HttpEntity<Usuario> cabecalhoRequisicao = JwtHelper.criarRequisicaoComToken(usuarioUpdate, token);

		ResponseEntity<Usuario> resposta = testRestTemplate.exchange(BASE_URL + "/atualizar", HttpMethod.PUT, cabecalhoRequisicao, Usuario.class);
			
		// THEN
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertNotNull(resposta.getBody());
	}
	
	@Test
	@DisplayName("05 - Deve listar usuário por ID")
	void deveListarUsuarioPorId() {
		// GIVEN
		Usuario usuario = TestBuilder.criarUsuario(null, "Carla", "carla@gmail.com.br", "caca1234");
		Optional<Usuario> usuarioCriado = usuarioService.cadastrarUsuario(usuario);

		// WHEN
		String token = JwtHelper.obterToken(testRestTemplate, USUARIO, SENHA);
		HttpEntity<Void> cabecalhoRequisicao = JwtHelper.criarRequisicaoComToken(token);
		ResponseEntity<Usuario> resposta = testRestTemplate.exchange(BASE_URL + "/" + usuarioCriado.get().getId(), HttpMethod.GET, cabecalhoRequisicao, Usuario.class);
				
		// THEN
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertNotNull(resposta.getBody()); 
	}
}