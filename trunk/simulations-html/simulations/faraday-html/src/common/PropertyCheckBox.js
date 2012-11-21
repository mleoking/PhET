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
                var checkBox = document.getElementById( id );
                checkBox.checked = booleanProperty.get();
                checkBox.onclick = function () {
                    booleanProperty.set( checkBox.checked );
                };
                booleanProperty.addObserver( function ( newValue ) {
                    checkBox.checked = newValue;
                } );
            };

            return PropertyCheckBox;
        } );
