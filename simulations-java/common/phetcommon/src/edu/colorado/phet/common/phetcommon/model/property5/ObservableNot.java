// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property5;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Provides the negation of an ObservableProperty<Boolean>, but unlike Not, its value cannot be set (only observed)
 *
 * @author Sam Reid
 */
public class ObservableNot extends ObservableProperty<Boolean> {
    private ObservableProperty<Boolean> parent;
    private Boolean lastValue;

    public ObservableNot( final ObservableProperty<Boolean> parent ) {
        super( !parent.getValue() );
        this.parent = parent;
        lastValue = getValue();
        parent.addObserver( new SimpleObserver() {
            public void update() {
                notifyObservers( getValue(), lastValue );
                lastValue = getValue();
            }
        } );
    }

    public And and( SettableProperty<Boolean> p ) {
        return new And( this, p );
    }

    @Override
    public Boolean getValue() {
        return !parent.getValue();
    }

    public static ObservableNot not( ObservableProperty<Boolean> p ) {
        return new ObservableNot( p );
    }
}