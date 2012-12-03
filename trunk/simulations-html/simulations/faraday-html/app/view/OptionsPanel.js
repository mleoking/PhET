// Copyright 2002-2012, University of Colorado

/**
 * Options panel.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [
            'common/PropertyCheckBox',
            'i18n!../../nls/faraday-strings',
            'tpl!../../templates/optionsButton.html',
            'tpl!../../templates/optionsPanel.html'
        ],
        function ( PropertyCheckBox, strings, optionsButtonTemplate, optionsPanelTemplate ) {

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
             * Initializes the Options panel. This should only be called once.
             * @param {FaradayModel} model
             * @param {FaradayStage} stage
             */
            OptionsPanel.init = function ( model, stage ) {

                OptionsPanel.resize();

                // DOM modification ------------------------------------------------------------

                // Add the Options button to the DOM
                var optionsButtonFragment = optionsButtonTemplate( { options:strings.options } );
                $( "#optionsButtonDiv" ).append( $( optionsButtonFragment ) ).trigger( "create" );

                // Add the Options panel to the DOM
                var optionsPanelFragment = optionsPanelTemplate(
                        {
                            magnetStrength:strings.magnetStrength,
                            flipPolarity:strings.flipPolarity,
                            seeInsideMagnet:strings.seeInsideMagnet,
                            showCompass:strings.showCompass,
                            showField:strings.showField,
                            showFieldMeter:strings.showFieldMeter,
                            resetAll:strings.resetAll
                        } );
                $( "#optionsPanelDiv" ).append( $( optionsPanelFragment ) ).trigger( "create" );

                // Wire up DOM components ------------------------------------------------------

                // Strength slider
                var strengthSlider = document.getElementById( "strengthSlider" );
                strengthSlider.change = function ( event ) {
                    model.barMagnet.strength.set( strengthSlider.value );
                };
                model.barMagnet.strength.addObserver( function ( strength ) {
                    //TODO: use jquery selectors everywhere
                    $( "#strengthSlider" ).val( strength ).slider( "refresh" );
                } );

                // Check boxes
                PropertyCheckBox.connect( stage.seeInside, "seeInsideCheckBox" );
                PropertyCheckBox.connect( model.compass.visible, "compassCheckBox" );
                PropertyCheckBox.connect( stage.showField, "fieldCheckBox" );
                PropertyCheckBox.connect( model.fieldMeter.visible, "meterCheckBox" );

                $( "#flipPolarityButton" ).bind( 'click',
                                                 function () {
                                                     model.barMagnet.orientation.set( model.barMagnet.orientation.get() + Math.PI );
                                                     model.compass.startMovingNow();
                                                 } );

                $( "#resetAllButton" ).bind( 'click',
                                             function () {
                                                 model.reset();
                                                 stage.reset();
                                             } );
            };

            return OptionsPanel;
        } );
