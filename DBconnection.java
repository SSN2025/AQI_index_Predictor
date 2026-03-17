import java.sql.Connection;
import java.sql.DriverManager;

public class DBconnection{
    //don't forget to type your password and database name
    private static final String url = "jdbc:mysql://localhost:3306/database_name";
    private static final String user = "root";
    private static final String password = "password";
    public static void main(String[] args){
        try{
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected");
            conn.close();
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}