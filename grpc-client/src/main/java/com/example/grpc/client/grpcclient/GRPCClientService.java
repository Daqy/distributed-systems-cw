package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.PingRequest;
import com.example.grpc.server.grpcserver.PongResponse;
import com.example.grpc.server.grpcserver.PingPongServiceGrpc;
import com.example.grpc.server.grpcserver.MatrixRequest;

import java.util.ArrayList;
import java.util.List;

import com.example.grpc.server.grpcserver.MatrixReply;
import com.example.grpc.server.grpcserver.MatrixServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

@Service
public class GRPCClientService {
	public String multiplyFiles(String matrixAContent, String matrixBContent) throws Exception {
		validateMatrix(matrixAContent);
		validateMatrix(matrixBContent);
		int[][] matrixA = MatrixConversion.StringToIntArray(matrixAContent);
		int[][] matrixB = MatrixConversion.StringToIntArray(matrixBContent);

		List<int[][]> blocks = createBlocks(matrixA, matrixB);

		ManagedChannel channel1 = ManagedChannelBuilder.forAddress("localhost", 8085).usePlaintext().build();
		MatrixServiceGrpc.MatrixServiceBlockingStub stub1 = MatrixServiceGrpc.newBlockingStub(channel1);
		
		ManagedChannel channel2 = ManagedChannelBuilder.forAddress("localhost", 8082).usePlaintext().build();
		MatrixServiceGrpc.MatrixServiceBlockingStub stub2 = MatrixServiceGrpc.newBlockingStub(channel2);
		
		MatrixReply a1a2 = stub2.multiplyBlock(MatrixRequest.newBuilder()
		.setMatrixA(MatrixConversion.IntArrayToString(blocks.get(0)))
		.setMatrixB(MatrixConversion.IntArrayToString(blocks.get(1)))
		.build());

		MatrixReply b1c2 = stub1.multiplyBlock(MatrixRequest.newBuilder()
		.setMatrixA(MatrixConversion.IntArrayToString(blocks.get(2)))
		.setMatrixB(MatrixConversion.IntArrayToString(blocks.get(5)))
		.build());

		MatrixReply a1b2 = stub1.multiplyBlock(MatrixRequest.newBuilder()
		.setMatrixA(MatrixConversion.IntArrayToString(blocks.get(0)))
		.setMatrixB(MatrixConversion.IntArrayToString(blocks.get(3)))
		.build());

		MatrixReply b1d2 = stub1.multiplyBlock(MatrixRequest.newBuilder()
		.setMatrixA(MatrixConversion.IntArrayToString(blocks.get(2)))
		.setMatrixB(MatrixConversion.IntArrayToString(blocks.get(7)))
		.build());

		MatrixReply c1a2 = stub1.multiplyBlock(MatrixRequest.newBuilder()
		.setMatrixA(MatrixConversion.IntArrayToString(blocks.get(4)))
		.setMatrixB(MatrixConversion.IntArrayToString(blocks.get(1)))
		.build());

		MatrixReply d1c2 = stub1.multiplyBlock(MatrixRequest.newBuilder()
		.setMatrixA(MatrixConversion.IntArrayToString(blocks.get(6)))
		.setMatrixB(MatrixConversion.IntArrayToString(blocks.get(5)))
		.build());

		MatrixReply c1b2 = stub1.multiplyBlock(MatrixRequest.newBuilder()
		.setMatrixA(MatrixConversion.IntArrayToString(blocks.get(4)))
		.setMatrixB(MatrixConversion.IntArrayToString(blocks.get(3)))
		.build());

		MatrixReply d1d2 = stub1.multiplyBlock(MatrixRequest.newBuilder()
		.setMatrixA(MatrixConversion.IntArrayToString(blocks.get(6)))
		.setMatrixB(MatrixConversion.IntArrayToString(blocks.get(7)))
		.build());

		MatrixReply a3 = stub1.addBlock(MatrixRequest.newBuilder()
		.setMatrixA(a1a2.getMatrix())
		.setMatrixB(b1c2.getMatrix())
		.build());

		MatrixReply b3 = stub1.addBlock(MatrixRequest.newBuilder()
		.setMatrixA(a1b2.getMatrix())
		.setMatrixB(b1d2.getMatrix())
		.build());

		MatrixReply c3 = stub1.addBlock(MatrixRequest.newBuilder()
		.setMatrixA(c1a2.getMatrix())
		.setMatrixB(d1c2.getMatrix())
		.build());

		MatrixReply d3 = stub1.addBlock(MatrixRequest.newBuilder()
		.setMatrixA(c1b2.getMatrix())
		.setMatrixB(d1d2.getMatrix())
		.build());
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