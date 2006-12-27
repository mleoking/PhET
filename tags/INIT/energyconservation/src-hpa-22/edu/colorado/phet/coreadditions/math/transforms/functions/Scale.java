package edu.colorado.phet.coreadditions.math.transforms.functions;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Dec 26, 2002
 * Time: 12:49:22 PM
 * To change this template use Options | File Templates.
 */
public class Scale implements Function {
    double factor;

    public Scale( double factor ) {
        this.factor = factor;
    }

    public double evaluate( double in ) {
        return in * factor;
    }

}
