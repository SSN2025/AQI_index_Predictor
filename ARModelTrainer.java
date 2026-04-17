import java.util.List;

/**
 * Handles the mathematical training of the AR model.
 */
public class ARModelTrainer {
    private int lag;

    public ARModelTrainer(int lag){
        this.lag = lag;
    }

    public ARModel train(List<AQIRecord> data){
        if(data.size() < lag){
            throw new RuntimeException("Need at least " + lag + " data points");
        }

        int n = data.size();
        double[][] X = new double[n - lag][lag + 1];
        double[] Y = new double[n - lag];

        for(int i = lag; i < n; i++){
            X[i - lag][0] = 1;
            for (int j = 1; j <= lag; j++){
                X[i - lag][j] = data.get(i - j).getAqi();
            }
            Y[i - lag] = data.get(i).getAqi();
        }

        int m = X.length;
        int cols = lag + 1;

        double[][] XtX = new double[cols][cols];
        double[] XtY = new double[cols];

        for(int i = 0; i < cols; i++){
            for(int j = 0; j < cols; j++){
                for(int k = 0; k < m; k++){
                    XtX[i][j] += X[k][i] * X[k][j];
                }
            }
            for (int k = 0; k < m; k++){
                XtY[i] += X[k][i] * Y[k];
            }
        }

        double[] beta = solve(XtX, XtY);
        double c = beta[0];
        double[] phi = new double[lag];

        for(int i = 0; i < lag; i++){
            phi[i] = beta[i + 1];
        }

        return new ARModel(c, phi);
    }

    private double[] solve(double[][] A, double[] B){
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

            for(int j = i + 1; j < n; j++){
                double factor = A[j][i] / A[i][i];
                B[j] -= factor * B[i];

                for(int k = i; k < n; k++){
                    A[j][k] -= factor * A[i][k];
                }
            }
        }

        double[] x = new double[n];
        for(int i = n - 1; i >= 0; i--){
            double sum = B[i];
            for(int j = i + 1; j < n; j++){
                sum -= A[i][j] * x[j];
            }
            x[i] = sum / A[i][i];
        }

        return x;
    }
}