package lesson12;

public class StaticQuizz {

}

//https://www.scientecheasy.com/2021/10/java-static-interview-questions.html/
class Test1 {
    private static int x = 10;

    static int m1() {
        int y = x;
        return y;
    }

    public static void main(String[] args) {
        m1();
    }
}

class Test33 {
    public int x = 10;
}

class Test2 {
    private int x = 10;

    static int m1() {
        Test33 obj = new Test33();
        int y = obj.x;
        return y;
    }

    public static void main(String[] args) {
        System.out.println(m1());
    }
}

class Test3 {
    static int a = 20;
    static int b = 30;
    static int c = 40;

    Test3() {
        a = 200;
    }

    static void m1() {
        Test3 test3 = new Test3();
        b = 300;
    }

    static {
        c = 400;
    }

    public static void main(String[] args) {
        m1();
        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
    }
}


class Test4 {
    static int a = 20;

    Test4() {
        a = 200;
    }

    public static void main(String[] args) {
        new Test4();
        System.out.println(a);
    }
}


class Test5 {
    static int a = 20;

    Test5() {
        a++;
    }

    void m1() {
        a++;
        System.out.println(a);
    }

    public static void main(String[] args) {
        Test5 obj = new Test5();
        Test5 obj2 = new Test5();
        Test5 obj3 = new Test5();
        obj3.m1();
    }
}


class Myclass {
    private static int x = 10;

    static {
        x++;
    }

    static {
        ++x;
    }

    {
        x--;
    }

    public static void main(String[] args) {
        Myclass obj = new Myclass();
        Myclass obj2 = new Myclass();
        Myclass obj3 = new Myclass();
        System.out.println(x);
    }
}
