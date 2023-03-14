package lesson13;

public class Test2 {

    public final static int a = 10;

    public static void main(String[] args) {
        System.out.println(1);

        try {
            System.out.println(2);

            int i = Integer.parseInt("ABC");

            System.out.println(3);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(4);

            int a = 10;

            String result = String.valueOf(a);

//            new StringBuilder()
        } finally {
            System.out.println(5);
        }


    }
}
