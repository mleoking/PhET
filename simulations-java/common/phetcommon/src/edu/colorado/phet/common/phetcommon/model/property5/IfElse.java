// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property5;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * IfElse<T> is an ObservableProperty that uses a Property<Boolean> condition to simulation an if/else block.
 *
 * @author Sam Reid
 */
public class IfElse<T> extends ObservableProperty<T> {
    private final Property<Boolean> condition;
    private final T yes;
    private final T no;
    private T oldValue;

    public IfElse( Property<Boolean> condition, T yes, T no ) {
        this.condition = condition;
        this.yes = yes;
        this.no = no;
        condition.addObserver( new SimpleObserver() {
            public void update() {
                notifyObservers( getValue(), oldValue );
                oldValue = getValue();
            }
        } );
    }

    @Override
    public T getValue() {
        return condition.getValue() ? yes : no;
    }
}
