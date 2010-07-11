package edu.colorado.phet.common.motion.charts;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

/**
 * @author Sam Reid
 */
public class Range extends SimpleObservable {
    private double min;
    private double max;

    public Range(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        if (this.min != min) {
            this.min = min;
            notifyObservers();
        }
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        if (this.max != max) {
            this.max = max;
            notifyObservers();
        }
    }

    public double getRange() {
        return max - min;
    }

    public double clamp(double x) {
        return MathUtil.clamp(min, x, max);
    }

    //Provides support for animating a range
    public void stepTowardsRange(double min, double max,double speed) {
        double dMin = min - this.min;
        double dMax = max - this.max;
        double targetMin = this.min + speed * MathUtil.signum(dMin);
        double targetMax = this.max + speed * MathUtil.signum(dMax);
        if (Math.abs(targetMax - max) <= speed) {
            targetMax = max;
        }
        if (Math.abs(targetMin - min) <= speed) {
            targetMin = min;
        }
        setMin(targetMin);
        setMax(targetMax);
    }
}