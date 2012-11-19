// Copyright 2002-2012, University of Colorado

/**
 * Control panel.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'common/Logger' ], function ( Logger ) {

    /**
     * @param {FaradayModel} model
     * @param {FaradayStage} stage
     * @constructor
     */
    function ControlPanel( model, stage ) {

        var logger = new Logger( "faraday-main" ); // logger for this source file

        // See Inside Magnet
        var seeInsideMagnetCheckBox = document.getElementById( "seeInsideMagnetCheckBox" );
        seeInsideMagnetCheckBox.checked = stage.magnetTransparent.get();
        seeInsideMagnetCheckBox.onclick = function () {
            logger.debug( "seeInsideMagnetCheckBox=" + seeInsideMagnetCheckBox.checked );
            //TODO
        };
        stage.magnetTransparent.addObserver( function ( newValue ) {
            seeInsideMagnetCheckBox.checked = newValue;
        } );

        // Show Field
        var showFieldCheckBox = document.getElementById( "showFieldCheckBox" );
        showFieldCheckBox.checked = model.field.visible.get();
        showFieldCheckBox.onclick = function () {
            logger.debug( "showFieldCheckBox=" + showFieldCheckBox.checked );
            model.field.visible.set( showFieldCheckBox.checked );
        };
        model.field.visible.addObserver( function ( newValue ) {
            showFieldCheckBox.checked = newValue;
        } );

        // Show Compass
        var showCompassCheckBox = document.getElementById( "showCompassCheckBox" );
        showCompassCheckBox.checked = model.compass.visible.get();
        showCompassCheckBox.onclick = function () {
            logger.debug( "showCompassCheckBox=" + showCompassCheckBox.checked );
            model.compass.visible.set( showCompassCheckBox.checked );
        };
        model.compass.visible.addObserver( function ( newValue ) {
            console.log( "compassVisible newValue=" + newValue );//XXX
            showCompassCheckBox.checked = newValue;
        } );

        // Show Field Meter
        var showFieldMeterCheckBox = document.getElementById( "showFieldMeterCheckBox" );
        showFieldMeterCheckBox.checked = model.fieldMeter.visible.get();
        showFieldMeterCheckBox.onclick = function () {
            logger.debug( "showFieldMeterCheckBox=" + showFieldMeterCheckBox.checked );
            model.fieldMeter.visible.set( showFieldMeterCheckBox.checked );
        };
        model.fieldMeter.visible.addObserver( function ( newValue ) {
            showFieldMeterCheckBox.checked = newValue;
        } );

        var flipPolarityButton = document.getElementById( "flipPolarityButton" );
        flipPolarityButton.onclick = function () {
            model.barMagnet.orientation.set( model.barMagnet.orientation.get() + Math.PI );
        };

        var resetAllButton = document.getElementById( "resetAllButton" );
        resetAllButton.onclick = function () {
            model.reset();
            stage.reset();
        };
    }

    return ControlPanel;
} );
