package com.example.grpc.server.grpcserver;


import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
@GrpcService
public class MatrixServiceImpl extends MatrixServiceGrpc.MatrixServiceImplBase
{
	int _currentThread;

	public MatrixServiceImpl(int currentThread) {
		this._currentThread = currentThread;
	}

	@Override
	public void addBlock(MatrixRequest request, StreamObserver<MatrixReply> reply)
	{
		System.out.printf("Rquest recieved from client:\n %s\n\n`addBlock` fucntion called on server: %d\n", request, _currentThread);

		int[][] matrixA = MatrixConversion.StringToIntArray(request.getMatrixA());
		int[][] matrixB = MatrixConversion.StringToIntArray(request.getMatrixB());

		int sizeOfMatrix = matrixA.length;
		int[][] _matrix = new int[sizeOfMatrix][sizeOfMatrix];

		for (int row = 0; row < sizeOfMatrix; row++) {
			for (int column = 0; column < sizeOfMatrix; column++) {
				_matrix[row][column] = matrixA[row][column] + matrixB[row][column];
			}
		}

		String responseMatrix = MatrixConversion.IntArrayToString(_matrix);
		MatrixReply response = MatrixReply.newBuilder().setMatrix(responseMatrix).build();

		reply.onNext(response);
		reply.onCompleted();
	}
	
	@Override
    	public void multiplyBlock(MatrixRequest request, StreamObserver<MatrixReply> reply)
    	{
				System.out.printf("Rquest recieved from client:\n %s\n\n`multiplyBlock` fucntion called on server: %d\n", request, _currentThread);

				int[][] matrixA = MatrixConversion.StringToIntArray(request.getMatrixA());
				int[][] matrixB = MatrixConversion.StringToIntArray(request.getMatrixB());

				int sizeOfMatrix = matrixA.length;
				int[][] _matrix = new int[sizeOfMatrix][sizeOfMatrix];

				for (int row = 0; row < sizeOfMatrix/2; row++) {
					for (int column = 0; column < sizeOfMatrix/2; column++) {
						for (int MatrixBrow = 0; MatrixBrow < sizeOfMatrix/2; MatrixBrow++) {
							_matrix[row][column] += matrixA[row][MatrixBrow] + matrixB[MatrixBrow][column];
						}
					}
				}

				String responseMatrix = MatrixConversion.IntArrayToString(_matrix);
				MatrixReply response = MatrixReply.newBuilder().setMatrix(responseMatrix).build();

				reply.onNext(response);
				reply.onCompleted();
    }
}
