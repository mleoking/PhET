// Copyright 2002-2012, University of Colorado

/**
 * Options panel.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [
            'common/WidgetConnector',
            'i18n!../../nls/faraday-strings',
            'tpl!../../templates/optionsButton.html',
            'tpl!../../templates/optionsPanel.html'
        ],
        function ( WidgetConnector, strings, optionsButtonTemplate, optionsPanelTemplate ) {

            function OptionsPanel() {
            }

            /**
             * Initializes the Options panel. This should only be called once.
             * @param {FaradayModel} model
             * @param {FaradayStage} stage
             */
            OptionsPanel.init = function ( model, stage ) {

                // DOM modification ------------------------------------------------------------

                // Add the Options button to the DOM
                var optionsButtonFragment = optionsButtonTemplate( { options:strings.options } );
                $( "#optionsButtonDiv" ).append( $( optionsButtonFragment ) ).trigger( "create" );

                // Add the Options panel to the DOM
                var optionsPanelFragment = optionsPanelTemplate(
                        {
                            options:strings.options,
                            magnetStrength:strings.magnetStrength,
                            flipPolarity:strings.flipPolarity,
                            seeInsideMagnet:strings.seeInsideMagnet,
                            showCompass:strings.showCompass,
                            showField:strings.showField,
                            showFieldMeter:strings.showFieldMeter,
                            resetAll:strings.resetAll
                        } );
                $( "#optionsPanelDiv" ).append( $( optionsPanelFragment ) ).trigger( "create" );

                /*
                 * Workaround for jQuery.mobile bug,
                 * see http://stackoverflow.com/questions/8088837/jquery-mobile-triggercreate-command-not-working
                 */
                $(".ui-page").trigger('pagecreate');

                // Make the Options panel the same height as the window
                $( "#optionsPanel" ).on(
                        {
                            popupbeforeposition:function () {
                                var h = $( window ).height();
                                $( "#optionsPanel" ).css( "height", h );
                            }
                        } );

                // Wire up DOM components ------------------------------------------------------

                // slider
                WidgetConnector.connectSliderToProperty( "strengthSlider", model.barMagnet.strength );

                // check boxes
                WidgetConnector.connectCheckBoxToProperty( "seeInsideCheckBox", stage.seeInside );
                WidgetConnector.connectCheckBoxToProperty( "compassCheckBox", model.compass.visible );
                WidgetConnector.connectCheckBoxToProperty( "fieldCheckBox", stage.showField );
                WidgetConnector.connectCheckBoxToProperty( "meterCheckBox", model.fieldMeter.visible );

                // buttons
                WidgetConnector.connectButtonToFunction( "flipPolarityButton",
                                                         function () {
                                                             model.barMagnet.orientation.set( model.barMagnet.orientation.get() + Math.PI );
                                                             model.compass.startMovingNow();
                                                         } );
                WidgetConnector.connectButtonToFunction( "resetAllButton",
                                                         function () {
                                                             model.reset();
                                                             stage.reset();
                                                         } );
            };

            return OptionsPanel;
        } );
