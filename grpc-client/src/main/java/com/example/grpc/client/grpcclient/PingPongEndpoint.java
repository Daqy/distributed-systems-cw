package com.example.grpc.client.grpcclient;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import java.nio.charset.StandardCharsets;

@RestController
public class PingPongEndpoint {    

	GRPCClientService grpcClientService;    

	@Autowired
	public PingPongEndpoint(GRPCClientService grpcClientService) {
		this.grpcClientService = grpcClientService;
	}

	//https://stackoverflow.com/questions/38700790/how-to-return-a-html-page-from-a-restful-controller-in-spring-boot
	@GetMapping("/")
	public ModelAndView welcome() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("file.html");
		return modelAndView;
	}  

	// https://stackoverflow.com/questions/31393553/how-to-read-a-multipart-file-as-a-string-in-spring
	@PostMapping("/")
	public String multiplyMatrix(@RequestParam("matrixFile1") MultipartFile matrixFile1, @RequestParam("matrixFile2") MultipartFile matrixFile2) {
		try {
			String matrixAContent = new String(matrixFile1.getBytes(), StandardCharsets.UTF_8);
			String matrixBContent = new String(matrixFile2.getBytes(), StandardCharsets.UTF_8);
			return grpcClientService.multiplyFiles(matrixAContent, matrixBContent);
		} catch (Exception error) {
			error.printStackTrace();
			return  error.getLocalizedMessage();
		}
	}
}
