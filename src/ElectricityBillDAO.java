import java.sql.*;

public class ElectricityBillDAO {

    // ➕ Add Consumer

	public static int addConsumer(Consumer c) {

	    String query = "INSERT INTO consumers(name, phone, father_name, house_no, area, city, pincode) VALUES (?, ?, ?, ?, ?, ?, ?)";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

	        ps.setString(1, c.getName());
	        ps.setString(2, c.getPhone());
	        ps.setString(3, c.getFatherName());
	        ps.setString(4, c.getHouseNo());
	        ps.setString(5, c.getArea());
	        ps.setString(6, c.getCity());
	        ps.setString(7, c.getPincode());

	        ps.executeUpdate();

	        ResultSet rs = ps.getGeneratedKeys();
	        if (rs.next()) {
	            int id = rs.getInt(1);
	            
	            System.out.println("✅ Consumer added. ID: " + id);
	            
	            return id;
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return -1;
	}
    // 🔍 Get Consumer
	public static Consumer getConsumer(int id) {

	    String query = "SELECT * FROM consumers WHERE consumer_id=?";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(query)) {

	        ps.setInt(1, id);
	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {
	            return new Consumer(
	                rs.getString("name"),
	                rs.getString("phone"),
	                rs.getString("father_name"),
	                rs.getString("house_no"),
	                rs.getString("area"),
	                rs.getString("city"),
	                rs.getString("pincode")
	            );
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return null;
	}
    
	
	public static void showAllConsumers() {

	    String query = "SELECT consumer_id, name, city FROM consumers";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(query);
	         ResultSet rs = ps.executeQuery()) {
	    	
	    	System.out.println("\n====================================\n");
	        System.out.println("\n👥 CONSUMER LIST:");
	        System.out.println("\n====================================\n");

	        while (rs.next()) {
	            System.out.println(
	                "ID: " + rs.getInt("consumer_id") +
	                " | Name: " + rs.getString("name") +
	                " | City: " + rs.getString("city")
	            );
	        }
	        
	        System.out.println("\n====================================\n");

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
		
	
	public static boolean hasAnyBill(int consumerId) {

		String query = "SELECT 1 FROM bills WHERE consumer_id=?";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(query)) {

	        ps.setInt(1, consumerId);
	        ResultSet rs = ps.executeQuery();

	        return rs.next();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return false;
	}
	
	
	//Generate Bill
	public static void generateBill(int consumerId, int units, int month, int year) {

		String check = "SELECT 1 FROM bills WHERE consumer_id=? AND billing_month=? AND billing_year=?";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(check)) {

	        ps.setInt(1, consumerId);
	        ps.setInt(2, month);
	        ps.setInt(3, year);

	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {
	        	System.out.println("\n====================================\n");
	            System.out.println("\n❌ Bill already generated for this month!\n");
	            System.out.println("\n====================================\n");
	            return;
	        }

	        double amount = calculateBill(units);

	        String insert = "INSERT INTO bills(consumer_id, units_consumed, amount, billing_month, billing_year) VALUES (?, ?, ?, ?, ?)";

	        try (PreparedStatement ps2 = con.prepareStatement(insert)) {
	            ps2.setInt(1, consumerId);
	            ps2.setInt(2, units);
	            ps2.setDouble(3, amount);
	            ps2.setInt(4, month);
	            ps2.setInt(5, year);

	            ps2.executeUpdate();
	            System.out.println("\n====================================\n\n");
	            System.out.println("💰 Bill Generated Successfully!");
	            System.out.println("📅 Month: " + month + "/" + year);
	            System.out.println("⚡ Units: " + units);
	            System.out.println("💵 Amount: ₹" + amount);
	            System.out.println("\n\n====================================\n");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public static void showAllUnpaidBills(int consumerId) {

	    String query = "SELECT bill_id, billing_month, billing_year, amount FROM bills WHERE consumer_id=? AND status='UNPAID'";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(query)) {

	        ps.setInt(1, consumerId);
	        ResultSet rs = ps.executeQuery();

	        boolean found = false;

	        while (rs.next()) {
	            found = true;
	            System.out.println("\n====================================\n\n");
	            System.out.println(
	                "Bill ID: " + rs.getInt("bill_id") +
	                " | Month: " + rs.getInt("billing_month") + "/" + rs.getInt("billing_year") +
	                " | Amount: ₹" + rs.getDouble("amount")
	            );
	            System.out.println("\n\n====================================\n");
	        }

//	        if (!found) {
//	        	System.out.println("\n====================================\n\n");
//	            System.out.println("❌ No unpaid bills!");
//	            System.out.println("\n\n====================================\n");
//	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public static void viewBills() {

	    String query = "SELECT b.*, c.name FROM bills b " +
	                   "JOIN consumers c ON b.consumer_id = c.consumer_id " +
	                   "ORDER BY c.name, b.billing_year, b.billing_month";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(query);
	         ResultSet rs = ps.executeQuery()) {

	        System.out.println("\n====================================\n");
	        System.out.println("📄 BILL RECORDS (USER-WISE):");
	        System.out.println("\n====================================\n");

	        if (!rs.isBeforeFirst()) {
	            System.out.println("❌ No bills available.");
	            return;
	        }

	        String currentUser = "";

	        while (rs.next()) {

	            String name = rs.getString("name");

	            // 🔹 Print header when user changes
	            if (!name.equals(currentUser)) {
	                currentUser = name;

	                System.out.println("\n👤 Consumer: " + name);
	                System.out.println("-------------------------------------------------------------");
	                System.out.printf("%-8s %-8s %-10s %-10s %-10s\n",
	                        "BillID", "Units", "Month", "Amount", "Status");
	                System.out.println("-------------------------------------------------------------");
	            }

	            // 🔹 Print row
	            System.out.printf("%-8d %-8d %-10s %-10.2f %-10s\n",
	                    rs.getInt("bill_id"),
	                    rs.getInt("units_consumed"),
	                    rs.getInt("billing_month") + "/" + rs.getInt("billing_year"),
	                    rs.getDouble("amount"),
	                    rs.getString("status"));
	        }

	        System.out.println("\n====================================\n");

	    } catch (SQLException e) {
	        System.out.println("❌ Error: " + e.getMessage());
	    }
	}
	
	// 👤 Show Full Consumer Profile
	public static void showProfile(int consumerId) {

	    String query = "SELECT * FROM consumers WHERE consumer_id=?";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(query)) {

	        ps.setInt(1, consumerId);
	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {

	            System.out.println("\n====================================");
	            System.out.println("👤 CONSUMER PROFILE");
	            System.out.println("====================================");

	            System.out.println("Name        : " + rs.getString("name"));
	            System.out.println("Phone       : " + rs.getString("phone"));
	            System.out.println("Father Name : " + rs.getString("father_name"));
	            System.out.println("House No    : " + rs.getString("house_no"));
	            System.out.println("Area        : " + rs.getString("area"));
	            System.out.println("City        : " + rs.getString("city"));
	            System.out.println("Pincode     : " + rs.getString("pincode"));

	            System.out.println("====================================\n");

	        } else {
	            System.out.println("❌ Profile not found!");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	// 🗑️ Delete Consumer
	public static boolean deleteConsumer(int consumerId) {

	    String query = "DELETE FROM consumers WHERE consumer_id=?";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(query)) {

	        ps.setInt(1, consumerId);

	        int rows = ps.executeUpdate();

	        return rows > 0;

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return false;
	}
	
	// 🗑️ Delete user linked to consumer
	public static void deleteUserByConsumerId(int consumerId) {

	    String query = "DELETE FROM users WHERE consumer_id=?";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(query)) {

	        ps.setInt(1, consumerId);
	        ps.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	// 🚫 Check if consumer has unpaid bills
	public static boolean hasUnpaidBillsForDelete(int consumerId) {

	    String query = "SELECT 1 FROM bills WHERE consumer_id=? AND status='UNPAID'";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(query)) {

	        ps.setInt(1, consumerId);
	        ResultSet rs = ps.executeQuery();

	        return rs.next();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return false;
	}
	
	// 📞 Get current phone number
	public static String getPhone(int consumerId) {

	    String query = "SELECT phone FROM consumers WHERE consumer_id=?";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(query)) {

	        ps.setInt(1, consumerId);
	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {
	            return rs.getString("phone");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return null;
	}
	
	// 📱 Update Phone Number Only
	public static boolean updatePhone(int consumerId, String phone) {

	    String query = "UPDATE consumers SET phone=? WHERE consumer_id=?";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(query)) {

	        ps.setString(1, phone);
	        ps.setInt(2, consumerId);

	        return ps.executeUpdate() > 0;

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return false;
	}
	
	
	
	
	public static int getUnpaidBillId(int consumerId) {

	    String query = "SELECT bill_id FROM bills WHERE consumer_id=? AND status='UNPAID' LIMIT 1";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(query)) {

	        ps.setInt(1, consumerId);
	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {
	            return rs.getInt("bill_id");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return -1;
	}
	
	public static boolean hasUnpaidBill(int consumerId) {

		String query = "SELECT 1 FROM bills WHERE consumer_id=? AND status='UNPAID'";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(query)) {

	        ps.setInt(1, consumerId);
	        ResultSet rs = ps.executeQuery();

	        return rs.next();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return false;
	}
	
	
	public static void showUnpaidBill(int consumerId) {

	    String query = "SELECT * FROM bills WHERE consumer_id=? AND status='UNPAID'";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(query)) {

	        ps.setInt(1, consumerId);
	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {
	        	System.out.println("\n====================================\n\n");
	        	System.out.println("🧾 Bill ID: " + rs.getInt("bill_id"));
	        	System.out.println("📅 Month: " + rs.getInt("billing_month") + "/" + rs.getInt("billing_year"));
	        	System.out.println("⚡ Units: " + rs.getInt("units_consumed"));
	        	System.out.println("💰 Amount: ₹" + rs.getDouble("amount"));
	        	
	        	System.out.println("\n\n====================================\n");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public static void payBill(int billId, int consumerId) {

		String query = "SELECT amount FROM bills WHERE bill_id=? AND consumer_id=? AND status='UNPAID'";

	    try (Connection con = DBConnection.getConnection()) {

	    if (con == null) {
	        System.out.println("❌ Database connection failed!");
	        return;
	    }
	    
	    try (PreparedStatement ps = con.prepareStatement(query)) {

	        ps.setInt(1, billId);
	        ps.setInt(2, consumerId);
	        ResultSet rs = ps.executeQuery();

	        if (!rs.next()) {
	        	
	        	System.out.println("\n====================================\n\n");
	            System.out.println("❌ Invalid or already paid!");
	            System.out.println("\n\n====================================\n");
	            return;
	        }

	        double amount = rs.getDouble("amount");

	        // insert payment
	        String payment = "INSERT INTO payments(bill_id, amount, payment_method, status) VALUES (?, ?, 'ONLINE', 'SUCCESS')";

	        try (PreparedStatement ps2 = con.prepareStatement(payment)) {
	            ps2.setInt(1, billId);
	            ps2.setDouble(2, amount);
	            ps2.executeUpdate();
	        }

	        // update bill
	        String update = "UPDATE bills SET status='PAID' WHERE bill_id=?";

	        try (PreparedStatement ps3 = con.prepareStatement(update)) {
	            ps3.setInt(1, billId);
	            ps3.executeUpdate();
	        }
	        System.out.println("\n====================================\n");
	        System.out.println("\n✅ Payment Successful!");
	        System.out.println("\n====================================\n");
	    }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	
	
    // 💡 Bill Calculation Logic
    public static double calculateBill(int units) {

        double bill;

        if (units <= 100) {
            bill = units * 1.5;
        } else if (units <= 300) {
            bill = (100 * 1.5) + (units - 100) * 2.5;
        } else {
            bill = (100 * 1.5) + (200 * 2.5) + (units - 300) * 4;
        }

        return bill;
    }
    
 // 📊 Show Payment History
    public static void showPaymentHistory(int consumerId) {

        String query = "SELECT b.billing_month, b.billing_year, p.amount, p.payment_date " +
                       "FROM payments p JOIN bills b ON p.bill_id = b.bill_id " +
                       "WHERE b.consumer_id=? ORDER BY p.payment_date DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, consumerId);
            ResultSet rs = ps.executeQuery();

            boolean found = false;
            System.out.println("\n====================================\n\n");

            System.out.println("\n📊 PAYMENT HISTORY:");
            System.out.println("\n\n====================================\n");

            while (rs.next()) {
                found = true;

                System.out.println(
                    "📅 " + rs.getInt("billing_month") + "/" + rs.getInt("billing_year") +
                    " | 💰 ₹" + rs.getDouble("amount") +
                    " | 🕒 " + rs.getTimestamp("payment_date")
                );
                System.out.println("\n\n====================================\n\n");
            }
            
            if (!found) {
            	System.out.println("\n====================================\n\n");
                System.out.println("❌ No payment history found.");
                System.out.println("\n\n====================================\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}