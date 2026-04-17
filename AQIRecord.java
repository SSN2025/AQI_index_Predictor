/**
 * Entity representing a single Air Quality Index (AQI) record.
 */
public class AQIRecord {
    private double aqi;

    public AQIRecord(double aqi){
        this.aqi = aqi;
    }

    public double getAqi(){
        return aqi;
    }
}