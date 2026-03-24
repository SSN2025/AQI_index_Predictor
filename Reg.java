import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.lang.Math;


class Matrix {
    private int rows;
    private int cols;
    private double[][] a;

    // Constructor: creates a rows x cols zero matrix
    public Matrix(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Dimensions must be positive");
        }
        this.rows = rows;
        this.cols = cols;
        this.a = new double[rows][cols];
    }

    // Constructor: creates matrix from 2D array
    public Matrix(double[][] data) {
        if (data == null || data.length == 0 || data[0].length == 0) {
            throw new IllegalArgumentException("Invalid data for matrix");
        }

        this.rows = data.length;
        this.cols = data[0].length;
        this.a = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            if (data[i].length != cols) {
                throw new IllegalArgumentException("All rows must have the same length");
            }
            for (int j = 0; j < cols; j++) {
                this.a[i][j] = data[i][j];
            }
        }
    }

    // Set value at position (i, j)
    public void set(int i, int j, double value) {
        a[i][j] = value;
    }

    // Get value at position (i, j)
    public double get(int i, int j) {
        return a[i][j];
    }

    // Print matrix
    public void printMatrix() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.printf("%10.4f ", a[i][j]);
            }
            System.out.println();
        }
    }

    // Return transpose of given matrix
    public static Matrix transpose(Matrix m) {
        // Swap rows and cols for the new transposed matrix
        Matrix result = new Matrix(m.cols, m.rows);

        for (int i = 0; i < m.rows; i++) {
            for (int j = 0; j < m.cols; j++) {
                result.a[j][i] = m.a[i][j];
            }
        }

        return result;
    }

    // Multiply two matrices
    public static Matrix multiply(Matrix m1, Matrix m2) {
        if (m1.cols != m2.rows) {
            throw new IllegalArgumentException("Inner dimensions must match for multiplication (m1.cols == m2.rows)");
        }

        Matrix result = new Matrix(m1.rows, m2.cols);

        for (int i = 0; i < m1.rows; i++) {
            for (int j = 0; j < m2.cols; j++) {
                double sum = 0;
                for (int k = 0; k < m1.cols; k++) {
                    sum += m1.a[i][k] * m2.a[k][j];
                }
                result.a[i][j] = sum;
            }
        }

        return result;
    }

    // Return inverse of given matrix using Gauss-Jordan elimination
    public static Matrix inverse(Matrix m) {
        if (m.rows != m.cols) {
            throw new IllegalArgumentException("Inverse is only defined for square matrices");
        }

        int n = m.rows;
        double[][] aug = new double[n][2 * n];

        // Create augmented matrix [A | I]
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                aug[i][j] = m.a[i][j];
            }
            for (int j = n; j < 2 * n; j++) {
                aug[i][j] = (i == (j - n)) ? 1 : 0;
            }
        }

        // Gauss-Jordan elimination
        for (int i = 0; i < n; i++) {
            int pivotRow = i;
            for (int r = i + 1; r < n; r++) {
                if (Math.abs(aug[r][i]) > Math.abs(aug[pivotRow][i])) {
                    pivotRow = r;
                }
            }

            // Swap rows
            if (pivotRow != i) {
                double[] temp = aug[i];
                aug[i] = aug[pivotRow];
                aug[pivotRow] = temp;
            }

            // Check singular
            if (Math.abs(aug[i][i]) < 1e-10) {
                throw new ArithmeticException("Matrix is singular, inverse does not exist");
            }

            // Make pivot = 1
            double pivot = aug[i][i];
            for (int j = 0; j < 2 * n; j++) {
                aug[i][j] /= pivot;
            }

            // Make remaining entries in column = 0
            for (int r = 0; r < n; r++) {
                if (r != i) {
                    double factor = aug[r][i];
                    for (int j = 0; j < 2 * n; j++) {
                        aug[r][j] -= factor * aug[i][j];
                    }
                }
            }
        }

        // Extract inverse
        Matrix inv = new Matrix(n, n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                inv.a[i][j] = aug[i][j + n];
            }
        }

        return inv;
    }
}

public class Reg {
    public static double getAQI(int day_no) {
        String csvFile = "C:\\Users\\lenovo\\OneDrive\\Desktop\\Project\\Trial.csv";
        Scanner sc = null;
        Matrix a = new Matrix((365*2),4);
        Matrix b = new Matrix((365*2),1);
        try{
             sc = new Scanner(new File(csvFile));
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Scanner making failed");
        }
        int i=0,j=0;
        while(sc.hasNextLine()){
            String[] x = (sc.nextLine()).split(",");
            a.set(j,0,1);
            a.set(j,1,Integer.parseInt(x[0]));
            a.set(j,2,Math.sin(  (2*Math.PI*Integer.parseInt(x[1])) / 365.0));
            a.set(j,3,Math.cos(  (2*Math.PI*Integer.parseInt(x[1])) / 365.0));
            b.set(j,0,Integer.parseInt(x[2]));
            j++;
        }
        //a.printMatrix();
        System.out.println();
        //b.printMatrix();
        Matrix theta = Matrix.multiply(Matrix.inverse(Matrix.multiply(Matrix.transpose(a),a)),Matrix.multiply(Matrix.transpose(a),b)); 
        System.out.println();
        System.out.println();
        int year =26;
        return (theta.get(0,0) + theta.get(1,0) * year + theta.get(2,0) * Math.sin(  (2*Math.PI*day_no) / 365.0) + theta.get(3,0) * Math.cos(  (2*Math.PI*day_no) / 365.0));
        }
}