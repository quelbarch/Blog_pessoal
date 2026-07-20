package com.generation.blogpessoal.controller;

import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Tema;
import com.generation.blogpessoal.repository.TemaRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/temas") 
@CrossOrigin(origins = "*", allowedHeaders = "*") 
public class TemaController {

    @Autowired 
    private TemaRepository TemaRepository;

    @GetMapping 
    public ResponseEntity<@Nullable Object> getAll() {
        return ResponseEntity.ok(TemaRepository.findAll());
          
	}
    
    @GetMapping("/{id}") 
    public ResponseEntity<Tema> getById(@PathVariable Long id) {
		return TemaRepository.findById(id)
				.map(resposta -> ResponseEntity.ok(resposta)) 
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()); 	
	}
    
    @GetMapping("/descricao/{descricao}")
    public ResponseEntity<@Nullable Object> getAllByDescricao(@PathVariable String descricao) {
		return ResponseEntity.ok(TemaRepository.findAllByDescricaoContainingIgnoreCase(descricao));
		
		}
    
    @PostMapping
    public ResponseEntity<Tema> post(@Valid @RequestBody Tema TemaModel) {
    	TemaModel.setId(null);
		return ResponseEntity.status(HttpStatus.CREATED).body(TemaRepository.save(TemaModel));
		
    }
    
    @PutMapping 
    public ResponseEntity<Tema> put(@Valid @RequestBody Tema TemaModel) {
    	return TemaRepository.findById(TemaModel.getId())
                .map(resposta -> ResponseEntity.status(HttpStatus.CREATED)
                .body(TemaRepository.save(TemaModel)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }
    
    @ResponseStatus(HttpStatus.NO_CONTENT) 
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
    	Optional<Tema> TemaModel = TemaRepository.findById(id); 
    	if(TemaModel.isEmpty()) 
			throw new ResponseStatusException(HttpStatus.NOT_FOUND); 
		TemaRepository.deleteById(id); 
    }
}