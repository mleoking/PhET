package edu.colorado.phet.gravityandorbits.model;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 */
public class RestoreProperty<T> extends PublicProperty<T> {
    private final Property<Boolean> clockPaused;
    private T restorePoint;
    private Property<Boolean> changed;

    public RestoreProperty( Property<Boolean> clockPaused, T value ) {
        super( value );
        this.clockPaused = clockPaused;
        this.restorePoint = value;

        changed = new Property<Boolean>( !equalsRestorePoint() );
    }

    @Override
    public void setValue( T value ) {
        super.setValue( value );
        if ( clockPaused.getValue() ) {
            restorePoint = value;
        }
        changed.setValue( !equalsRestorePoint() );
    }

    public boolean equalsRestorePoint() {
        return restorePoint.equals( getValue() );
    }

    public void restore() {
        setValue( restorePoint );
    }

    public Property<Boolean> changed() {
        return changed;
    }
}
