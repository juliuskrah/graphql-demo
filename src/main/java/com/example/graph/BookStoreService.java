package com.example.graph;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class BookStoreService implements StoreService {
	private static final Logger log = LoggerFactory.getLogger(BookStoreService.class);
	private final AuthorRepository authorRepository;
	private final BookRepository bookRepository;

	public BookStoreService(AuthorRepository authorRepository, BookRepository bookRepository) {
		this.authorRepository = authorRepository;
		this.bookRepository = bookRepository;
	}

	@Override
	@Cacheable("authors")
	public Optional<Author> findAuthorById(String id) {
		log.info("Fetching author with id - {}", id);
		return authorRepository.findById(id);
	}

	@Override
	@Cacheable("books")
	public Optional<Book> findBookById(String id) {
		log.info("Fetching book with id - {}", id);
		return bookRepository.findById(id);
	}

}
