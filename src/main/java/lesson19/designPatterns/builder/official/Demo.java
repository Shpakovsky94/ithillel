package lesson19.designPatterns.builder.official;

import lesson19.designPatterns.builder.official.builders.CarBuilder;
import lesson19.designPatterns.builder.official.builders.CarManualBuilder;
import lesson19.designPatterns.builder.official.cars.Car;
import lesson19.designPatterns.builder.official.cars.Manual;
import lesson19.designPatterns.builder.official.director.Director;

/**
 * Demo class. Everything comes together here.
 */
public class Demo {

    //https://refactoring.guru/uk/design-patterns/builder
    //https://refactoring.guru/uk/design-patterns/builder/java/example
    public static void main(String[] args) {
        Director director = new Director();

        // Director gets the concrete builder object from the client
        // (application code). That's because application knows better which
        // builder to use to get a specific product.
        CarBuilder builder = new CarBuilder();
        director.constructSportsCar(builder);

        // The final product is often retrieved from a builder object, since
        // Director is not aware and not dependent on concrete builders and
        // products.
        Car car = builder.getResult();
        System.out.println("Car built:\n" + car.getCarType());


        CarManualBuilder manualBuilder = new CarManualBuilder();

        // Director may know several building recipes.
        director.constructSportsCar(manualBuilder);
        Manual carManual = manualBuilder.getResult();
        System.out.println("\nCar manual built:\n" + carManual.print());
    }

}
