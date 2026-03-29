import java.util.*;

public class Trainer {

    public static ARModel train(List<AQIRecord> data) {

        if (data.size() < 7){
            throw new RuntimeException("Need at least 7 data points");
        }

        int n = data.size();
        int p = 7;

        double[][] X = new double[n - p][p + 1];
        double[] Y = new double[n - p];

        for(int i = p; i < n; i++){
            X[i - p][0] = 1;

            for(int j = 1; j <= p; j++){
                X[i - p][j] = data.get(i - j).aqi;
            }

            Y[i - p] = data.get(i).aqi;
        }

        int m = X.length;
        int cols = p + 1;

        double[][] XtX = new double[cols][cols];
        double[] XtY = new double[cols];

        for(int i = 0; i < cols; i++) {
            for(int j = 0; j < cols; j++){
                for (int k = 0; k < m; k++){
                    XtX[i][j] += X[k][i] * X[k][j];
                }
            }

            for(int k = 0; k < m; k++){
                XtY[i] += X[k][i] * Y[k];
            }
        }

        double[] beta = solve(XtX, XtY);

        double c = beta[0];
        double[] phi = new double[p];

        for(int i = 0; i < p; i++){
            phi[i] = beta[i + 1];
        }

        return new ARModel(c, phi);
    }

    private static double[] solve(double[][] A, double[] B){
        int n = B.length;

        for(int i = 0; i < n; i++){

            int max = i;
            for(int j = i + 1; j < n; j++){
                if (Math.abs(A[j][i]) > Math.abs(A[max][i])){
                    max = j;
                }
            }

            double[] temp = A[i]; A[i] = A[max]; A[max] = temp;
            double t = B[i]; B[i] = B[max]; B[max] = t;

            for (int j = i + 1; j < n; j++){
                double factor = A[j][i] / A[i][i];
                B[j] -= factor * B[i];

                for (int k = i; k < n; k++) {
                    A[j][k] -= factor * A[i][k];
                }
            }
        }

        double[] x = new double[n];

        for(int i = n - 1; i >= 0; i--){
            double sum = B[i];
            for (int j = i + 1; j < n; j++) {
                sum -= A[i][j] * x[j];
            }
            x[i] = sum / A[i][i];
        }

        return x;
    }
}