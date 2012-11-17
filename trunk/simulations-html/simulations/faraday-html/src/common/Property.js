// Copyright 2002-2012, University of Colorado

/**
 * An observable property, notifies registered observers when the value changes.
 *
 * Uses the "Constructor" pattern for object creation, which has the downside that
 * all properties are created once for each instance. It would be nice if our functions
 * were shared. But since the only way to create private fields is in the constructor,
 * and the functions need access to those private fields, there doesn't seem to be
 * any choice but to define the functions in the constructor.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define([], function() {

    function Property( value ) {

        // Variables declared in the constructor are private.
        var _value = value;
        var _observers = new Array();

        /**
         * Gets the value.
         * @return {*}
         */
        this.get = function() {
            return _value;
        };

        /**
         * Sets the value and notifies registered observers.
         * @param value
         */
        this.set = function( value ) {
            //TODO should (value === _value) result in a no-op?
            _value = value;
            var observersCopy = _observers.slice(); // make a copy, in case notification results in removeObserver
            for ( var i = 0; i < observersCopy.length; i++ ) {
                observersCopy[i]( _value );
            }
        };

        /**
         * Adds an observer, but does not notify it.
         * The observer should be a function that expects one argument of the same type as the value.
         * @param observer
         */
        this.addObserverNoNotify = function( observer ) {
            _observers.push( observer );
        };

        /**
         * Adds an observer, and notifies it immediately.
         * The observer should be a function that expects one argument of the same type as the value.
         * @param observer
         */
        this.addObserver = function( observer ) {
            this.addObserverNoNotify( observer );
            observer( _value );
        };


        /**
         * Removes an observer.
         * @param observer
         */
        this.removeObserver = function( observer ) {
            var index = _observers.indexOf( observer );
            if ( index !== -1 ) {
               _observers.splice( index, index + 1 );
            }
        };
    }

    //TODO is this a good way of co-locating small tests?
    // test
    Property.prototype.test = function () {
        var name = new Property( "Simon" );
        var observer = function ( name ) {
            console.log( "name=" + name );
        };
        name.addObserver( observer );
        name.set( "Garfunkel" ); // observer should be notified
        name.removeObserver( observer );
        name.set( "Simon" ); // this should result in no notification
    };

    return Property;
});