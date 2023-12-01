package org.example;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PearsonParalelo {

    public static final int NUM_THREADS = 4;
    public static double[][] correlationMatrix;

    public static void main(String[] args) throws InterruptedException {
        // Leemos nuestro dataset.csv
        String path = "archivo.csv";
        List<List<String>> data = readCSV(path);
        // Lista de listas String a una matriz double
        double[][] dataset = convertToDoubleArray(data);
        correlationMatrix = new double[dataset.length][dataset.length];

        double[][] covarianceMatrix = cov(dataset);
        calculateCorrelationMatrixInParallel(covarianceMatrix);

        // Impresion de la matriz de correlación
        for (double[] row : correlationMatrix) {
            System.out.println(Arrays.toString(row));
        }
    }

    public static void calculateCorrelationMatrixInParallel(double[][] covarianceMatrix) throws InterruptedException {
        Thread[] threads = new Thread[NUM_THREADS];
        int n = covarianceMatrix.length;
        int chunkSize = (n + NUM_THREADS - 1) / NUM_THREADS;

        for (int threadNum = 0; threadNum < NUM_THREADS; threadNum++) {
            int start = threadNum * chunkSize;
            int end = Math.min(start + chunkSize, n);

            threads[threadNum] = new Thread(() -> {
                long inicio = System.currentTimeMillis();

                for (int i = start; i < end; i++) {
                    for (int j = 0; j < n; j++) {
                        correlationMatrix[i][j] = covarianceMatrix[i][j] /
                                Math.sqrt(covarianceMatrix[i][i] * covarianceMatrix[j][j]);
                    }
                }

                long fin = System.currentTimeMillis() - inicio;
                System.out.println("Tiempo de ejecución del hilo: " + fin + " milisegundos");
            });

            threads[threadNum].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }

    // Los demás métodos (cov, convertToDoubleArray, readCSV) permanecen iguales

    public static double[][] cov(double[][] data) {
        int n = data.length;
        int m = data[0].length;
        double[][] covarianceMatrix = new double[n][n];
        double[] mean = new double[n];

        for (int i = 0; i < n; i++) {
            mean[i] = Arrays.stream(data[i]).average().orElse(Double.NaN);
        }

        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                double cov = 0.0;
                for (int k = 0; k < m; k++) {
                    cov += (data[i][k] - mean[i]) * (data[j][k] - mean[j]);
                }
                cov /= m;
                covarianceMatrix[i][j] = cov;
                covarianceMatrix[j][i] = cov;
            }
        }

        return covarianceMatrix;
    }

    public static double[][] convertToDoubleArray(List<List<String>> records) {
        int numRows = records.size();
        int numCols = records.get(0).size();
        double[][] data = new double[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            List<String> row = records.get(i);
            for (int j = 0; j < numCols; j++) {
                try {
                    data[i][j] = Double.parseDouble(row.get(j));
                } catch (NumberFormatException e) {
                    // Manejo de error si el valor no es un número válido
                    data[i][j] = Double.NaN; // Puedes optar por asignar un valor por defecto o lanzar una excepción
                }
            }
        }

        return data;
    }

    public static List<List<String>> readCSV(String path) {
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(","); // Asume que las comas son el separador
                records.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }
    public static void impresionParalelo(double[][] data, JTextArea TA){
        for (int i = 0; i < data.length; i++){
            for(int j = 0; j < data.length; j++){
                TA.append(String.valueOf(data[i][j]) + "  ");
            }
            TA.append("\n");
        }
    }

}
