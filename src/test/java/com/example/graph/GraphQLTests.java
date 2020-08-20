package com.example.graph;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLTest;
import com.graphql.spring.boot.test.GraphQLTestTemplate;

import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

@TestConstructor(autowireMode = AutowireMode.ALL)
@GraphQLTest(profiles = "graph", includeFilters = {
		@ComponentScan.Filter(type = ASSIGNABLE_TYPE, classes = { GraphQLDataFetchers.class }) })
@TestPropertySource(properties = "debug=true")
@Import(GraphQLTests.TestConfig.class)
@TestInstance(Lifecycle.PER_CLASS)
class GraphQLTests {
	private final GraphQLTestTemplate template;
	private final ObjectMapper mapper;
	@MockBean
	private AuthorRepository authorRepository;
	@MockBean
	private BookRepository bookRepository;

	@BeforeAll
	void initMocks() {
		when(authorRepository.findById(anyString())).thenReturn( //
				Optional.of(new Author("author-1", "Joanne", "Rowling")));
		when(bookRepository.findById(anyString())).then(invocation -> {
			String id = invocation.getArgument(0);
			return books.stream().filter(book -> id.equals(book.getId())).findFirst();
		});
	}

	private static List<Book> books = Arrays.asList(
			new Book("book-1", "Harry Potter and the Philosopher's Stone", 223, "author-1"),
			new Book("book-2", "Moby Dick", 635, "author-2"),
			new Book("book-3", "Interview with the vampire", 371, "author-3"));

	GraphQLTests(GraphQLTestTemplate template, ObjectMapper mapper) {
		this.template = template;
		this.mapper = mapper;
	}

	@Test
	@DisplayName("Testing Graph Query 'findById'")
	void testQueryFindById() throws IOException {
		ObjectNode variables = mapper.createObjectNode();
		variables.put("bookId", "book-1");
		var response = template.perform("graphql/get-book.graphql", variables);
		assertThat(response.isOk()).isTrue();
		assertThat(response.get("$.data.bookById.name")).isEqualTo("Harry Potter and the Philosopher's Stone");
		// --
		variables.put("bookId", "book-2");
		response = template.perform("graphql/get-book.graphql", variables);
		assertThat(response.isOk()).isTrue();
		assertThat(response.get("$.data.bookById.name")).isEqualTo("Moby Dick");
		assertThat(response.get("$.data.bookById.pageCount")).isEqualTo("635");
	}

	@TestConfiguration
	public static class TestConfig {
		@Value("classpath:schema.graphqls")
		private Resource resource;
		@Autowired
		private GraphQLDataFetchers dataFetchers;

		private String loadResource(Resource resource) throws IOException {
			try (InputStream inputStream = resource.getInputStream()) {
				return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
			}
		}

		@Bean
		GraphQLSchema schema() throws IOException {
			var sdl = loadResource(resource);
			TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
			RuntimeWiring runtimeWiring = Application.buildWiring(dataFetchers);
			SchemaGenerator schemaGenerator = new SchemaGenerator();
			return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
		}
	}
}
