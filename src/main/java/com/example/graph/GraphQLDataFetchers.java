package com.example.graph;

import org.springframework.stereotype.Component;

import graphql.schema.DataFetcher;

@Component
public class GraphQLDataFetchers {
	private final StoreService storeService;
	
	public GraphQLDataFetchers(StoreService storeService) {
		this.storeService = storeService;
	}

    public DataFetcher<Book> getBookByIdDataFetcher() {
        return dataFetchingEnvironment -> {
            String bookId = dataFetchingEnvironment.getArgument("id");
            return storeService.findBookById(bookId)
                    .orElse(null);
        };
    }

    public DataFetcher<Author> getAuthorDataFetcher() {
        return dataFetchingEnvironment -> {
            Book book = dataFetchingEnvironment.getSource();
            String authorId = book.getAuthorId();
            return storeService.findAuthorById(authorId)
                    .orElse(null);
        };
    }
}
