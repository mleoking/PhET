package edu.colorado.phet.movingman.common.math;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Dec 26, 2002
 * Time: 12:58:11 PM
 * To change this template use Options | File Templates.
 */
public class RangeToRange {
    private double t1;
    private double s;
    private double t2;
    private double inStart;
    private double inEnd;
    private double outStart;
    private double outEnd;

    public RangeToRange( double inStart, double inEnd, double outStart, double outEnd ) {
        this.inStart = inStart;
        this.inEnd = inEnd;
        this.outStart = outStart;
        this.outEnd = outEnd;
        t1 = ( -inStart );
        double scale = ( outEnd - outStart ) / ( inEnd - inStart );
        s = scale;
        t2 = ( outStart );
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

    public double evaluate( double in ) {
        in = t1 + in;
        in = s * in;
        in = t2 + in;
        return in;
    }

    public RangeToRange invert() {
        return new RangeToRange( outStart, outEnd, inStart, inEnd );
    }

    public double getInputWidth() {
        return inEnd - inStart;
    }
}
