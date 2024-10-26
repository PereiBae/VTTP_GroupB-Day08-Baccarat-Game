package main.server;

public class Cards {
    
    private double numericCode;
    private int value;

    public Cards(double numericCode, int value) {
        this.numericCode = numericCode;
        this.value = value;
    }

    @Override
    public String toString() {
        return numericCode + " " + value ;
    }

    // Getter for numericCode and value if needed
    public double getNumericCode() {
        return numericCode;
    }

    public int getValue() {
        return value;
    }
}

