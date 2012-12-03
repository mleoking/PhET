// Copyright 2002-2012, University of Colorado

/**
 * Connects a boolean Property to an HTML check box.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [],
        function () {

            function PropertyCheckBox() {
            }

            /**
             * @param {Property} booleanProperty the property to be synchronized
             * @param {String} id id attribute of the HTML element containing the check box
             */
            PropertyCheckBox.connect = function ( booleanProperty, id ) {

                // Initial state
                $( "#" + id ).attr( "checked", booleanProperty.get() ).checkboxradio( "refresh" );

                // sync model with check box
                $( "#" + id ).bind( 'change', function () {
                    booleanProperty.set( $( "#" + id ).attr( "checked" ) );
                } );

                // sync check box with model
                var setChecked = function ( checked ) {
                    console.log( "setChecked" );
                    $( "#" + id ).attr( "checked", checked ).checkboxradio( "refresh" );
                };
                booleanProperty.addObserver( function ( checked ) {
                    setChecked( checked );
                } );

                // initial state
                setChecked( booleanProperty.get() );
            };

            return PropertyCheckBox;
        } );
