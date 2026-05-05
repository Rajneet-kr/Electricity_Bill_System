public class Consumer {

//    private int consumerId;
    private String name;
    private String phone;
    private String fatherName;
    private String houseNo;
    private String area;
    private String city;
    private String pincode;

    public Consumer(String name, String phone, String fatherName,
                    String houseNo, String area, String city, String pincode) {

        this.name = name;
        this.phone = phone;
        this.fatherName = fatherName;
        this.houseNo = houseNo;
        this.area = area;
        this.city = city;
        this.pincode = pincode;
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getFatherName() { return fatherName; }
    public String getHouseNo() { return houseNo; }
    public String getArea() { return area; }
    public String getCity() { return city; }
    public String getPincode() { return pincode; }
}