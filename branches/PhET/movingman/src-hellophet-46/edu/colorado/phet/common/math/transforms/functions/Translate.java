package edu.colorado.phet.common.math.transforms.functions;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Dec 26, 2002
 * Time: 12:48:44 PM
 * To change this template use Options | File Templates.
 */
public class Translate implements Function {
    double dx;

    public Translate(double dx) {
        this.dx = dx;
    }

    public double evaluate(double in) {
        return in + dx;
    }
}
