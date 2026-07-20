package com.generation.blogpessoal.repository;


import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.generation.blogpessoal.model.Tema;


public interface TemaRepository extends JpaRepository<Tema, Long>{
		

		public @Nullable Object findAllByDescricaoContainingIgnoreCase(String descricao);
		
		
	}
