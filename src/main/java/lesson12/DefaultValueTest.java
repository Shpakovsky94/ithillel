package lesson12;

public class DefaultValueTest {

    static boolean val1;
    static double val2;
    static float val3;
    static int val4;
    static long val5;
    static String val6;
    static char val7;

    public static void main(String[] args) {
        val6 = "jojo";
        System.out.println(val1);
        System.out.println(val2);
        System.out.println(val3);
        System.out.println(val4);
        val6 = "gogo";

        System.out.println(val5);
        System.out.println(val6);
        System.out.println(val7);
        val6 = "coo";

    }

    /*
    test line1
    test line2
     */
    private void test3() {
        //test default value for boolean
        System.out.println(val1);


        //test default value for boolean
        System.out.println(val1);
    }

    /**
     * This method is used to add two integers. This is
     * a the simplest form of a class method, just to
     * show the usage of various javadoc Tags.
     *
     * @param numA This is the first paramter to addNum method
     * @param numB This is the second parameter to addNum method
     * @return int This returns sum of numA and numB.
     */
    public static int addNum(int numA, int numB) {
        return numA + numB;
    }
}
