// Copyright 2002-2012, University of Colorado

/**
 * Control panel.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'common/PropertyCheckBox', 'i18n!resources/nls/faraday-html-strings' ],
        function ( PropertyCheckBox, strings ) {

            function ControlPanel() {
            }

            /**
             * @param {FaradayModel} model
             * @param {FaradayStage} stage
             */
            ControlPanel.connect = function ( model, stage ) {

                //TODO Would prefer a slider, but HTML slider is brain damaged, and doesn't work in FireFox.
                // Strength text field
                var strengthTextFieldLabel = document.getElementById( "strengthTextFieldLabel" );
                //TODO strengthTextFieldLabel i18n
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
                PropertyCheckBox.connect( stage.seeInside, "seeInsideMagnetCheckBox" );
                PropertyCheckBox.connect( stage.showField, "showFieldCheckBox" );
                PropertyCheckBox.connect( model.compass.visible, "showCompassCheckBox" );
                PropertyCheckBox.connect( model.fieldMeter.visible, "showFieldMeterCheckBox" );

                var flipPolarityButton = document.getElementById( "flipPolarityButton" );
                flipPolarityButton.value = strings.flipPolarity;
                flipPolarityButton.onclick = function () {
                    model.barMagnet.orientation.set( model.barMagnet.orientation.get() + Math.PI );
                    model.compass.startMovingNow();
                };

                var resetAllButton = document.getElementById( "resetAllButton" );
                resetAllButton.value = strings.resetAll;
                resetAllButton.onclick = function () {
                    model.reset();
                    stage.reset();
                };
            };

            return ControlPanel;
        } );
