package lesson19.oop;

public class AssociationTest {

    public static void main(String[] args) {
        User p1 = new User();
        p1.setName("John");

        User p2 = new User();
        p2.setName("Shubh");

        Address a1 = new Address();
        a1.setState("Jharkhand");
        a1.setCity("Dhanbad");
        a1.setZip("123524");

        Address a2 = new Address();
        a2.setState("Maharashtra");
        a2.setCity("Mumbai");
        a2.setZip("123635");

// Association between two classes in the main method.
        System.out.println(p1.getName() + " lives at address " + a1.getCity() + "' " + a1.getState() + ", " + a1.getZip() + " but he has also address at " + a2.getCity() + ", " + a2.getState() + ", " + a2.getZip());
        System.out.println(p2.getName() + " lives at address " + a2.getCity() + "' " + a2.getState() + ", " + a2.getZip() + " but she has also address at " + a1.getCity() + ", " + a1.getState() + ", " + a1.getZip());

    }
}

class User {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

class Address {
    private String state;
    private String city;
    private String zip;

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getZip() {
        return zip;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
