package gateway.dataloader;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.volvo.demo.book.Book;
import com.volvo.demo.book.BookServiceGrpc;
import com.volvo.demo.book.ListBooksRequest;
import net.javacrumbs.futureconverter.java8guava.FutureConverter;
import org.dataloader.BatchLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletionStage;

@Component
public class BookBatchLoader implements BatchLoader<Integer, Book> {
    final Logger log = LoggerFactory.getLogger(BookBatchLoader.class);
    private final BookServiceGrpc.BookServiceFutureStub futureStub;

    public BookBatchLoader(BookServiceGrpc.BookServiceFutureStub futureStub) {
        this.futureStub = futureStub;
    }

    @Override
    public CompletionStage<List<Book>> load(List<Integer> keys) {
        log.info("BookBatchLoader load is called with keys {}:",keys);
        ListenableFuture<List<Book>> listenableFuture =
                Futures.transform(futureStub.listBooks(ListBooksRequest.newBuilder().addAllIds(keys).build()),
                        listBooksResponse -> listBooksResponse != null ? listBooksResponse.getBooksList() : null,
                        MoreExecutors.directExecutor());
        return FutureConverter.toCompletableFuture(listenableFuture);
    }
}

