package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.PingRequest;
import com.example.grpc.server.grpcserver.PongResponse;
import com.example.grpc.server.grpcserver.PingPongServiceGrpc;
import com.example.grpc.server.grpcserver.MatrixRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

import com.example.grpc.server.grpcserver.MatrixReply;
import com.example.grpc.server.grpcserver.MatrixServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;


@Service
public class GRPCClientService {
	static int[] ports = {8082,8083,8084,8085,8086,8087,8088,8089};

	public ManagedChannel[] createChannels() {
		ManagedChannel[] ch = new ManagedChannel[ports.length];
		for (int index = 0; index < ports.length; index++) {
			ch[index] = ManagedChannelBuilder.forAddress("localhost", ports[index]).usePlaintext().build();
		}
		return ch;
	};

	public MatrixServiceGrpc.MatrixServiceBlockingStub[] createStubs(ManagedChannel[] ch) {
		MatrixServiceGrpc.MatrixServiceBlockingStub[] st = new MatrixServiceGrpc.MatrixServiceBlockingStub[ports.length];
		for (int index = 0; index < ports.length; index++) {
			st[index] = MatrixServiceGrpc.newBlockingStub(ch[index]);
		}
		return st;
	}

	public String multiplyFiles(String matrixAContent, String matrixBContent) throws Exception {
		long deadline = 20 * 1000;
		validateMatrix(matrixAContent);
		validateMatrix(matrixBContent);
		int[][] matrixA = MatrixConversion.StringToIntArray(matrixAContent);
		int[][] matrixB = MatrixConversion.StringToIntArray(matrixBContent);

		List<int[][]> blocks = createBlocks(matrixA, matrixB);

		ManagedChannel[] channels = createChannels();
		MatrixServiceGrpc.MatrixServiceBlockingStub[] stubs = createStubs(channels);

		long startTime = System.nanoTime();

		CompletableFuture<MatrixReply> a1a2sync = CompletableFuture.supplyAsync(() -> stubs[0].multiplyBlock(MatrixRequest.newBuilder()
		.setMatrixA(MatrixConversion.IntArrayToString(blocks.get(0)))
		.setMatrixB(MatrixConversion.IntArrayToString(blocks.get(1)))
		.build()));

		MatrixReply a1a2 = a1a2sync.get();
		long endTime = System.nanoTime();
		long footprint= endTime-startTime;

		int numberServer = (int) Math.ceil((float)footprint*(float)11/(float)deadline);
		numberServer = numberServer <= 8 ? numberServer : 8;

		BlockingQueue<Integer> stubCounter = new LinkedBlockingQueue<>((int) 11);

		for (int index = 0; index < 11; index++) {
			stubCounter.add(index > numberServer ? index-numberServer : index);
		}
		// int stubCounter = 1;
		
		try {
			MatrixReply b1c2 = CompletableFuture.supplyAsync(() -> stubs[stubCounter.take()].multiplyBlock(MatrixRequest.newBuilder()
			.setMatrixA(MatrixConversion.IntArrayToString(blocks.get(2)))
			.setMatrixB(MatrixConversion.IntArrayToString(blocks.get(5)))
			.build())).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		

		// stubCounter = stubCounter+1 > numberServer ? 0 : stubCounter+1;

		MatrixReply a1b2 = CompletableFuture.supplyAsync(() -> stubs[stubCounter.take()].multiplyBlock(MatrixRequest.newBuilder()
		.setMatrixA(MatrixConversion.IntArrayToString(blocks.get(0)))
		.setMatrixB(MatrixConversion.IntArrayToString(blocks.get(3)))
		.build())).get();

		// stubCounter = stubCounter+1 > numberServer ? 0 : stubCounter+1;

		MatrixReply b1d2 = CompletableFuture.supplyAsync(() -> stubs[stubCounter.take()].multiplyBlock(MatrixRequest.newBuilder()
		.setMatrixA(MatrixConversion.IntArrayToString(blocks.get(2)))
		.setMatrixB(MatrixConversion.IntArrayToString(blocks.get(7)))
		.build())).get();
		
		// stubCounter = stubCounter+1 > numberServer ? 0 : stubCounter+1;

		MatrixReply c1a2 = CompletableFuture.supplyAsync(() -> stubs[stubCounter.take()].multiplyBlock(MatrixRequest.newBuilder()
		.setMatrixA(MatrixConversion.IntArrayToString(blocks.get(4)))
		.setMatrixB(MatrixConversion.IntArrayToString(blocks.get(1)))
		.build())).get();

		// stubCounter = stubCounter+1 > numberServer ? 0 : stubCounter+1;

		MatrixReply d1c2 = CompletableFuture.supplyAsync(() -> stubs[stubCounter.take()].multiplyBlock(MatrixRequest.newBuilder()
		.setMatrixA(MatrixConversion.IntArrayToString(blocks.get(6)))
		.setMatrixB(MatrixConversion.IntArrayToString(blocks.get(5)))
		.build())).get();

		// stubCounter = stubCounter+1 > numberServer ? 0 : stubCounter+1;

		MatrixReply c1b2 = CompletableFuture.supplyAsync(() -> stubs[stubCounter.take()].multiplyBlock(MatrixRequest.newBuilder()
		.setMatrixA(MatrixConversion.IntArrayToString(blocks.get(4)))
		.setMatrixB(MatrixConversion.IntArrayToString(blocks.get(3)))
		.build())).get();

		// stubCounter = stubCounter+1 > numberServer ? 0 : stubCounter+1;

		MatrixReply d1d2 = CompletableFuture.supplyAsync(() -> stubs[stubCounter.take()].multiplyBlock(MatrixRequest.newBuilder()
		.setMatrixA(MatrixConversion.IntArrayToString(blocks.get(6)))
		.setMatrixB(MatrixConversion.IntArrayToString(blocks.get(7)))
		.build())).get();

		// stubCounter = stubCounter+1 > numberServer ? 0 : stubCounter+1;

		MatrixReply a3 = CompletableFuture.supplyAsync(() -> stubs[stubCounter.take()].addBlock(MatrixRequest.newBuilder()
		.setMatrixA(a1a2.getMatrix())
		.setMatrixB(b1c2.getMatrix())
		.build())).get();

		// stubCounter = stubCounter+1 > numberServer ? 0 : stubCounter+1;

		MatrixReply b3 = CompletableFuture.supplyAsync(() -> stubs[stubCounter.take()].addBlock(MatrixRequest.newBuilder()
		.setMatrixA(a1b2.getMatrix())
		.setMatrixB(b1d2.getMatrix())
		.build())).get();

		// stubCounter = stubCounter+1 > numberServer ? 0 : stubCounter+1;

		MatrixReply c3 = CompletableFuture.supplyAsync(() -> stubs[stubCounter.take()].addBlock(MatrixRequest.newBuilder()
		.setMatrixA(c1a2.getMatrix())
		.setMatrixB(d1c2.getMatrix())
		.build())).get();

		// stubCounter = stubCounter+1 > numberServer ? 0 : stubCounter+1;

		MatrixReply d3 = CompletableFuture.supplyAsync(() -> stubs[stubCounter.take()].addBlock(MatrixRequest.newBuilder()
		.setMatrixA(c1b2.getMatrix())
		.setMatrixB(d1d2.getMatrix())
		.build())).get();
		// MatrixReply multiplyMatrix = stud1.addBlock(MatrixRequest.newBuilder().setMatrixA(matrixA).setMatrixA(matrixB));

		// System.out.printn(MatrixConversion.StringToIntArray(d3.getMatrix()));

		String matrix = MatrixConversion.IntArrayToString(mergeblocks(
			MatrixConversion.StringToIntArrayFromServer(a3.getMatrix()),
			MatrixConversion.StringToIntArrayFromServer(b3.getMatrix()),
			MatrixConversion.StringToIntArrayFromServer(c3.getMatrix()),
			MatrixConversion.StringToIntArrayFromServer(d3.getMatrix()))
		);
		return MatrixConversion.prettify(matrix);
	};

	// https://qmplus.qmul.ac.uk/pluginfile.php/2561581/mod_resource/content/0/BlockMult.java
	public List<int[][]> createBlocks(int[][] A, int[][] B) {
		int MAX = A.length;
		int bSize = MAX/2;
		int[][] A1 = new int[MAX][MAX]; // 0
		int[][] A2 = new int[MAX][MAX]; // 1
		int[][] B1 = new int[MAX][MAX]; // 2
		int[][] B2 = new int[MAX][MAX]; // 3
		int[][] C1 = new int[MAX][MAX]; // 4
		int[][] C2 = new int[MAX][MAX]; // 5
		int[][] D1 = new int[MAX][MAX]; // 6
		int[][] D2 = new int[MAX][MAX]; // 7

		List<int[][]> blocks = new ArrayList<>();

		for (int i = 0; i < bSize; i++) { 
			for (int j = 0; j < bSize; j++) {
				A1[i][j]=A[i][j];
				A2[i][j]=B[i][j];
			}
		}

		for (int i = 0; i < bSize; i++) { 
			for (int j = bSize; j < MAX; j++) {
				B1[i][j-bSize]=A[i][j];
				B2[i][j-bSize]=B[i][j];
			}
		}

		for (int i = bSize; i < MAX; i++) { 
			for (int j = 0; j < bSize; j++) {
				C1[i-bSize][j]=A[i][j];
				C2[i-bSize][j]=B[i][j];
			}
		}

		for (int i = bSize; i < MAX; i++) { 
			for (int j = bSize; j < MAX; j++){
				D1[i-bSize][j-bSize]=A[i][j];
				D2[i-bSize][j-bSize]=B[i][j];
			}
		}

		blocks.add(A1);blocks.add(A2);
		blocks.add(B1);blocks.add(B2);
		blocks.add(C1);blocks.add(C2);
		blocks.add(D1);blocks.add(D2);
		return blocks;
	}

	public int[][] mergeblocks(int[][] A3, int[][] B3, int[][] C3, int[][] D3) {
		int MAX = A3.length;
		int bSize = MAX/2;
		int[][] res = new int[MAX][MAX];

		for (int i = 0; i < bSize; i++) { 
			for (int j = 0; j < bSize; j++){
				res[i][j]=A3[i][j];
			}
		}
		for (int i = 0; i < bSize; i++) { 
			for (int j = bSize; j < MAX; j++)		{
					res[i][j]=B3[i][j-bSize];
				}
			}
		for (int i = bSize; i < MAX; i++) { 
			for (int j = 0; j < bSize; j++)		{
				res[i][j]=C3[i-bSize][j];
			}
		} 
		for (int i = bSize; i < MAX; i++) { 
			for (int j = bSize; j < MAX; j++)		{
				res[i][j]=D3[i-bSize][j-bSize];
			}
		}
		return res;
	}

	public void validateMatrix(String matrix) throws Exception {
		String[] lines = matrix.split("\n");
		String[] columns = lines[0].split(" ");

		if (lines.length != columns.length) {
			throw new Exception("Matrix:" + matrix + "is invalid due to the rows and columns not being equal length");
		}
		
		if (lines.length == 0 || columns.length == 0) {
			throw new Exception("Matrix:" + matrix + "is invalid due to them needing to have rows and columns");
		}

		if (lines.length % 2 != 0 || columns.length % 2 != 0) {
			throw new Exception("Matrix:" + matrix + "is invalid due to them not being a multiple of 2");
		}

		for (int line = 0; line < lines.length; line++) {
			if (lines[line].split(" ").length != lines.length) {
				throw new Exception("Matrix:" + matrix + "is invalid due to the rows and columns not being equal length");
			}
		}
	}

    // public String ping() {
    //     	ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
    //             .usePlaintext()
    //             .build();        
		// PingPongServiceGrpc.PingPongServiceBlockingStub stub
    //             = PingPongServiceGrpc.newBlockingStub(channel);        
		// PongResponse helloResponse = stub.ping(PingRequest.newBuilder()
    //             .setPing("")
    //             .build());        
		// channel.shutdown();        
		// return helloResponse.getPong();
    // }
    // public String add(){
		// ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",9090)
		// .usePlaintext()
		// .build();
		// MatrixServiceGrpc.MatrixServiceBlockingStub stub
		//  = MatrixServiceGrpc.newBlockingStub(channel);
		// MatrixReply A=stub.addBlock(MatrixRequest.newBuilder()
		// 	.setA00(1)
		// 	.setA01(2)
		// 	.setA10(5)
		// 	.setA11(6)
		// 	.setB00(1)
		// 	.setB01(2)
		// 	.setB10(5)
		// 	.setB11(6)
		// 	.build());
		// String resp= A.getC00()+" "+A.getC01()+"<br>"+A.getC10()+" "+A.getC11()+"\n";
		// return resp;
    // }
}