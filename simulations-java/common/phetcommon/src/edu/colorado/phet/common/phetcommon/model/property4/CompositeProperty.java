// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property4;

import java.util.ArrayList;

/*
 * TODO: It's possible for subclasses to call or override setValue, which is not cool for a composite property.
 * Investigate using composition instead of inheritance for storing the value.
 */
/**
 * TODO: If a client adds a listener to one of the arg properties, and calls getValue on the composite in
 * that listener's callback, the composite value may not have been updated yet and will return a stale
 * (and therefore incorrect value.)   This is a synchronization issue. It's also bad client code.
 * But perhaps we should try to design so that this can't happen, since real-world dependencies can get complicated.
 * Evaluating the composite when getValue is called makes it problematic to define and store oldValue,
 * and as a general solution, could be an issue if the computation of the value is performance intensive.
 */

/**
 * A composite property's value is based on a set of dependent properties (the "args") and a function.
 * The args may be of homogeneous or heterogeneous types.
 * A composite property is publicly gettable, but not publicly settable because its value is derived.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CompositeProperty<T> extends GettableProperty<T> {

    /**
     * Function for computing a composite value.
     * The function is applied to the args when any of the arg values changes.
     * T is the return type of the function.
     */
    public static abstract class CompositeFunction<T> {

        private final ArrayList<GettableProperty> args; // function arguments

        /**
         * Function with a variable number of homogeneous args.
         *
         * @param args
         */
        public CompositeFunction( GettableProperty... args ) {
            this.args = new ArrayList<GettableProperty>();
            for ( GettableProperty arg : args ) {
                this.args.add( arg );
            }
        }

        /**
         * Clients implement this. Note that the apply function has no arguments,
         * and the arguments should be accessed via getArgs.
         *
         * @return
         */
        public abstract T apply();

        /**
         * Provides subclasses with access to the function args.  This method is useful
         * when it's possible to process the args via iteration. An alternative to using
         * this method is to reference final constructor parameters in the implementation
         * of apply. For example, see the implementation of ValueEquals.
         *
         * @return
         */
        protected ArrayList<GettableProperty> getArgs() {
            return args;
        }

        /*
         * Adds a listener to each of the args.
         * Private because this should not be exposed to clients.
         */
        private void addArgsListener( PropertyChangeListener listener ) {
            for ( GettableProperty arg : args ) {
                arg.addListener( listener );
            }
        }

        /*
         * Removes a listener from each of the args.
         * Private because this should not be exposed to clients.
         */
        private void removeArgsListener( PropertyChangeListener listener ) {
            for ( GettableProperty arg : args ) {
                arg.removeListener( listener );
            }
        }
    }

    private final CompositeFunction<T> function;
    private final PropertyChangeListener<T> argsListener;

    /**
     * Constructor
     *
     * @param function a function that is applied when any of the function's args change.
     */
    public CompositeProperty( final CompositeFunction<T> function ) {
        super( function.apply() );

        this.function = function;

        // changing any of the args will apply the function
        argsListener = new PropertyChangeListener<T>() {
            public void propertyChanged( PropertyChangeEvent<T> event ) {
                setValue( function.apply() );
            }
        };
        function.addArgsListener( argsListener );
    }

    /**
     * Call this if you're done with the composite value, but not done with the args.
     * If you don't call this, the args still reference the argsListener. This property
     * will continue to receive notifications, and will not be garbage collected.
     */
    public void cleanup() {
        function.removeArgsListener( argsListener );
    }
}
