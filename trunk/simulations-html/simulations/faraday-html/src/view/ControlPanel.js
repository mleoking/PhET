// Copyright 2002-2012, University of Colorado

/**
 * Control panel.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'common/PropertyCheckBox' ],
        function ( PropertyCheckBox ) {

            function ControlPanel() {
            }

            /**
             * @param {FaradayModel} model
             * @param {FaradayStage} stage
             */
            ControlPanel.connect = function ( model, stage ) {

                //TODO Would prefer a slider, but HTML slider is brain damaged, and doesn't work in FireFox.
                // Strength text field
                var strengthTextField = document.getElementById( "strengthTextField" );
                strengthTextField.onkeydown = function ( event ) {
                    // Interpretting keyCodes has all kinds of browser incompatibility problems.
                    if ( event.keyCode === 13 ) {
                        // keep the value in range
                        if ( strengthTextField.value < model.barMagnet.strengthRange.min ) {
                            strengthTextField.value = model.barMagnet.strengthRange.min;
                        }
                        else if ( strengthTextField.value > model.barMagnet.strengthRange.max ) {
                            strengthTextField.value = model.barMagnet.strengthRange.max;
                        }
                        // update the model
                        model.barMagnet.strength.set( strengthTextField.value );
                    }
                };

                // Check boxes
                PropertyCheckBox.connect( stage.magnetTransparent, "seeInsideMagnetCheckBox" );
                PropertyCheckBox.connect( model.field.visible, "showFieldCheckBox" );
                PropertyCheckBox.connect( model.compass.visible, "showCompassCheckBox" );
                PropertyCheckBox.connect( model.fieldMeter.visible, "showFieldMeterCheckBox" );

                var flipPolarityButton = document.getElementById( "flipPolarityButton" );
                flipPolarityButton.onmousemove = function () {
                    model.barMagnet.orientation.set( model.barMagnet.orientation.get() + Math.PI );
                };

                var resetAllButton = document.getElementById( "resetAllButton" );
                resetAllButton.onclick = function () {
                    model.reset();
                    stage.reset();
                };
            };

            return ControlPanel;
        } );
