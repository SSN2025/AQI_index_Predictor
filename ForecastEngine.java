import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ForecastEngine {
    private ARModel model;
    private List<Double> historicalValues;

    public ForecastEngine(ARModel model, List<AQIRecord> initialData){
        this.model = model;
        this.historicalValues = new ArrayList<>();
        
        for(AQIRecord r : initialData){
            this.historicalValues.add(r.getAqi());
        }
    }

    /**
     * Runs the forecast loop up to the target date.
     * @return The complete list of historical AND predicted values for graphing.
     */
    public List<Double> runForecast(LocalDate startDate, LocalDate targetDate){
        LocalDate currentDate = startDate.plusDays(1);
        int lag = model.getLag();

        while(currentDate.isBefore(targetDate) || currentDate.equals(targetDate)){
            int n = historicalValues.size();
            double[] lastN = new double[lag];

            for(int i = 0; i < lag; i++){
                lastN[i] = historicalValues.get(n - 1 - i);
            }

            double nextAqi = model.predict(lastN);
            historicalValues.add(nextAqi);
            
            currentDate = currentDate.plusDays(1);
        }
        
        return historicalValues; // Return the full dataset for the graph
    }
}