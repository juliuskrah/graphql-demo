package com.example.graph;

import org.springframework.stereotype.Component;

import graphql.schema.DataFetcher;

@Component
public class GraphQLDataFetchers {
	private final AuthorRepository authorRepository;
	private final BookRepository bookRepository;
	
	public GraphQLDataFetchers(BookRepository bookRepository, AuthorRepository authorRepository) {
		this.authorRepository = authorRepository;
		this.bookRepository = bookRepository;
	}

    public DataFetcher<Book> getBookByIdDataFetcher() {
        return dataFetchingEnvironment -> {
            String bookId = dataFetchingEnvironment.getArgument("id");
            return bookRepository.findById(bookId)
                    .orElse(null);
        };
    }

    public DataFetcher<Author> getAuthorDataFetcher() {
        return dataFetchingEnvironment -> {
            Book book = dataFetchingEnvironment.getSource();
            String authorId = book.getAuthorId();
            return authorRepository.findById(authorId)
                    .orElse(null);
        };
    }
}
