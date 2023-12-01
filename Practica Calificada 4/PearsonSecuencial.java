package org.example;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JTextArea;



public class PearsonSecuencial {

    public static double[][] corrcoef(double[][] data) {
        int n = data.length;
        double[][] covarianceMatrix = cov(data);
        double[][] correlationMatrix = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                correlationMatrix[i][j] = covarianceMatrix[i][j] /
                        Math.sqrt(covarianceMatrix[i][i] * covarianceMatrix[j][j]);
            }
        }

        return correlationMatrix;
    }

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

    public static void impresionSecuencial(double[][] data, JTextArea TA){
        for (int i = 0; i < data.length; i++){
            for(int j = 0; j < data.length; j++){
                TA.append(String.valueOf(data[i][j]) + "  ");
            }
            TA.append("\n");
        }
    }

}
