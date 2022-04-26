package com.example.grpc.server.grpcserver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class GrpcServerApplication extends SpringBootServletInitializer {
	static int[] ports = {8082,8083,8084,8085,8086,8087,8088,8089};
	public static void main(String[] args) {
		SpringApplication.run(GrpcServerApplication.class, args);

		int cores = Runtime.getRuntime().availableProcessors();
    ExecutorService executorService = Executors.newFixedThreadPool(cores);
    for (int i = 0; i < cores; i++) {
        int name = i+1;
        int port = ports[i];
        executorService.submit(() -> {
            try {
							new GRPCServer(port, name).startServer();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
		// new GRPCServer(8085, 1).startServer();
		// new GRPCServer(8086, 2).startServer();

		// for (int index = 0; index < cores; index++) {
		// 	try {
		// 		Server server = ServerBuilder.forPort(ports[index]).addService(new MatrixServiceImpl()).build();
		// 		// new GRPCServer(ports[index], index+1);
		// 		server.start();
		// 		System.out.printf("Server %d started on port: %d\n", index+1, ports[index]);
		// 		server.awaitTermination();
		// 	} catch (IOException error) {
		// 		error.printStackTrace();
		// 	} catch (InterruptedException error) {
		// 		error.printStackTrace();
		// 	}
		// 	;
		// }
	}
}
