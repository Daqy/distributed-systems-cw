package com.example.grpc.server.grpcserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.io.IOException;

@SpringBootApplication
public class GrpcServerApplication extends SpringBootServletInitializer {
	static int[] ports = {8080,8081,8082,8083,8084,8085,8086,8087};
	public static void main(String[] args) {
		SpringApplication.run(GrpcServerApplication.class, args);

		int cores = Runtime.getRuntime().availableProcessors();

		for (int index = 0; index < cores; index++) {
			new GRPCServer(ports[index], index+1).startServer();
		}
	}
}
