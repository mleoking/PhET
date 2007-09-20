package edu.colorado.phet.statesofmatter.model.engine;

public class ConstantMeasurable implements Measurable {
    private final double constant;

    public ConstantMeasurable(double constant) {
        this.constant = constant;
    }
    
    public double measure() {
        return constant;
    }
}
