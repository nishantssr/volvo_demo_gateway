package gateway.schema;

import com.google.api.graphql.rejoiner.Mutation;
import com.google.api.graphql.rejoiner.Query;
import com.google.api.graphql.rejoiner.SchemaModification;
import com.google.api.graphql.rejoiner.SchemaModule;
import com.google.api.graphql.rejoiner.Type;
import com.google.api.graphql.rejoiner.TypeModification;
import com.google.common.util.concurrent.ListenableFuture;
import com.volvo.demo.author.*;
import com.volvo.demo.book.Book;
import gateway.dataloader.BookBatchLoader;
import graphql.schema.DataFetchingEnvironment;
import net.javacrumbs.futureconverter.java8guava.FutureConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

final class AuthorSchemaModule extends SchemaModule {
    final Logger log = LoggerFactory.getLogger(AuthorSchemaModule.class);
    @Mutation("createAuthor")
    ListenableFuture<Author> createAuthor(CreateAuthorRequest request, AuthorServiceGrpc.AuthorServiceFutureStub stub) {
        log.info("AuthorSchemaModule createAuthor is called request{}:",request);
        return stub.createAuthor(request);
    }

    @Query("getAuthor")
    ListenableFuture<Author> getAuthor(GetAuthorRequest request, AuthorServiceGrpc.AuthorServiceFutureStub stub) {
        log.info("AuthorSchemaModule getAuthor is called request{}:",request);
        return stub.getAuthor(request);
    }

    @Mutation("addBook")
    ListenableFuture<Author> addBook(AddBookRequest request, AuthorServiceGrpc.AuthorServiceFutureStub stub) {
        log.info("AuthorSchemaModule addBook is called request{}:",request);
        return stub.addBook(request);
    }

    @SchemaModification
    TypeModification removeBookIds = Type.find(Author.getDescriptor()).removeField("bookIds");

    @SchemaModification(addField = "books", onType = Author.class)
    ListenableFuture<List<Book>> authorToBooks(Author author, DataFetchingEnvironment environment) {
        log.info("AuthorSchemaModule authorToBooks is called request{}:",author);
        return FutureConverter.toListenableFuture(
                environment.<Integer, Book>getDataLoader("books").loadMany(author.getBookIdsList())
        );
    }
}