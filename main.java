import java.time.*;
import java.util.*;

public class main {

    public static void main(String[] args){

        List<AQIRecord> data = DataFetcher.getAQIData();

        ARModel model = Trainer.train(data);

        // last date in your DB
        LocalDate lastDate = LocalDate.of(2025, 12, 31);

        // user input date
        LocalDate targetDate = LocalDate.of(2026, 3, 29);

        List<Double> values = new ArrayList<>();
        for (AQIRecord r : data){
            values.add(r.aqi);
        }

        while (lastDate.isBefore(targetDate)){

            int n = values.size();
            double[] last7 = new double[7];

            for (int i = 0; i < 7; i++){
                last7[i] = values.get(n - 1 - i);
            }

            double next = model.predict(last7);

            values.add(next);
            lastDate = lastDate.plusDays(1);
        }

        double result = values.get(values.size() - 1);

        System.out.println("AQI on " + targetDate + " = " + result);
    }
}