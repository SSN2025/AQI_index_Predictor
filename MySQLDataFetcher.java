import java.sql.*;
import java.util.*;

/**
 * Concrete implementation for fetching AQI data from a MySQL database.
 */
public class MySQLDataFetcher implements AQIDataProvider {
    
    @Override
    public List<AQIRecord> fetchData() {
        List<AQIRecord> list = new ArrayList<>();
        String query = "SELECT aqi FROM aqi_data ORDER BY date";

        try {
            Connection con = DBConnection.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {
                 
                while (rs.next()) {
                    list.add(new AQIRecord(rs.getDouble("aqi")));
                }
            }
        } catch (Exception e) {
            System.err.println("Database fetch failed: " + e.getMessage());
        }
        return list;
    }
}