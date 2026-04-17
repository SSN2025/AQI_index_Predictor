// import java.time.LocalDate;
// import java.util.List;

// /**
//  * The main driver class for the AQI prediction application.
//  */
// public class Main {
//     public static void main(String[] args) {
//         // 1. Data Retrieval (Polymorphism)
//         AQIDataProvider dataProvider = new MySQLDataFetcher();
//         List<AQIRecord> data = dataProvider.fetchData();

//         if (data.isEmpty()) {
//             System.out.println("Error: No data retrieved. Please check your database connection.");
//             return;
//         }

//         // 2. Train the Model (Encapsulation of state)
//         int lag = 7; 
//         ARModelTrainer trainer = new ARModelTrainer(lag);
//         ARModel trainedModel = trainer.train(data);

//         // 3. Setup Forecasting Engine
//         ForecastEngine engine = new ForecastEngine(trainedModel, data);

//         // 4. Define Dates
//         LocalDate dbEndDate = LocalDate.of(2025, 12, 31);
//         LocalDate targetDate = LocalDate.of(2026, 3, 29);
        
//         // 5. Execute Forecast
//         System.out.println("Running forecast engine...");
//         double finalPrediction = engine.runForecast(dbEndDate, targetDate);
        
//         // 6. Print Results
//         System.out.println("\n--- FORECAST RESULT ---");
//         System.out.printf("Target Date: %s%n", targetDate);
//         System.out.printf("Predicted AQI: %.2f%n", finalPrediction);
//         System.out.println("-----------------------\n");
//     }
// }