package lesson19.designPrinciples.interfaceSegregation;

public interface BearKeeper {
    void washTheBear();

    void feedTheBear();

    void petTheBear();
}

//split to small interfaces
interface BearCleaner {
    void washTheBear();
}

interface BearFeeder {
    void feedTheBear();
}

interface BearPetter {
    void petTheBear();
}

class BearCarer implements BearCleaner, BearFeeder {

    public void washTheBear() {
        //I think we missed a spot...
    }

    public void feedTheBear() {
        //Tuna Tuesdays...
    }
}

class CrazyPerson implements BearPetter {

    public void petTheBear() {
        //Good luck with that!
    }
}
