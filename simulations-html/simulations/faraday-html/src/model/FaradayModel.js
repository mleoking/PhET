// Copyright 2002-2012, University of Colorado

/**
 * Model container.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'easel', 'common/Dimension', 'common/Logger', 'model/BarMagnet' ], function ( Easel, Dimension, Logger, BarMagnet ) {

    function FaradayModel() {

        var logger = new Logger( "faraday-main" ); // logger for this source file

        this.barMagnet = new BarMagnet( new Easel.Point( 0, 0 ), new Dimension( 250, 50 ), 10, 0 );

        // Debug output for the bar magnet.
        var thisModel = this;
        this.barMagnet.location.addObserver( function () {
            logger.info( "barMagnet.location=" + thisModel.barMagnet.location.get().toString() );
        } );
        this.barMagnet.strength.addObserver( function () {
            logger.info( "barMagnet.strength=" + thisModel.barMagnet.strength.get() );
        } );
        this.barMagnet.orientation.addObserver( function () {
            logger.info( "barMagnet.orientation=" + thisModel.barMagnet.orientation.get() );
        } );
    }

    FaradayModel.prototype.reset = function() {
        this.barMagnet.reset();
    };

    return FaradayModel;
} );
