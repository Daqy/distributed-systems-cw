package com.example.grpc.server.grpcserver;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;

public class GRPCServer extends Thread {
  private final int _port;
  private final int _currentThread;
  public final Server _server;
  public GRPCServer(int port, int currentThread) {
    this._port = port;
    this._currentThread = currentThread;
    this._server = ServerBuilder.forPort(port).addService(new MatrixServiceImpl()).build();
  }

  public void startServer() throws IOException, InterruptedException {
      this._server.start();
      System.out.printf("Server %d started on port: %d\n", this._currentThread, this._port);
      this._server.awaitTermination();
  } 
}
