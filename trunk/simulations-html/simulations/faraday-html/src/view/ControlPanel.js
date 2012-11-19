// Copyright 2002-2012, University of Colorado

/**
 * Control panel.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'common/Logger' ], function ( Logger ) {

    /**
     * @param {FaradayModel} model
     * @constructor
     */
    function ControlPanel( model ) {

        var logger = new Logger( "faraday-main" ); // logger for this source file

        //TODO similar for other check boxes
        var showCompassCheckBox = document.getElementById( "showCompassCheckBox" );
        showCompassCheckBox.onclick = function () {
            //TODO change compass visibility
            logger.info( "showCompassCheckBox=" + showCompassCheckBox.checked );
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
