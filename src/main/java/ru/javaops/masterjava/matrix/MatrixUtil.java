package ru.javaops.masterjava.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int aColumns = matrixA.length;
        final int aRows = matrixA[0].length;
        final int bColumns = matrixB.length;
        final int bRows = matrixB[0].length;
        final int[][] matrixC = new int[aColumns][aRows];

        class Column {
            private final int[] column;
            private final int columnNumber;

            public Column(int[] column, int columnNumber) {
                this.column = column;
                this.columnNumber = columnNumber;
            }

            public int getColumnNumber() {
                return columnNumber;
            }

            public int[] getColumn() {
                return column;
            }
        }
        List<Future<Column>> futures = new ArrayList<>();
        final CompletionService<Column> completionExecutor = new ExecutorCompletionService<>(executor);

        for (int j = 0; j < bColumns; j++) {
            final int innerJ = j;
            Future<Column> future = completionExecutor
                    .submit(() -> {
                        int[] thatColumn = new int[bRows];
                        for (int m = 0; m < bRows; m++) {
                            //column from B to array
                            thatColumn[m] = matrixB[m][innerJ];
                        }
                        //for each row from А
                        final int[] result = new int[aRows];
                        for (int i = 0; i < aRows; i++) {
                            int summand = 0;
                            for (int k = 0; k < aColumns; k++) {
                                summand += matrixA[i][k] * thatColumn[k];
                            }
                            result[i] = summand;
                        }
                        return new Column(result, innerJ);
                    });
           futures.add(future);
        }
        while (!futures.isEmpty()) {
            Future<Column> future = completionExecutor.poll(1, TimeUnit.SECONDS);
            if (future == null) {
                System.exit(1);
            }
            futures.remove(future);
            Column columnObject = future.get();
            int columnNumber = columnObject.getColumnNumber();
            int[] column = columnObject.getColumn();
            for (int i = 0; i < column.length; i++) {
                matrixC[i][columnNumber] = column[i];
            }
        }
        return matrixC;
    }

    // optimized by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int aColumns = matrixA.length;
        final int aRows = matrixA[0].length;
        final int bColumns = matrixB.length;
        final int bRows = matrixB[0].length;
        final int[][] matrixC = new int[aColumns][aRows];

        int[] thatColumn = new int[bRows];
        try {
            for (int j = 0; j < bColumns; j++) {
                for (int k = 0; k < aColumns; k++) {
                    thatColumn[k] = matrixB[k][j];
                }
                for (int i = 0; i < aRows; i++) {
                    int[] thisRow = matrixA[i];
                    int summand = 0;
                    for (int k = 0; k < aColumns; k++) {
                        summand += thisRow[k] * thatColumn[k];
                    }
                    matrixC[i][j] = summand;
                }
            }
        } catch (IndexOutOfBoundsException ignored) {
        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
