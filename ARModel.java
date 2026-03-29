public class ARModel {

    double c;
    double[] phi; // size = 7

    public ARModel(double c, double[] phi){
        this.c = c;
        this.phi = phi;
    }

    public double predict(double[] last7){
        double result = c;

        for(int i = 0; i < 7; i++){
            result += phi[i] * last7[i];
        }

        return result;
    }
}