// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.model.property5;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function2;

/**
 * Returns a boolean computation over Property<Boolean> arguments, such as And or Or.
 *
 * @author Sam Reid
 */
public class BinaryBooleanProperty extends ObservableProperty<Boolean> {
    private ObservableProperty<Boolean> a;
    private ObservableProperty<Boolean> b;
    private final Function2<Boolean, Boolean, Boolean> combiner;//rule for combining the booleans
    private boolean valueAtLastNotification;

    public BinaryBooleanProperty( final ObservableProperty<Boolean> a, final ObservableProperty<Boolean> b, Function2<Boolean, Boolean, Boolean> combiner ) {
        this.a = a;
        this.b = b;
        this.combiner = combiner;
        //When child properties change, only send out notifications if the combined property changes
        final SimpleObserver updateState = new SimpleObserver() {
            public void update() {
                if ( getValue() != valueAtLastNotification ) {
                    notifyObservers( getValue(), valueAtLastNotification );
                    valueAtLastNotification = getValue();
                }
            }
        };
        a.addObserver( updateState );
        b.addObserver( updateState );
        valueAtLastNotification = getValue();
    }

    @Override
    public Boolean getValue() {
        return combiner.apply( a.getValue(), b.getValue() );
    }
}
