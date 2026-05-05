import java.util.Scanner;

public class MainApp {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        String role = null;
        String username = "";

        System.out.println("===== Welcome to Electricity Bill System =====");

        // 🔁 LOGIN / REGISTER LOOP
        while (true) {

            System.out.println("\n1. Admin Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            
            System.out.println("\n====================================\n");
            System.out.print("Enter option: ");

            int option = sc.nextInt();
            sc.nextLine(); // FIX

            if (option == 1) {

                // 🔹 REGISTER

                System.out.print("Username: ");
                String uname = sc.nextLine();

                System.out.print("Password: ");
                String pass = sc.nextLine();

                if (uname.trim().isEmpty() || pass.trim().isEmpty()) {
                	
                	System.out.println("\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\n");
                    System.out.println("❌ Username/Password cannot be empty!");
                    System.out.println("\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\n");
                    continue;
                }

                System.out.print("Role : ");
                String r = sc.nextLine().toUpperCase();

                if (!r.equals("ADMIN") && !r.equals("USER")) {
                    System.out.println("❌ Invalid role!");
                    continue;
                }

                int consumerId = 0;

                // ✅ Only USER enters consumer ID
                if (r.equals("USER")) {
                	
                	System.out.println("\n--------------------------------------------\n");
                    System.out.print("Enter Consumer ID: ");
                    consumerId = sc.nextInt();
                    sc.nextLine();
                }

                User u = new User(uname, pass, r, consumerId);
                boolean status = UserDAO.register(u);

                if (status) {
                	
                	System.out.println("\n--------------------------------------------\n");
                    System.out.println("✅ Registration successful!");
                    System.out.println("\n--------------------------------------------\n");
                }
                else
                {
                	System.out.println("\n--------------------------------------------\n");
                    System.out.println("❌ Registration failed!");
                    System.out.println("\n--------------------------------------------\n");
                }
            }

            else if (option == 2) {

                // 🔹 LOGIN
            	System.out.println("\n--------------------------------------------\n");
                System.out.print("Username: ");
                username = sc.nextLine();

                System.out.print("Password: ");
                
                String password = sc.nextLine();
                System.out.println("\n--------------------------------------------\n");
                
                role = UserDAO.login(username, password);

                if (role != null) {
                	
                	System.out.println("\n--------------------------------------------\n");
                    System.out.println("✅ Login successful!");
                    System.out.println("\n--------------------------------------------\n");
                    
                    
                    // 🔐 FIRST LOGIN CHECK
                    if (role.equals("USER") && UserDAO.isFirstLogin(username)) {
                    	System.out.println("\n\n--------------------------------------------\n\n");
                        System.out.println("⚠️ First Login - You must change your password!");
                        System.out.println("\n--------------------------------------------\n");

                        while (true) {
                        	
                            System.out.print("Enter New Password: ");
                            String newPass = sc.nextLine();

                            if (newPass.trim().isEmpty()) {
                                System.out.println("❌ Password cannot be empty!");
                                continue;
                            }

                            boolean updated = UserDAO.changePassword(username, newPass);

                            if (updated) {
                            	
                            	System.out.println("\n--------------------------------------------\n");
                                System.out.println("✅ Password changed successfully!");
                                
                                System.out.println("\n--------------------------------------------\n");
                                break;
                            } else {
                            	
                            	System.out.println("\n--------------------------------------------\n");
                                System.out.println("❌ Failed to update password. Try again.");
                                System.out.println("\n--------------------------------------------\n");
                            }
                        }
                    }
                    
                    
                    break;
                } else {
                	System.out.println("\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\n");
                    System.out.println("❌ Invalid credentials! Try again.");
                    System.out.println("\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\n");
                
                }
            }

            else if (option == 3) {
            	
            	System.out.println("\n--------------------------------------------\n");
                System.out.println("👋 Exiting...");
                System.out.println("\n--------------------------------------------\n");
                sc.close();
                return;
            }

            else {
            	System.out.println("\n--------------------------------------------\n");
                System.out.println("❌ Invalid option!");
                System.out.println("\n--------------------------------------------\n");
            }
        }

        // 🔐 ROLE BASED ACCESS
        if (role.equals("ADMIN")) {
            adminMenu(sc);
        } else {
            userMenu(sc, username);
        }

        sc.close();
    }

