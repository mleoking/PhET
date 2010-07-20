package edu.colorado.phet.common.phetcommon.model;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

/**
 * This can be used to represent a Boolean value in a MVC style pattern.  It remembers its default value and can be reset.
 *
 * @author Sam Reid
 */
public class MutableBoolean extends SimpleObservable {
    private boolean value;
    private final boolean defaultValue;

    public MutableBoolean(boolean value) {
        this.defaultValue = value;
        this.value = value;
    }

    public void reset() {
        setValue(defaultValue);
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
