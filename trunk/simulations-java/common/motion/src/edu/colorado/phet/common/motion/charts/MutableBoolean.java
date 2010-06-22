package edu.colorado.phet.common.motion.charts;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

/**
 * @author Sam Reid
 */
public class MutableBoolean extends SimpleObservable {
    private boolean value;

    public MutableBoolean(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        if (this.value != value) {
            this.value = value;
            notifyObservers();
        }
    }
}
