package lesson24.generic;

public class GenericInterview {
}

//https://www.wisdomjobs.com/e-university/java-generics-interview-questions.html
/*final class Algorithm {
    public static <T> boolean max(T x, T y) {
        return x > y ? x : y;
    }
}

final class Algorithm {
    public static <T extends Comparable<T>> T max(T x, T y) {
        return x.compareTo(y) > 0 ? x : y;
    }
}*/

//Will code below execute? #1
/*
class GenericClass<T> {
    private T data;

    public GenericClass(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public static void main(String[] args) {
        List<T> list = new ArrayList<>();
        list.add("apple");
        list.add("banana");
        list.add("orange");

        GenericClass<String> stringObject = new GenericClass<>(list.get(0));
        String data = stringObject.getData();
        System.out.println(data);
    }
}
*/

//Will code below execute? #2
/*
class GenericClass<T extends Number> {
    private T data;

    public GenericClass(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public static void main(String[] args) {
        List<String> stringList = new ArrayList<>();
        stringList.add("apple");
        stringList.add("banana");
        stringList.add("orange");

        GenericClass<Integer> stringObject = new GenericClass<>(stringList.get(0));
        String data = stringObject.getData();
        System.out.println(data);
    }
}
*/

//Will code below execute? #3
/*class Main{
    public static void main(String[] args) {
        List<Object> objects = new ArrayList<>();
        List<String> strings = new ArrayList<>();
        objects = Collections.singletonList(strings);
        objects.add("test");
    }
}*/

//Will code below execute? #3
/*class MyGenericClass<T> {
    private T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
 class Main{
     public static void main(String[] args) {
         MyGenericClass<String> myString = new MyGenericClass<>();
         MyGenericClass<Integer> myInteger = new MyGenericClass<>();
         myString = myInteger;
     }
 }*/

//Will code below execute? #4
/*class MyGenericClass<T> {
    private T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}

class Main {
    public static void main(String[] args) {
        MyGenericClass<?> myWildcard = new MyGenericClass<>();
        myWildcard.setValue("Hello World");
    }
}*/

//Will code below execute? #5
/*
class MyGenericClass<T extends Number> {
    private T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public static <E> void printValue(E e) {
        System.out.println(e);
    }
}
class Main{
    public static void main(String[] args) {
        MyGenericClass.printValue("Hello World");
    }
}
*/
