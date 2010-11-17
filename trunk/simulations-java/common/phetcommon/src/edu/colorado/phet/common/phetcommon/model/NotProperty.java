package edu.colorado.phet.common.phetcommon.model;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Provides the negation of a Property<Boolean>
 *
 * @author Sam Reid
 */
public class NotProperty extends Property<Boolean> {
    public NotProperty( final Property<Boolean> p ) {
        super( !p.getValue() );
        p.addObserver( new SimpleObserver() {
            public void update() {
                setValue( !p.getValue() );
            }
        } );
        addObserver( new SimpleObserver() {
            public void update() {
                p.setValue( !getValue() );
            }
        } );
    }

}
