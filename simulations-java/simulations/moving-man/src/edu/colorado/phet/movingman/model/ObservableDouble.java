package edu.colorado.phet.movingman.model;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

/**
 * @author Sam Reid
 */
public class ObservableDouble extends SimpleObservable {
    private double value;

    public ObservableDouble(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    //Not all clients should be able to mutate the ObservableDouble, only specific instances, so don't make this part of its public interface

    protected void setValue(double value) {
        this.value = value;
        notifyObservers();
    }
}
