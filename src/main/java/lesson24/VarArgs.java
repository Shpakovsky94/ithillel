package lesson24;

import java.util.ArrayList;
import java.util.List;

public class VarArgs {
    public static void main(String[] args) {
        //example with String methods
        String.format("This is an integer: %d", 1);
        String.format("This is an integer: %d and a string: %s", 1, "s");

        //example with array
        int[] arr = {1, 3, 4};
        System.out.println(calculateWithArray(arr));
        //example with varArgs
        System.out.println(calculateWithVarArgs(1, 3, 4));


        List<String> stringsWithHeapPollution = polluteHeap();

        System.out.println(stringsWithHeapPollution.get(0));
    }


    public static int calculateWithArray(int[] list) {
        int sum = 0;
        for (int item : list) {
            sum += item;
        }
        return sum;
    }

    public static int calculateWithVarArgs(int... list) {
        int sum = 0;
        for (int item : list) {
            sum += item;
        }
        return sum;
    }

    public static List<String> polluteHeap() {
        List numbers = new ArrayList<Number>();
        numbers.add(1);
        List<String> strings = numbers;
        strings.add("");
        return strings;
    }


}
