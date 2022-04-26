package com.example.grpc.server.grpcserver;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;

public class GRPCServer extends Thread {
  private final int _port;
  private final int _currentThread;
  private final Server _server;
  public GRPCServer(int port, int currentThread) {
    this._port = port;
    this._currentThread = currentThread;
    this._server = ServerBuilder.forPort(port).addService(new MatrixServiceImpl()).build();
  }

  public static void main(String[] args) {
    Runtime runtime = Runtime.getRuntime();
    int numberOfProcessors = runtime.availableProcessors();
    System.out.println("Number of processors available to this JVM: " + numberOfProcessors);

    // for(int i = 0; i<numberOfProcessors; i++) {
    //     new GrpcServer(ports[i],i+1).start();
    // }
}

  public void startServer() {
    try {
      this._server.start();
      System.out.printf("Server %d started on port: %d\n", this._currentThread, this._port);
      this._server.awaitTermination();
    } catch (IOException error) {
      error.printStackTrace();
    } catch (InterruptedException error) {
      error.printStackTrace();
    }
  } 
}
