package edu.colorado.phet.movingman;

import edu.colorado.phet.common.motion.charts.Range;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 */
public class LinearTransform extends SimpleObservable {
    private Function.LinearFunction linearFunction;

    public LinearTransform(final Range modelRange, final Range viewRange) {
        linearFunction = new Function.LinearFunction(modelRange.getMin(), modelRange.getMax(), viewRange.getMin(), viewRange.getMax());
        final SimpleObserver update = new SimpleObserver() {
            public void update() {
                linearFunction.setInput(modelRange.getMin(), modelRange.getMax());
                linearFunction.setOutput(viewRange.getMin(), viewRange.getMax());
                notifyObservers();
            }
        };
        modelRange.addObserver(update);
        viewRange.addObserver(update);
    }

    public double evaluate(double x) {
        return linearFunction.evaluate(x);
    }
}