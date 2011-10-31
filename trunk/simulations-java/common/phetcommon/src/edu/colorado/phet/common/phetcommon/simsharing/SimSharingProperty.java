// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.param;

/**
 * Property that can record its value with the sim sharing features.  Use of this class makes it easy to report values, in many cases I just need to change this:
 * <p/>
 * selectedLevel = new Property<Integer>(1);
 * <p/>
 * to this:
 * <p/>
 * selectedLevel = new SimSharingProperty<Integer>("Selected level",1);
 * <p/>
 * And it will report any changes.
 * Note that this reports any changes to the property, so should not be used with model properties that change automatically (though the values often will change after the user presses "reset all").
 * Also, it does not report the initial value of the property, just subsequent changes.
 *
 * @author Sam Reid
 */
public class SimSharingProperty<T> extends Property<T> {
    public SimSharingProperty( final String name, final T value ) {
        this( name, value, new Function1<T, String>() {
            public String apply( T t ) {
                return t.toString();
            }
        } );
    }

    public SimSharingProperty( final String name, final T value, final Function1<T, String> toString ) {
        super( value );

        //Observe changes but do not notify about the initial value//TODO: Or should we notify about the initial value for purposes of knowing how the sim started up in case it is changed later?
        addObserver( new SimpleObserver() {
            public void update() {
                SimSharingEvents.actionPerformed( name, "changed", param( "value", toString.apply( get() ) ) );
            }
        }, false );
    }
}
