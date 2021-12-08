package gateway;
import com.google.api.graphql.execution.GuavaListenableFutureSupport;
import com.google.api.graphql.rejoiner.Schema;
import com.google.api.graphql.rejoiner.SchemaProviderModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.volvo.demo.book.BookServiceGrpc;
import gateway.client.GrpcClientModule;
import gateway.dataloader.BookBatchLoader;
import gateway.schema.GraphQlSchemaModule;
import graphql.schema.GraphQLSchema;
import graphql.servlet.config.DefaultGraphQLSchemaProvider;
import graphql.servlet.config.GraphQLSchemaProvider;
import graphql.servlet.context.DefaultGraphQLContext;
import graphql.servlet.context.DefaultGraphQLServletContext;
import graphql.servlet.context.DefaultGraphQLWebSocketContext;
import graphql.servlet.context.GraphQLContext;
import graphql.servlet.context.GraphQLContextBuilder;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import graphql.execution.instrumentation.Instrumentation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;

@SpringBootApplication
public class VolvoDemoGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(VolvoDemoGatewayApplication.class, args);
	}

	private final Injector injector;
	{
		injector = Guice.createInjector(
				new SchemaProviderModule(),
				new GrpcClientModule(),
				new GraphQlSchemaModule()
		);
	}

	@Bean
	public GraphQLSchemaProvider schemaProvider() {
		GraphQLSchema schema = injector.getInstance(Key.get(GraphQLSchema.class, Schema.class));
		return new DefaultGraphQLSchemaProvider(schema);
	}

	@Bean
	public Instrumentation instrumentation() {
		return GuavaListenableFutureSupport.listenableFutureInstrumentation();
	}

	@Bean
	public BookServiceGrpc.BookServiceFutureStub bookServiceFutureStub() {
		return injector.getInstance(BookServiceGrpc.BookServiceFutureStub.class);
	}

	@Bean
	public DataLoaderRegistry buildDataLoaderRegistry(BookBatchLoader bookBatchLoader) {
		DataLoaderRegistry registry = new DataLoaderRegistry();
		registry.register("books", new DataLoader<>(bookBatchLoader));
		return registry;
	}

	@Bean
	public GraphQLContextBuilder contextBuilder(DataLoaderRegistry dataLoaderRegistry) {
		return new GraphQLContextBuilder() {
			@Override
			public GraphQLContext build(HttpServletRequest req, HttpServletResponse response) {
				return DefaultGraphQLServletContext.createServletContext(dataLoaderRegistry, null).with(req).with(response).build();
			}

			@Override
			public GraphQLContext build() {
				return new DefaultGraphQLContext(dataLoaderRegistry, null);
			}

			@Override
			public GraphQLContext build(Session session, HandshakeRequest request) {
				return DefaultGraphQLWebSocketContext.createWebSocketContext(dataLoaderRegistry, null).with(session).with(request).build();
			}
		};
	}
}
