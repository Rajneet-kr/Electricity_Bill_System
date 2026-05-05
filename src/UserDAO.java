import java.sql.*;

public class UserDAO {

    // 🔹 REGISTER USER
	public static boolean register(User user) {

	    String checkQuery = "SELECT * FROM users WHERE username=?";
	    String insertQuery = "INSERT INTO users (username, password, role, consumer_id) VALUES (?, ?, ?, ?)";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement checkPs = con.prepareStatement(checkQuery)) {

	        // 🔍 1. Check duplicate username
	        checkPs.setString(1, user.getUsername());
	        ResultSet rs = checkPs.executeQuery();

	        if (rs.next()) {
	            System.out.println("❌ Username already exists!");
	            return false;
	        }

	        // 🔥 2. ADD THIS PART (consumer validation)
	        if (user.getRole().equals("USER")) {

	            String checkConsumer = "SELECT * FROM consumers WHERE consumer_id=?";

	            try (PreparedStatement ps = con.prepareStatement(checkConsumer)) {
	                ps.setInt(1, user.getConsumerId());
	                ResultSet crs = ps.executeQuery();

	                if (!crs.next()) {
	                    System.out.println("❌ Consumer ID does not exist!");
	                    return false;
	                }
	            }
	        }

	        // 🔥 3. Insert user
	        try (PreparedStatement insertPs = con.prepareStatement(insertQuery)) {

	            insertPs.setString(1, user.getUsername());
	            insertPs.setString(2, user.getPassword());
	            insertPs.setString(3, user.getRole());

	            if (user.getRole().equals("ADMIN")) {
	                insertPs.setNull(4, Types.INTEGER);
	            } else {
	                insertPs.setInt(4, user.getConsumerId());
	            }

	            insertPs.executeUpdate();
	            return true;
	        }

	    } catch (SQLException e) {
	        System.out.println("❌ DB Error: " + e.getMessage());
	    }

	    return false;
	}
	
	
	public static boolean isFirstLogin(String username) {

	    String query = "SELECT is_first_login FROM users WHERE username=?";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(query)) {

	        ps.setString(1, username);
	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {
	            return rs.getBoolean("is_first_login");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return false;
	}
	
	
    // 🔹 LOGIN USER
    public static String login(String username, String password) {

        String query = "SELECT role FROM users WHERE username=? AND password=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("role");
            }

        } catch (SQLException e) {
            System.out.println("❌ DB Error: " + e.getMessage());
        }

        return null;
    }

    // 🔹 GET CONSUMER ID (IMPORTANT)
    public static int getConsumerId(String username) {

        String query = "SELECT consumer_id FROM users WHERE username=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("consumer_id");
            }

        } catch (SQLException e) {
            System.out.println("❌ DB Error: " + e.getMessage());
        }

        return -1;
    }
    public static boolean changePassword(String username, String newPassword) {

        String query = "UPDATE users SET password=?, is_first_login=FALSE WHERE username=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, newPassword);
            ps.setString(2, username);

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    
}