/**
 * Represents the trained Autoregressive (AR) forecasting model.
 */
public class ARModel {
    private double c;
    private double[] phi; 

    public ARModel(double c, double[] phi){
        this.c = c;
        this.phi = phi;
    }

    public double predict(double[] lastValues){
        if (lastValues.length != phi.length){
            throw new IllegalArgumentException("Input array must match the model's lag parameter.");
        }
        
        double result = c;
        for (int i = 0; i < phi.length; i++){
            result += phi[i] * lastValues[i];
        }
        return result;
    }
    
    public int getLag(){
        return phi.length;
    }
}