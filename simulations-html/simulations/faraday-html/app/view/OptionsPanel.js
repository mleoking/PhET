// Copyright 2002-2012, University of Colorado

/**
 * Options panel.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'common/PropertyCheckBox', 'i18n!../../nls/faraday-strings' ],
        function ( PropertyCheckBox, strings ) {

            function OptionsPanel() {
            }

            //TODO ??? this doesn't get called, but is recommended at http://jquerymobile.com/demos/1.2.0/docs/pages/popup/popup-panels.html
            OptionsPanel.resize = function () {
                // Make the options panel the same height as the window
                $( "#optionsPanel" ).on(
                        {
                            popupbeforeposition:function () {
                                var h = $( window ).height();
                                $( "#optionsPanel" ).css( "height", h );
                            }
                        } );
            };

            /**
             * @param {FaradayModel} model
             * @param {FaradayStage} stage
             */
            OptionsPanel.connect = function ( model, stage ) {

                OptionsPanel.resize();

                // Options panel title
                var optionsCloseButtonLabel = document.getElementById( "optionsCloseButtonLabel" );
                optionsCloseButtonLabel.value = strings["options"];

                // Strength slider
                var strengthSliderLabel = document.getElementById( "strengthSliderLabel" );
                strengthSliderLabel.innerHTML = strings.magnetStrength;
                var strengthSlider = document.getElementById( "strengthSlider" );
                strengthSlider.change = function ( event ) {
                    model.barMagnet.strength.set( strengthSlider.value );
                };
                model.barMagnet.strength.addObserver( function( strength ) {
                    //TODO: use jquery selectors everywhere
                    $("#strengthSlider").val(strength).slider( "refresh" );
                });

                // Check boxes
                PropertyCheckBox.connect( stage.seeInside, "seeInsideCheckBox", strings.seeInsideMagnet );
                PropertyCheckBox.connect( stage.showField, "fieldCheckBox", strings.showField );
                PropertyCheckBox.connect( model.compass.visible, "compassCheckBox", strings.showCompass );
                PropertyCheckBox.connect( model.fieldMeter.visible, "meterCheckBox", strings.showFieldMeter );

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

            return OptionsPanel;
        } );
