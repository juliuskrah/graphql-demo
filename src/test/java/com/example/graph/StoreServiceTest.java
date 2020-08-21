package com.example.graph;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {
	@Mock
	private AuthorRepository authorRepository;
	@Mock
	private BookRepository bookRepository;
	private StoreService storeService;

	@BeforeEach
	void initMocks() {
		storeService = new BookStoreService(authorRepository, bookRepository);
	}

	private static List<Book> books = Arrays.asList(
			new Book("book-1", "Harry Potter and the Philosopher's Stone", 223, "author-1"),
			new Book("book-2", "Moby Dick", 635, "author-2"),
			new Book("book-3", "Interview with the vampire", 371, "author-3"));

	@Test
	void testFindBookById() {
		when(bookRepository.findById(anyString())).then(invocation -> {
			String id = invocation.getArgument(0);
			return books.stream().filter(book -> id.equals(book.getId())).findFirst();
		});
		
		var book = storeService.findBookById("book-3");
		assertThat(book).isPresent();
		Function<Book, Optional<String>> extractName = b -> Optional.of(b.getName());
		assertThat(book).flatMap(extractName).contains("Interview with the vampire");	
		verify(bookRepository, times(1)).findById("book-3");
	}

	@Test
	void testFindAuthorById() {
		when(authorRepository.findById(anyString())).thenReturn( //
				Optional.of(new Author("author-1", "Joanne", "Rowling")));
		
		var author = storeService.findAuthorById("author-2");
		assertThat(author).isPresent();
		assertThat(author).get().extracting("firstName").isEqualTo("Joanne");
		verify(authorRepository, times(1)).findById("author-2");
	}

}
