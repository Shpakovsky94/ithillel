public class ComparingTest {

    public static void main(String[] args) {
        String s1 = "HELLO";
        String s2 = "HELLO";
        String s3 = new String("HELLO");

        System.out.println("s1 hash code: " + s1.hashCode());
        System.out.println("s2 hash code: " + s2.hashCode());
        System.out.println("s3 hash code: " + s3.hashCode());

        // true
        System.out.println(s1 == s2); // true
        System.out.println(s1 == s3); // false
        System.out.println(s1.equals(s2)); // true
        System.out.println(s1.equals(s3)); // true
    }
}
