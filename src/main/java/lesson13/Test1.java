package lesson13;

import java.sql.SQLException;

public class Test1 {

    public static void main(String[] args) {
        System.out.println("Start");
        try {
            throwTest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("End");
    }

    public static void throwTest() throws SQLException, IllegalAccessException {
        System.out.println("Exception");
        double a = Math.random();
        if (a == 10.2) {
            throw new SQLException();
        } else {
            throw new IllegalAccessException();
        }
    }
}
