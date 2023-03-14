package lesson19.designPrinciples.dependencyInversion;

public class Main {
}

class Windows98Machine {

    private final Keyboard keyboard;
    private final Monitor monitor;

    public Windows98Machine(Keyboard keyboard, Monitor monitor) {
        this.keyboard = keyboard;
        this.monitor = monitor;
    }
}

interface Keyboard {
}

class StandardKeyboard implements Keyboard {
}

interface Monitor {
}

class StandardMonitor implements Keyboard {
}


