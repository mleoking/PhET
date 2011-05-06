// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property3;

/**
 * Observable property that indicates whether its argument (also observable) is equal to the specified value
 *
 * @author Sam Reid
 */
public class ValueEquals<T> extends CompositeProperty<Boolean> {//No set defined on ValueEquals since undefined what to set the value to if "false"
    private final GettableObservable0<T> property;
    private final T value;

    //Keep track of state and don't send out notifications unless the values changes
    private final Notifier<Boolean> notifier;

    public ValueEquals( GettableObservable0<T> property, T value ) {
        this.property = property;
        this.value = value;
        notifier = new Notifier<Boolean>( get() );
        new RichListener() {
            public void apply() {
                if ( notifier.set( get() ) ) {
                    notifyListeners();
                }
            }
        }.observe( property );
    }

    //Returns true if the wrapped observable is equal to the specified value
    public Boolean get() {
        return property.get() == value;//TODO why is this referential equality? What if we're comparing Booleans with autoboxing?
    }
}