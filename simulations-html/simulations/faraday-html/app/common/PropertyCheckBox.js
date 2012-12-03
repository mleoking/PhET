// Copyright 2002-2012, University of Colorado

/**
 * Connects a boolean Property to a jQuery Mobile check box.
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

                var checkBox = $( "#" + id ); // caution: this is actually a "wrapped set"

                // Initial state
                checkBox.attr( "checked", booleanProperty.get() ).checkboxradio( "refresh" );

                // sync model with check box
                checkBox.bind( 'change', function () {
                    booleanProperty.set( checkBox.attr( "checked" ) );
                } );

                // sync check box with model
                var setChecked = function ( checked ) {
                    checkBox.attr( "checked", checked ).checkboxradio( "refresh" );
                };
                booleanProperty.addObserver( function ( checked ) {
                    setChecked( checked );
                } );

                // initial state
                setChecked( booleanProperty.get() );
            };

            return PropertyCheckBox;
        } );
