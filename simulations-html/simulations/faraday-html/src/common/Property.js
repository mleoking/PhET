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
         * If the value hasn't changed, this is a no-op.
         *
         * @param value
         */
        this.set = function( value ) {
            if ( value !== _value ) {
                var oldValue = _value;
                _value = value;
                var observersCopy = _observers.slice(); // make a copy, in case notification results in removeObserver
                for ( var i = 0; i < observersCopy.length; i++ ) {
                    observersCopy[i]( oldValue, value );
                }
            }
        };

        /**
         * Adds an observer.
         * If observer is already registered, this is a no-op.
         *
         * @param observer a function of the form observer(oldValue,newValue)
         */
        this.addObserver = function( observer ) {
            if ( _observers.indexOf( observer ) === -1 ) {
                _observers.push( observer );
            }
        };

        /**
         * Removes an observer.
         * If observer is not registered, this is a no-op.
         *
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
        var name = new Property( "Moe" );
        var observer = function ( oldName, newName ) {
            console.log( "oldName=" + oldName + ", newName=" + newName );
        };
        name.addObserver( observer );
        name.set( "Larry" ); // observer should be notified
        name.removeObserver( observer );
        name.set( "Curly" ); // this should result in no notification
    };

    return Property;
});