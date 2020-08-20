package com.example.graph;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@TestConstructor(autowireMode = AutowireMode.ALL)
@TestInstance(Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase
class BookRepositoryTest extends ApplicationTests {
	private final BookRepository bookRepository;
	private final AuthorRepository authorRepository;
	public BookRepositoryTest(AuthorRepository authorRepository, BookRepository bookRepository) {
		this.bookRepository = bookRepository;
		this.authorRepository = authorRepository;
		
	}

	@BeforeAll
	void init() {
		authorRepository.saveAll(Arrays.asList( //
				new Author("author-1", "Joanne", "Rowling"), //
				new Author("author-2", "Herman", "Melville"), //
				new Author("author-3", "Anne", "Rice")));
	}

	@Test
	void testSave() {
		var book = new Book("book-1", "Harry Potter and the Philosopher's Stone", 223, "author-1");
		bookRepository.save(book);
		assertThat(bookRepository.findById("book-1")).isPresent();
	}
}
