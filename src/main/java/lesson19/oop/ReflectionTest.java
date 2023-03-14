package lesson19.oop;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;

public class ReflectionTest {

    public static void main(String[] args) {
        User user = new User();
        Class<?> testClass = user.getClass();

        try {
            System.out.println("Variant 1:");
            System.out.println("Class fields data: ");
            Field[] fields = testClass.getDeclaredFields();

            for (Field f : fields) {

                String filedName = f.getName();

                f.setAccessible(true);
                Object filedValue = f.get(user);
                System.out.printf("filedName: %s, filedValue: %s \n", filedName, filedValue);
            }

            System.out.println("Variant 2:");
            Object result = FieldUtils.readField(user, "firstName", true);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

class Person {
    private String firstName = "Mark";
    private String lastName = "Loo";
    private int age = 22;

    public Person() {
    }
}
