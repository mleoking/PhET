package edu.colorado.phet.coreadditions.math.transforms.functions;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Dec 26, 2002
 * Time: 12:58:11 PM
 * To change this template use Options | File Templates.
 */
public class RangeToRangeInvert implements Function {
    RangeToRange core;
    double inStart;
    double inEnd;
    double outStart;
    double outEnd;
    double midout;//=(outStart+outEnd)/2;

    public RangeToRangeInvert(double inStart, double inEnd, double outStart, double outEnd) {
        this.inStart = inStart;
        this.inEnd = inEnd;
        this.outStart = outStart;
        this.outEnd = outEnd;
        core = new RangeToRange(inStart, inEnd, outStart, outEnd);
        this.midout = (outStart + outEnd) / 2;
    }

    public double evaluate(double in) {
        double regular = core.evaluate(in);
        double distFromMid = midout - regular;//regular-midout;
        double flipped = regular + 2 * distFromMid;
        return flipped;
    }
}
