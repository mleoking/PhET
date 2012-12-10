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
define( [], function () {

    /**
     * @class Property
     * @constructor
     * @param value
     */
    function Property( value ) {

        // Variables declared in the constructor are private.
        this._value = value;
        this._initialValue = value;
        this._observers = [];
    }

    /**
     * Gets the value.
     * @return {*}
     */
    Property.prototype.get = function () {
        return this._value;
    };

    /**
     * Sets the value and notifies registered observers.
     * If the value hasn't changed, this is a no-op.
     *
     * @param value
     */

        // TODO: this should be refactored to use jQuery custom events.
    Property.prototype.set = function ( value ) {
        if ( value !== this._value ) {
            var oldValue = this._value;
            this._value = value;
            var observersCopy = this._observers.slice(); // make a copy, in case notification results in removeObserver
            for ( var i = 0; i < observersCopy.length; i++ ) {
                observersCopy[i]( value, oldValue );
            }
        }
    };

    /**
     * Resets the value to the initial value.
     */
    Property.prototype.reset = function () {this.set( this._initialValue );};

    /**
     * Adds an observer.
     * If observer is already registered, this is a no-op.
     *
     * @param observer a function of the form observer(newValue,oldValue)
     */
    Property.prototype.addObserver = function ( observer ) {
        if ( this._observers.indexOf( observer ) === -1 ) {
            this._observers.push( observer );
        }
        observer( this.get(), this.get() );
    };

    /**
     * Adds an observer and sends a notification message with the current value.
     * TODO: Discuss with team whether this should be merged into addObserver
     * @param observer
     */
    Property.prototype.observe = function ( observer ) {
        this.addObserver( observer );
        observer( this.get(), this.get() );
    };

    /**
     * Removes an observer.
     * If observer is not registered, this is a no-op.
     *
     * @param observer
     */
    Property.prototype.removeObserver = function ( observer ) {
        var index = this._observers.indexOf( observer );
        if ( index !== -1 ) {
            this._observers.splice( index, index + 1 );
        }
    };

    return Property;
} );