import java.sql.*;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/electricity_db";
    private static final String USER = "root";
    private static final String PASSWORD = "0612";

    public static Connection getConnection() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (Exception e) {
            System.out.println("❌ DB Connection Error: " + e.getMessage());
        }

        return null;
    }
}