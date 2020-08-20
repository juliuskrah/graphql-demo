package com.example.graph;

import java.util.Optional;

public interface StoreService {
	Optional<Author> findAuthorById(String id);

	Optional<Book> findBookById(String id);
}
