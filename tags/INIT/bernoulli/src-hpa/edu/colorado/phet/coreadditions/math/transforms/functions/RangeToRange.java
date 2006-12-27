package edu.colorado.phet.coreadditions.math.transforms.functions;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Dec 26, 2002
 * Time: 12:58:11 PM
 * To change this template use Options | File Templates.
 */
public class RangeToRange implements Function {
    Translate t1;
    Scale s;
    Translate t2;
    double inStart;
    double inEnd;
    double outStart;
    double outEnd;

    public RangeToRange(double inStart, double inEnd, double outStart, double outEnd) {
        this.inStart = inStart;
        this.inEnd = inEnd;
        this.outStart = outStart;
        this.outEnd = outEnd;
        t1 = new Translate(-inStart);
        double scale = (outEnd - outStart) / (inEnd - inStart);
        s = new Scale(scale);
        t2 = new Translate(outStart);
    }

    public double getLowInputPoint() {
        return inStart;
    }

    public double getHighInputPoint() {
        return inEnd;
    }

    public double getLowOutputPoint() {
        return outStart;
    }

    public double getHighOutputPoint() {
        return outEnd;
    }

    public double evaluate(double in) {
        in = t1.evaluate(in);
        in = s.evaluate(in);
        in = t2.evaluate(in);
        return in;
    }

    public RangeToRange invert() {
        return new RangeToRange(outStart, outEnd, inStart, inEnd);
    }

    public double getInputWidth() {
        return inEnd - inStart;
    }
}
