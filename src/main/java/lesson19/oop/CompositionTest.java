package lesson19.oop;

public class CompositionTest {

    public static void main(String[] args) {
// Creating an object of Engine class.
        Engine engine = new Engine("Petrol", 300);

// Creating an object of Car class.
        Car car = new Car("Alto", engine);
        System.out.println(
                "Name of car: " + car.getName() + "\n" +
                        "Type of engine: " + engine.getType() + "\n" +
                        "Horse power of Engine: " + engine.getHorsePower());
    }
}

class Engine {
    private String type;
    private int horsePower;

    Engine(String type, int horsePower) {
        this.type = type;
        this.horsePower = horsePower;
    }

    public String getType() {
        return type;
    }

    public int getHorsePower() {
        return horsePower;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setHorsePower(int horsePower) {
        this.horsePower = horsePower;
    }
}

class Car {
    private final String name;
    private final Engine engine; // Composition.

    public Car(String name, Engine engine) {
        this.name = name;
        this.engine = engine;
    }

    public String getName() {
        return name;
    }

    public Engine getEngine() {
        return engine;
    }
}
