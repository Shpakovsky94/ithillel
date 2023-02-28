public class StaticChecks {

    private static int main1 = 10;
    private int main2 = 20;


    public static void main(String[] args) {
        StaticTest staticTest = new StaticTest();
        System.out.println(staticTest.test1);
        System.out.println(staticTest.test2);
    }
}

class StaticTest {
    public static int test1 = 100;
    public int test2 = 200;

    private static void function() {
        System.out.println("function");
    }
}
