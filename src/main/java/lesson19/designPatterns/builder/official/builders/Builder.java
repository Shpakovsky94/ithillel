package lesson19.designPatterns.builder.official.builders;

import lesson19.designPatterns.builder.official.cars.CarType;
import lesson19.designPatterns.builder.official.components.Engine;
import lesson19.designPatterns.builder.official.components.GPSNavigator;
import lesson19.designPatterns.builder.official.components.Transmission;
import lesson19.designPatterns.builder.official.components.TripComputer;

/**
 * Builder interface defines all possible ways to configure a product.
 */
public interface Builder {
    void setCarType(CarType type);

    void setSeats(int seats);

    void setEngine(Engine engine);

    void setTransmission(Transmission transmission);

    void setTripComputer(TripComputer tripComputer);

    void setGPSNavigator(GPSNavigator gpsNavigator);
}
