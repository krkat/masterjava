package ru.javaops.masterjava.matrix;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

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
        int[] thatColumn = new int[bRows];
        /*for (int j = 0; j < bColumns; j++) {
            for (int k = 0; k < aColumns; k++) {
                thatColumn[k] = matrixB[k][j];
            }
            for (int i = 0; i < aRows; i++) {
                final int innerI = i;
                Future<Integer> future = executor.submit(() -> {
                    int summand = 0;
                    int[] thisRow = matrixA[innerI];
                    for (int k = 0; k < aColumns; k++) {
                        summand += thisRow[k] + thatColumn[k];
                    }
                    return summand;
                });
                matrixC[i][j] = future.get();
            }
        }*/
        //Map<j, Future<i, Cij>>
       // Map<Integer, Future<Map<Integer, Integer>>> futureRows = new HashMap<>();
        for (int j = 0; j < bColumns; j++) {
            final int innerJ = j;
            Future<Map<Integer, Integer>> future = executor
                    .submit(() -> {
                        Map<Integer, Integer> result = new HashMap<>();
                        for (int m = 0; m < aColumns; m++) {
                            //column from B to array
                            thatColumn[m] = matrixB[m][innerJ];
                        }
                        //for each row from А
                        for (int i = 0; i < aRows; i++) {
                            int summand = 0;
                            //row from А to array
                            int[] thisRow = matrixA[i];
                            //count Сij
                            for (int k = 0; k < aColumns; k++) {
                                summand += thisRow[k] * thatColumn[k];
                            }
                            result.put(i,summand);
                        }
                        //j column for C
                        return result;
                    });
            Map<Integer, Integer> columnJ = future.get();
            for (int i = 0; i < aRows; i++) {
                matrixC[i][j] = columnJ.get(i);
            }
        }
        //Достаем результаты и записываем в матрицу С
        /*for (int j = 0; j < aRows; j++) {
            Future<Map<Integer, Integer>> futureColumnJ = futureRows.get(j);
            Map<Integer, Integer> columnJ = futureColumnJ.get();
            for (int i = 0; i < aRows; i++) {
                matrixC[i][j] = columnJ.get(i);
            }
        }*/
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
