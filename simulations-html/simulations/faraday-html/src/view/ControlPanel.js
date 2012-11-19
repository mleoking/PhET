// Copyright 2002-2012, University of Colorado

/**
 * Control panel.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'common/Logger' ], function ( Logger ) {

    /**
     * @param {FaradayModel} model
     * @param {FaradayView} viewProperties
     * @constructor
     */
    function ControlPanel( model, view ) {

        var logger = new Logger( "faraday-main" ); // logger for this source file

        var seeInsideMagnetCheckBox = document.getElementById( "seeInsideMagnetCheckBox" );
        seeInsideMagnetCheckBox.checked = view.magnetTransparent.get();
        seeInsideMagnetCheckBox.onclick = function () {
            logger.info( "seeInsideMagnetCheckBox=" + seeInsideMagnetCheckBox.checked );
            //TODO
        };

        var showFieldCheckBox = document.getElementById( "showFieldCheckBox" );
        showFieldCheckBox.checked = view.fieldVisible.get();
        showFieldCheckBox.onclick = function () {
            logger.info( "showFieldCheckBox=" + showFieldCheckBox.checked );
            view.field.visible = showFieldCheckBox.checked;
        };

        var showCompassCheckBox = document.getElementById( "showCompassCheckBox" );
        showCompassCheckBox.checked = view.compassVisible.get();
        showCompassCheckBox.onclick = function () {
            logger.info( "showCompassCheckBox=" + showCompassCheckBox.checked );
            view.compass.visible = showCompassCheckBox.checked;
        };

        var showFieldMeterCheckBox = document.getElementById( "showFieldMeterCheckBox" );
        showFieldMeterCheckBox.checked = view.fieldMeterVisible.get();
        showFieldMeterCheckBox.onclick = function () {
        logger.info( "showFieldMeterCheckBox=" + showFieldMeterCheckBox.checked );
            view.meter.visible = showFieldMeterCheckBox.checked;
        };

        var flipPolarityButton = document.getElementById( "flipPolarityButton" );
        flipPolarityButton.onclick = function () {
            model.barMagnet.orientation.set( model.barMagnet.orientation.get() + Math.PI );
        };

        var resetAllButton = document.getElementById( "resetAllButton" );
        resetAllButton.onclick = function () {
            model.reset();
        };
    }

    return ControlPanel;
} );
