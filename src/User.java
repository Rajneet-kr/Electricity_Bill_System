public class User {
    private String username;
    private String password;
    private String role;
    private int consumerId;

    public User(String username, String password, String role, int consumerId) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.consumerId = consumerId;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public int getConsumerId() {
        return consumerId;
    }
}