    // ================= ADMIN MENU =================
    public static void adminMenu(Scanner sc) {

        int choice;

        do {
            System.out.println("\n===== ADMIN MENU =====");
            System.out.println("1. Add Consumer");
            System.out.println("2. Generate Monthly Bill");
            System.out.println("3. View Bill History");
            System.out.println("4. Delete Consumer");
            System.out.println("5. Exit");
            System.out.println("\n--------------------------------------------\n");
            
            System.out.print("Enter your choice: ");

            choice = sc.nextInt();
            

            switch (choice) {

                case 1:
                	sc.nextLine(); // clear buffer
                	System.out.println("\n--------------------------------------------\n");
                	System.out.print("Enter Name: ");
                	String name = sc.nextLine();

                	if (name.trim().isEmpty()) {
                	    System.out.println("❌ Name cannot be empty!");
                	    break;
                	}

                	System.out.print("Phone: ");
                	String phone = sc.nextLine();
                	
                	if (phone.trim().isEmpty()) {
                	    System.out.println("❌ Phone cannot be empty!");
                	    break;
                	}

                	System.out.print("Father Name: ");
                	String father = sc.nextLine();

                	System.out.print("House No: ");
                	String house = sc.nextLine();

                	System.out.print("Area: ");
                	String area = sc.nextLine();

                	System.out.print("City: ");
                	String city = sc.nextLine();

                	System.out.print("Pincode: ");
                	String pin = sc.nextLine();
                	System.out.println("\n--------------------------------------------\n");
                	Consumer c = new Consumer(name, phone, father, house, area, city, pin);

                	int generatedId = ElectricityBillDAO.addConsumer(c);
                	
                	if (generatedId == -1) {
                		System.out.println("\n--------------------------------------------\n");
                	    System.out.println("❌ Failed to create consumer!");
                	    System.out.println("\n--------------------------------------------\n");
                	    break;
                	}
                	
                	
                	// Generate username & password
                	String uname = "user" + generatedId;
                	String password = "User@" + generatedId;

                	// 🔥 Create user in database
                	User user = new User(uname, password, "USER", generatedId);
                	boolean created = UserDAO.register(user);
                	
                	if (!created) {
                		System.out.println("\n--------------------------------------------\n");
                	    System.out.println("❌ User creation failed! Please retry.");
                	    System.out.println("\n--------------------------------------------\n");
                	    break;
                	}

                	// Print credentials
                	if (created) {
                		System.out.println("\n--------------------------------------------\n");
                	    System.out.println("🔐 Default Username: " + uname);
                	    System.out.println("🔑 Default Password: " + password);
                	    System.out.println("\n--------------------------------------------\n");
                	} else {
                		System.out.println("\n--------------------------------------------\n");
                	    System.out.println("❌ Failed to create user account!");
                	    System.out.println("\n--------------------------------------------\n");
                	}
                	
                	
                	System.out.println("\n--------------------------------------------\n");
                	System.out.println("✅ Consumer Created Successfully!\n");
                	System.out.println("👉 Generated Consumer ID: " + generatedId);
                	
                	System.out.println("\n--------------------------------------------\n");
                	
                	       
                    break;

                case 2:
               
                	ElectricityBillDAO.showAllConsumers();
                	System.out.println("\n\n--------------------------------------------\n\n");
                	System.out.print("\nSelect Consumer ID: ");
                	int cid = sc.nextInt();
                	
               
                	Consumer consumer = ElectricityBillDAO.getConsumer(cid);

                	if (consumer == null) {
                	    System.out.println("\n❌ Consumer not found!");
                	    break;
                	}

                	System.out.print("\n\nEnter Units Consumed (this month): ");
                	int units = sc.nextInt();

                	if (units < 0) {
                	    System.out.println("\n\n❌ Units cannot be negative!");
                	    break;
                	}

                	// 📅 Auto current month/year
                	java.time.LocalDate today = java.time.LocalDate.now();
                	int month = today.getMonthValue();
                	int year = today.getYear();

                	System.out.println("\n\n📅 Current Month: " + month + "/" + year);

                	// 🔥 Allow admin override (IMPORTANT FOR DEMO)
                	System.out.print("\n\nDo you want to override month? (yes/no): ");
                	String override = sc.next();

                	if (override.equalsIgnoreCase("yes")) {

                	    System.out.print("\n\nEnter Month (1-12): ");
                	    month = sc.nextInt();

                	    if (month < 1 || month > 12) {
                	        System.out.println("\n❌ Invalid month!");
                	        break;
                	    }

                	    System.out.print("\nEnter Year: ");
                	    year = sc.nextInt();

                	    if (year < 2020) {
                	        System.out.println("\n❌ Invalid year!");
                	        break;
                	    }
                	    System.out.println("\n\n--------------------------------------------\n");
                	}
                	
                	

                	ElectricityBillDAO.generateBill(cid, units, month, year); 
                	sc.nextLine();
                	break;

                case 3:
                    ElectricityBillDAO.viewBills();
                    break;
                    
                case 4:

                    ElectricityBillDAO.showAllConsumers();

                    System.out.print("\nEnter Consumer ID to delete: ");
                    int delId = sc.nextInt();
                    sc.nextLine();

                    Consumer consumerToDelete = ElectricityBillDAO.getConsumer(delId);

                    if (consumerToDelete == null) {
                        System.out.println("❌ Consumer not found!");
                        break;
                    }

                    // 🚫 CHECK UNPAID BILL FIRST
                    if (ElectricityBillDAO.hasUnpaidBillsForDelete(delId)) {
                        System.out.println("\n====================================");
                        System.out.println("❌ Cannot delete consumer!");
                        System.out.println("⚠️ Unpaid bills exist for this consumer.");
                        System.out.println("👉 Please clear dues before deletion.");
                        System.out.println("====================================");
                        break;
                    }

                    // Confirm delete
                    System.out.print("Are you sure you want to delete this consumer? (yes/no): ");
                    String confirm = sc.nextLine();

                    if (!confirm.equalsIgnoreCase("yes")) {
                        System.out.println("❌ Deletion cancelled.");
                        break;
                    }

                    // Delete user
                    ElectricityBillDAO.deleteUserByConsumerId(delId);

                    // Delete consumer
                    boolean deleted = ElectricityBillDAO.deleteConsumer(delId);

                    if (deleted) {
                        System.out.println("✅ Consumer deleted successfully!");
                    } else {
                        System.out.println("❌ Deletion failed!");
                    }

                    break;
                    
                    
                
                case 5:
                	
                	System.out.println("\n--------------------------------------------\n\n");
                    System.out.println("👋 Exiting Admin Menu...");
                    System.out.println("\n\n--------------------------------------------\n");
                    break;

                

                default:
                    System.out.println("❌ Invalid choice!");
            }

        } while (choice != 5);
    }

