package gateway.client;

import com.google.inject.AbstractModule;
import com.volvo.demo.author.AuthorServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class AuthorClient extends AbstractModule {
    final Logger log = LoggerFactory.getLogger(AuthorClient.class);
    private static final String HOST = "localhost";
    private static final int PORT = 9091;

    @Override
    protected void configure() {
        log.info("AuthorClient configure called :");
        ManagedChannel channel = ManagedChannelBuilder.forAddress(HOST, PORT).usePlaintext().build();
        bind(AuthorServiceGrpc.AuthorServiceFutureStub.class).toInstance(AuthorServiceGrpc.newFutureStub(channel));
        bind(AuthorServiceGrpc.AuthorServiceBlockingStub.class).toInstance(AuthorServiceGrpc.newBlockingStub(channel));
        log.info("AuthorClient configure done :");
    }
}