package lesson19.oop;

public class AssociationOneToOneTest {
    public static void main(String[] args) {
        Human p1 = new Human();
        p1.setName("John");

        Human p2 = new Human();
        p2.setName("Shubh");

        Passport pp1 = new Passport();
        pp1.setPassportNo(1234567);

        Passport pp2 = new Passport();
        pp2.setPassportNo(12398576);

// Association between two classes in the main method.
        System.out.println(p1.getName() + " has a US passport whose passport no is: " + pp1.getPassportNo());
        System.out.println(p2.getName() + " has an Indian passport whose passport no is: " + pp2.getPassportNo());
    }
}

class Human {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

class Passport {
    private int passportNo;

    public int getPassportNo() {
        return passportNo;
    }

    public void setPassportNo(int passportNo) {
        this.passportNo = passportNo;
    }
}
