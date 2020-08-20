package com.example.graph;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@TestConstructor(autowireMode = AutowireMode.ALL)
@AutoConfigureTestDatabase
class AuthorRepositoryTest extends ApplicationTests {
	private final AuthorRepository authorRepository;
	
	AuthorRepositoryTest(AuthorRepository authorRepository) {
		this.authorRepository = authorRepository;
	}
	
	@Test
	void testSave() {
		var author = new Author("author-1", "Joanne", "Rowling");
		assertDoesNotThrow(() -> authorRepository.save(author), () -> "Cannot save Author");
	}
}
