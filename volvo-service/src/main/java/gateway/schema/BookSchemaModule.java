package gateway.schema;

import com.google.api.graphql.rejoiner.Mutation;
import com.google.api.graphql.rejoiner.Query;
import com.google.api.graphql.rejoiner.SchemaModule;
import com.google.common.util.concurrent.ListenableFuture;
import com.volvo.demo.author.AddBookRequest;
import com.volvo.demo.author.Author;
import com.volvo.demo.author.AuthorServiceGrpc;
import com.volvo.demo.book.Book;
import com.volvo.demo.book.BookServiceGrpc;
import com.volvo.demo.book.CreateBookRequest;
import com.volvo.demo.book.GetBookRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class BookSchemaModule extends SchemaModule {
    final Logger log = LoggerFactory.getLogger(BookSchemaModule.class);
    @Mutation("createBook")
    Book createBook(CreateBookRequest request, BookServiceGrpc.BookServiceBlockingStub bookStub,
                    AuthorServiceGrpc.AuthorServiceBlockingStub authorStub) {
        log.info("BookSchemaModule createBook is called request{}:",request);
        Book book = bookStub.createBook(request);
        Author author = authorStub.addBook(AddBookRequest.newBuilder()
                .setAuthorId(request.getAuthorId())
                .setBookId(book.getId()).build());
        return book;
    }

    @Query("getBook")
    ListenableFuture<Book> getBook(GetBookRequest request, BookServiceGrpc.BookServiceFutureStub stub) {
        log.info("BookSchemaModule getBook is called request{}:",request);
        return stub.getBook(request);
    }
}

