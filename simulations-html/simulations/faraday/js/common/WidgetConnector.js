// Copyright 2002-2012, University of Colorado

/**
 * Encapsulates the connection of DOM user-interface components to JavaScript.
 * This implementation uses jQuery Mobile.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
define( [],
        function () {

            function WidgetConnector() {
            }

            /**
             * @param id id attribute of the HTML element containing the check box
             * @param f a function that expects no parameters
             */
            WidgetConnector.connectButtonToFunction = function ( id, f ) {
                $( "#" + id ).bind( 'click', f );
            }

            /**
             * @param {String} id id attribute of the HTML element containing the check box
             * @param {Property} booleanProperty the property to be synchronized
             */
            WidgetConnector.connectCheckBoxToProperty = function ( id, booleanProperty ) {

                var checkBox = $( "#" + id ); // caution: this is actually a "wrapped set"

                // sync model with check box
                checkBox.bind( 'change', function () {
                    booleanProperty.set( checkBox.attr( "checked" ) );
                } );

                // sync check box with model
                booleanProperty.addObserver( function ( checked ) {
                    checkBox.attr( "checked", checked ).checkboxradio( "refresh" );
                } );
            };

            /**
             * @param {String} id id attribute of the HTML element containing the check box
             * @param {Property} numberProperty the property to be synchronized
             */
            WidgetConnector.connectSliderToProperty = function ( id, numberProperty ) {

                var slider = $( "#" + id ); // caution: this is actually a "wrapped set"

                // sync model with check box
                slider.bind( 'change', function () {
                    numberProperty.set( slider.attr( "value" ) );
                } );

                // sync check box with model
                numberProperty.addObserver( function ( value ) {
                    slider.attr( "value", value ).slider( "refresh" );
                } );
            };

            return WidgetConnector;

        } );