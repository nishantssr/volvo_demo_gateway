package gateway.client;

import com.google.inject.AbstractModule;
import com.volvo.demo.book.BookServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class BookClient extends AbstractModule {
    final Logger log = LoggerFactory.getLogger(BookClient.class);
    private static final String HOST = "localhost";
    private static final int PORT = 9091;

    @Override
    protected void configure() {
        log.info("BookClient configure called :");
        ManagedChannel channel = ManagedChannelBuilder.forAddress(HOST, PORT).usePlaintext().build();
        bind(BookServiceGrpc.BookServiceFutureStub.class).toInstance(BookServiceGrpc.newFutureStub(channel));
        bind(BookServiceGrpc.BookServiceBlockingStub.class).toInstance(BookServiceGrpc.newBlockingStub(channel));
        log.info("BookClient configure done :");
    }
}
