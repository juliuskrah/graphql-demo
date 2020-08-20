package com.example.graph;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

@SpringBootApplication
public class Application {
	@Autowired
	GraphQLDataFetchers graphQLDataFetchers;
	@Value("classpath:schema.graphqls")
	private Resource resource;
	private GraphQL graphQL;

	@Bean
	public GraphQL graphQL() {
		return graphQL;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@PostConstruct
	public void init() throws IOException {
		GraphQLSchema graphQLSchema = graphQLSchema();
		this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
	}

	@Bean
	public GraphQLSchema graphQLSchema() throws IOException {
		var sdl = loadResource(resource);
		TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
		RuntimeWiring runtimeWiring = buildWiring(graphQLDataFetchers);
		SchemaGenerator schemaGenerator = new SchemaGenerator();
		return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
	}

	private String loadResource(Resource resource) throws IOException {
		try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
			return FileCopyUtils.copyToString(reader);
		}
	}

	public static RuntimeWiring buildWiring(GraphQLDataFetchers graphQLDataFetchers) {
		return RuntimeWiring.newRuntimeWiring() //
				.type(newTypeWiring("Query") //
						.dataFetcher("bookById", graphQLDataFetchers.getBookByIdDataFetcher()))
				.type(newTypeWiring("Book") //
						.dataFetcher("author", graphQLDataFetchers.getAuthorDataFetcher()))
				.build();
	}

}
