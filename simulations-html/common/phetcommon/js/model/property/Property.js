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
define( [],
        function () {

            /**
             * @class Property
             * @constructor
             * @param value
             */
            function Property( value ) {

                // Variables declared in the constructor are private.
                var _value = value;
                var _initialValue = value;
                var _observers = new Array();

                /**
                 * Gets the value.
                 * @return {*}
                 */
                this.get = function () {
                    return _value;
                };

                /**
                 * Sets the value and notifies registered observers.
                 * If the value hasn't changed, this is a no-op.
                 *
                 * @param value
                 */
                this.set = function ( value ) {
                    if ( value !== _value ) {
                        var oldValue = _value;
                        _value = value;
                        var observersCopy = _observers.slice(); // make a copy, in case notification results in removeObserver
                        for ( var i = 0; i < observersCopy.length; i++ ) {
                            observersCopy[i]( value, oldValue );
                        }
                    }
                };

                /**
                 * Resets the value to the initial value.
                 */
                this.reset = function () {
                    this.set( _initialValue );
                };

                /**
                 * Adds an observer and notifies it immediately.
                 * If observer is already registered, this is a no-op.
                 * The initial notification provides the current value for newValue and null for oldValue.
                 *
                 * @param observer a function of the form observer(newValue,oldValue)
                 */
                this.addObserver = function ( observer ) {
                    if ( _observers.indexOf( observer ) === -1 ) {
                        _observers.push( observer );
                        observer( _value, null ); // null should be used when an object is expected but unavailable
                    }
                };

                /**
                 * Removes an observer.
                 * If observer is not registered, this is a no-op.
                 *
                 * @param observer
                 */
                this.removeObserver = function ( observer ) {
                    var index = _observers.indexOf( observer );
                    if ( index !== -1 ) {
                        _observers.splice( index, index + 1 );
                    }
                };

                /**
                 * This function returns a bound function that sets the specified value.  For use in creating closures e.g. with gui classes.
                 * For instance, to have a button that sets a property to true, instead of using
                 * button.click(function(){property.set(true);});
                 * you could use
                 * button.click(property._set(true));
                 * @param value the value to use when the setter is called.
                 * @return a function that can be used to set the specified value.
                 */
                this._set = function ( value ) { return this.set.bind( this, value );};
            }

            //TODO is this a good way of co-locating small tests?
            // test
            Property.prototype.test = function () {
                var name = new Property( "Moe" );
                var observer = function ( newName, oldName ) {
                    console.log( "newName=" + newName + ", oldName=" + oldName );
                };
                name.addObserver( observer );
                name.set( "Larry" ); // observer should be notified
                name.removeObserver( observer );
                name.set( "Curly" ); // this should result in no notification
            };

            return Property;
        } );