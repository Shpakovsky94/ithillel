package lesson24.lambda;

public class LambdaInterview {

    //WarmUp
    //https://www.javaguides.net/2021/11/ava-lambda-expressions-interview-questions-and-answers.html
/*    Which of the following are valid lambda expressions?
A. String a, String b -> System.out.print(a+ b);
B.  () -> return;
C. (int i) -> i;
D.(int i) -> i++; return i;*/


    //Will code below execute? #1
/*    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        numbers.forEach(n -> {
            if(n % 2 == 0) {
                System.out.println(n * 2);
            }
        });
    }*/
    //Will code below execute? #2
/*    public static void main(String[] args) {
        int a = 5;
        int b = 0;
        IntSupplier s = () -> a / b;
        int result = s.getAsInt();
        System.out.println(result);
    }*/

    //Which of the following lambda expressions would correctly implement this interface to add two integers?
/*    public interface MathOperation {
        int operate(int a, int b);
    }
    A) (a, b) -> a * b
    B) (int a, int b) -> a + b
    C) (int a, int b) -> { return a + b; }
    D) (int a, int b) -> { a + b; }*/

    //Will code below execute? #3
/*    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        numbers.stream()
                .map(n -> n * n)
                .forEach(System.out::println);
    }*/

/*    Which of the following lambda expressions would
    correctly implement the java.util.function.Predicate interface to check if a given string is empty?
    A) () -> str.isEmpty()
    B) (str) -> str.isEmpty()
    C) (String str) -> str.length() == 0
    D) (String str) -> str.isEmpty()*/

    //Will code below execute? #4
/*    public static void main(String[] args) {
        Function<Integer, String> func = (n) -> {
            if (n % 2 == 0) {
                return "Even";
            } else {
                return "Odd";
            }
        };

        String result = func.apply(5);
        System.out.println(result);
    }*/

    //Will code below execute? #5
/*    public static void main(String[] args) {
        List<String> words = Arrays.asList("apple", "banana", "cherry");

        String result = words.stream()
                .reduce("", (a, b) -> a + b.charAt(0));
        System.out.println(result);
    }*/
}
