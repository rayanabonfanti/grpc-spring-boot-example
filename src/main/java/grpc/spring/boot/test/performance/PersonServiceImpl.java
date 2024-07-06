package grpc.spring.boot.test.performance;

import grpc.spring.boot.test.performance.models.Empty;
import grpc.spring.boot.test.performance.models.PersonRequest;
import grpc.spring.boot.test.performance.models.PersonResponse;
import grpc.spring.boot.test.performance.models.PersonServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.stream.IntStream;

@GrpcService
public class PersonServiceImpl extends PersonServiceGrpc.PersonServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(PersonServiceImpl.class);

    @Override
    public void createPerson(PersonRequest personRequest, StreamObserver<PersonResponse> responsePersonObserver) {
        PersonResponse reply = PersonResponse.newBuilder().setId(UUID.randomUUID().toString()).setName(personRequest.getName()).setAge(personRequest.getAge()).build();
        responsePersonObserver.onNext(reply);
        responsePersonObserver.onCompleted();
    }

    @Override
    public void getAllPeople(Empty request, StreamObserver<PersonResponse> responsePersonObserver) {
        long start = System.currentTimeMillis();

        log.info("Iniciando requisição com tempo: {} milissegundos", start);
        IntStream.range(0, 1000)
                .mapToObj(i -> PersonResponse.newBuilder()
                        .setId(UUID.randomUUID().toString())
                        .setName("Person " + i)
                        .setAge(20 + i)
                        .build())
                .forEach(responsePersonObserver::onNext);
        log.info("Tempo total antes do onCompleted de processamento: {} milissegundos", System.currentTimeMillis() - start);
        responsePersonObserver.onCompleted();
        log.info("Tempo total depois do onCompleted de processamento: {} milissegundos", System.currentTimeMillis() - start);
    }

}