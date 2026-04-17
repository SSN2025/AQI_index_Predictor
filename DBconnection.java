import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages database connections using the Singleton pattern.
 */
public class DBConnection {
    private static DBConnection instance;
    private Connection connection;
    
    private static final String URL = "jdbc:mysql://localhost:3306/aqi_project";
    private static final String USER = "root";
    private static final String PASSWORD = "have256-@17";

    private DBConnection() throws SQLException, ClassNotFoundException{
        Class.forName("com.mysql.cj.jdbc.Driver");
        this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static DBConnection getInstance() throws SQLException, ClassNotFoundException{
        if(instance == null || instance.getConnection().isClosed()){
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection(){
        return connection;
    }
}