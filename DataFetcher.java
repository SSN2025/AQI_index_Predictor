import java.sql.*;
import java.util.*;

public class DataFetcher {

    public static List<AQIRecord> getAQIData(){
        List<AQIRecord> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection()){

            String query = "SELECT aqi FROM aqi_data ORDER BY date";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                list.add(new AQIRecord(rs.getDouble("aqi")));
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }
}