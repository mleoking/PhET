// Copyright 2002-2012, University of Colorado

/**
 * Control panel.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'common/PropertyCheckBox' ], function ( PropertyCheckBox ) {

    function ControlPanel() {}

    /**
     * @param {FaradayModel} model
     * @param {FaradayStage} stage
     */
    ControlPanel.connect = function( model, stage ) {

        // Check boxes
        PropertyCheckBox.connect( stage.magnetTransparent, "seeInsideMagnetCheckBox" );
        PropertyCheckBox.connect( model.field.visible, "showFieldCheckBox" );
        PropertyCheckBox.connect( model.compass.visible, "showCompassCheckBox" );
        PropertyCheckBox.connect( model.fieldMeter.visible, "showFieldMeterCheckBox" );

        var flipPolarityButton = document.getElementById( "flipPolarityButton" );
        flipPolarityButton.onclick = function () {
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
