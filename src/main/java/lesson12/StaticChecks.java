package lesson12;

public class StaticChecks {

    private static int main1 = 10;
    private int main2 = 20;


    public static void main(String[] args) {

        StaticTest object = new StaticTest();


        System.out.println(StaticTest.test1);
//        System.out.println(object.test1);
//        System.out.println(object.test2);
    }
}

class StaticTest {
    public static int test1 = 100;
    public int test2 = 200;

    public static void function() {
        System.out.println("function");
    }
}
