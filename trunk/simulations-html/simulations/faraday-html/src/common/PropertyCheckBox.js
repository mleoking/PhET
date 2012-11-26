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
             * @param {String} labelString
             */
            PropertyCheckBox.connect = function ( booleanProperty, id, labelString ) {

                // check box
                var checkBox = document.getElementById( id );
                checkBox.checked = booleanProperty.get();
                checkBox.onclick = function () {
                    booleanProperty.set( checkBox.checked );
                };
                booleanProperty.addObserver( function ( newValue ) {
                    checkBox.checked = newValue;
                } );

                // i18n of check box label
                var label = document.getElementById( id + "Label" );
                if ( label instanceof HTMLElement ) {
                    label.innerHTML = labelString;
                }
            };

            return PropertyCheckBox;
        } );
