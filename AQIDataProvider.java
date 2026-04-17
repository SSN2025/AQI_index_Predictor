import java.util.List;

/**
 * Abstraction for data retrieval. 
 */
public interface AQIDataProvider {
    List<AQIRecord> fetchData();
}