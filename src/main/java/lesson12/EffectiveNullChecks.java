package lesson12;

public class EffectiveNullChecks {

    public static void main(String[] args) {

//
//        System.out.println(stringReverse(null));
//        System.out.println(stringReverse("hello"));

        try {
            System.out.println(reverseString(null));
            System.out.println(reverseString("hello"));
        } catch (IllegalArgumentException e) {
            System.err.print("Processing fail. Incorrect input" + e);
        } catch (Exception e) {
            System.err.print("Processing fail." + e.getMessage());
        }
    }

    private static String reverseString(String param) {
        if (param == null || param.isEmpty()) {
            throw new IllegalArgumentException();
        }
        StringBuilder stringBuilder = new StringBuilder(param);
        return stringBuilder.reverse().toString();
    }

    private static String stringReverse(String line) {
        String reverseLine = "";
        if (line != null) {
            StringBuilder builder = new StringBuilder(line);
            reverseLine = builder.reverse().toString();
        }
        return reverseLine;
    }
}
