// Copyright 2002-2012, University of Colorado

/**
 * View representation of the atom.
 *
 * @author John Blanco
 */
define( [
            'easel',
            'common/Point2D',
            'common/AtomIdentifier',
            'model/Atom'
        ], function ( Easel, Point2D, AtomIdentifier, Atom ) {

    /**
     * @param xPos
     * @param yPos
     * @param mvt
     * @constructor
     */
    var AtomView = function ( atom, mvt ) {
        this.initialize( atom, mvt );
    };

    var p = AtomView.prototype = new Easel.Container(); // inherit from Container

    p.Container_initialize = p.initialize;

    p.initialize = function ( atom, mvt ) {
        this.Container_initialize();
        this.atom = atom;

        // Create the X where the nucleus goes.
        var x = new Easel.Shape();
        var sizeInPixels = mvt.modelToView( 20 );
        var center = mvt.modelToView( new Point2D( atom.xPos, atom.yPos ) );
        x.graphics.beginStroke( "orange" )
                .setStrokeStyle( 5 )
                .moveTo( center.x - sizeInPixels / 2, center.y - sizeInPixels / 2 )
                .lineTo( center.x + sizeInPixels / 2, center.y + sizeInPixels / 2 )
                .moveTo( center.x - sizeInPixels / 2, center.y + sizeInPixels / 2 )
                .lineTo( center.x + sizeInPixels / 2, center.y - sizeInPixels / 2 )
                .endStroke();
        this.addChild( x );

        // Create the textual readouts for element name, stability, and charge.
        this.elementName = new Easel.Text( "", 'bold 36px Arial', 'red' );
        this.addChild( this.elementName );
        this.stabilityIndicator = new Easel.Text( "", 'bold 28px Arial', 'black' );
        this.stabilityIndicator.y = 50;
        this.addChild( this.stabilityIndicator );
        this.chargeIndicator = new Easel.Text( "", 'bold 28px Arial', 'black' );
        this.chargeIndicator.y = 100;
        this.addChild( this.chargeIndicator );

        // Update the textual indicators.
        var self = this;
        atom.events.on( Atom.CONFIG_CHANGE_EVENT, function () {

            // Update element name.
            self.elementName.text = AtomIdentifier.getName( self.atom.getNumProtons() );

            // Update stability indicator.
            if ( self.atom.getNumProtons() == 0 ){
                self.stabilityIndicator.text = "";
            }
            else if ( AtomIdentifier.isStable( self.atom.getNumProtons(), self.atom.getNumNeutrons() ) ){
                self.stabilityIndicator.text = "Stable";
            }
            else{
                self.stabilityIndicator.text = "Unstable";
            }

            // Update charge indicator.
            console.log( "self.atom.getCharge()" + self.atom.getCharge() );
            if ( self.atom.getCharge() == 0 ){
                self.chargeIndicator.text = "Neutral Atom";
            }
            else if ( self.atom.getCharge() < 0 ){
                self.chargeIndicator.text = "-Ion";
            }
            else{
                self.chargeIndicator.text = "+Ion";
            }
        } );
    };

    return AtomView;
} );
