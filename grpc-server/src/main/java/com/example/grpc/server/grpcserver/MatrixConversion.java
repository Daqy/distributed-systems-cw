package com.example.grpc.server.grpcserver;

public class MatrixConversion {
  public static int[][] StringToIntArray(String matrix) {
    String[] columns = matrix.split("],");
    int rowLength = matrix.split("],")[0].split(",").length;

    int[][] _matrix = new int[columns.length][rowLength];
    for (int column = 0; column < columns.length; column++) {
      String[] rows = columns[column].split(",");
      for (int row = 0; row < rows.length; row++) {
        _matrix[column][row] = Integer.parseInt(rows[row].replace("[", "").replace("]", "").replace(",", ""));
      }
    }
    
    return _matrix;
  }

  public static String IntArrayToString(int[][] matrix) {
    String concat = "[";
    for (int column = 0; column < matrix.length; column++) {
      concat += "[";
      for (int row = 0; row < matrix[column].length; row++) {
        concat += String.valueOf(matrix[column][row]) + (row==matrix[column].length-1 ? "" : ",");
      }
      concat += "]" + (column==matrix.length-1 ? "" : ",");
    }
    return concat + "]";
  }
}
