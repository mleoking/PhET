package edu.colorado.phet.reids.admin;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

/**
 * @author Sam Reid
 */
public class MutableInt extends SimpleObservable {
    private int value;

    public MutableInt(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        notifyObservers();
    }
}
