package lesson24.generic;

import java.util.ArrayList;
import java.util.List;

public class GenericTest {

    public static void main(String[] args) {
        String[] str = {"Monday", "Tuesday", "Wednesday", "Thursday"};
        List<String> listWithString = toList(str);

        Integer[] ints = {1, 2, 3, 4};
        List<Integer> listWithInteger = toList(ints);
    }


    //https://justamonad.com/generics-methods-and-varargs/
    public static <T> List<T> toList(T[] arr) {
        List<T> list = new ArrayList<T>();

        for (T t : arr) {
            list.add(t);
        }

        return list;
    }


}