    // ================= USER MENU =================
    public static void userMenu(Scanner sc, String username) {

        int consumerId = UserDAO.getConsumerId(username);
        if (consumerId == -1) {
            System.out.println("\n❌ User not linked to any consumer!");
            return;
        }
        int choice;

        do {
            System.out.println("\n===== USER MENU =====");
            System.out.println("1. View Bill");
            System.out.println("2. Pay Bill");
            System.out.println("3. View Payment History");
            System.out.println("4. Update Phone Number");
            System.out.println("5. View Profile");
            System.out.println("6. Exit\n");
            System.out.println("\n--------------------------------------------\n");
            System.out.print("Enter choice: ");

            choice = sc.nextInt();
            sc.nextLine(); 

            switch (choice) {

            case 1:

                // Check unpaid bill first
                if (ElectricityBillDAO.hasUnpaidBill(consumerId)) {

                    System.out.println("⚠️ Unpaid Bill Found:");
                    ElectricityBillDAO.showUnpaidBill(consumerId);

                } else {

                    // Check if any bill exists (paid or not)
                    if (ElectricityBillDAO.hasAnyBill(consumerId)) {
                    	
                    	System.out.println("\n=================================================");
                        System.out.println("✅ Previous bill already paid.\n");
                        System.out.println("ℹ️ New bill has not been generated yet.\n");
                        System.out.println("👉 Please wait for admin to generate the next bill.");
                        System.out.println("\n=================================================");

                    } else {
                    	
                    	System.out.println("=================================================");
                        System.out.println("ℹ️ No bill found for your account.");
                        System.out.println("👉 Please contact admin.");

                    }
                }

                break;

                case 2:
                	
                	
                	// Show all unpaid bills
                	ElectricityBillDAO.showAllUnpaidBills(consumerId);
                	
                	if (!ElectricityBillDAO.hasUnpaidBill(consumerId)) {
                	    System.out.println("❌ No unpaid bills!");
                	    break;
                	}
                	
                	System.out.print("\nEnter Bill ID to pay: ");
                	int billId = sc.nextInt();
                	sc.nextLine();

                	ElectricityBillDAO.payBill(billId, consumerId);
                	
//                	if (billId == -1) {
//                	    System.out.println("\n❌ No unpaid bills!");
//                	} else {
//                		ElectricityBillDAO.payBill(billId, consumerId);
//                	}

                  
                    break;
                    
                case 3:
                    ElectricityBillDAO.showPaymentHistory(consumerId);
                    break;
                    
                    
                case 4:

                    // 🔹 Show current phone
                    String currentPhone = ElectricityBillDAO.getPhone(consumerId);

                    System.out.println("\n--------------------------------------------");
                    System.out.println("📞 Current Phone: " + currentPhone);
                    System.out.println("--------------------------------------------");

                    System.out.print("Enter new phone number: ");
                    String phone = sc.nextLine();

                    // 🔹 Validation
                    if (!phone.matches("\\d{10}")) {
                        System.out.println("❌ Enter valid 10-digit phone number!");
                        break;
                    }

                    // 🔹 Confirm before update (extra polish)
                    System.out.print("Are you sure you want to update? (yes/no): ");
                    String confirm = sc.nextLine();

                    if (!confirm.equalsIgnoreCase("yes")) {
                        System.out.println("❌ Update cancelled.");
                        break;
                    }

                    boolean updated = ElectricityBillDAO.updatePhone(consumerId, phone);

                    if (updated) {
                        System.out.println("✅ Phone number updated successfully!");
                    } else {
                        System.out.println("❌ Update failed!");
                    }

                    break;
                    
                case 5:
                    ElectricityBillDAO.showProfile(consumerId);
                    break;

                case 6:
                    System.out.println("\n👋 Exiting User Menu...");
                    break;

                default:
                    System.out.println("\n❌ Invalid choice!");
            }

        } while (choice != 6);
    }
}