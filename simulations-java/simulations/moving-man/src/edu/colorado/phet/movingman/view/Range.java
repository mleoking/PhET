package edu.colorado.phet.movingman.view;

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
        this.min = min;
        notifyObservers();
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
        notifyObservers();
    }

    public double getRange() {
        return max - min;
    }

    public double clamp(double x) {
        return MathUtil.clamp(min, x, max);
    }
}
