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
        this.mvt = mvt;

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
        this.elementName.y = 50;
        this.addChild( this.elementName );
        this.stabilityIndicator = new Easel.Text( "", 'bold 26px Arial', 'black' );
        this.stabilityIndicator.y = 420;
        this.addChild( this.stabilityIndicator );
        this.ionIndicator = new Easel.Text( "", 'bold 26px Arial', 'black' );
        this.ionIndicator.x = 450;
        this.ionIndicator.y = 150;
        this.addChild( this.ionIndicator );

        // Update the textual indicators.
        var self = this;
        atom.events.on( Atom.CONFIG_CHANGE_EVENT, function () {

            // Update element name.
            self.elementName.text = AtomIdentifier.getName( self.atom.getNumProtons() );
            self.elementName.x = self.mvt.modelToView( new Point2D( 0, 0 ) ).x - self.elementName.getMeasuredWidth() / 2;

            // Update stability indicator.
            if ( self.atom.getNumProtons() == 0 ) {
                self.stabilityIndicator.text = "";
            }
            else if ( AtomIdentifier.isStable( self.atom.getNumProtons(), self.atom.getNumNeutrons() ) ) {
                self.stabilityIndicator.text = "Stable";
            }
            else {
                self.stabilityIndicator.text = "Unstable";
            }
            self.stabilityIndicator.x = self.mvt.modelToView( new Point2D( 0, 0 ) ).x - self.stabilityIndicator.getMeasuredWidth() / 2;

            // Update charge indicator.
            console.log( "self.atom.getCharge()" + self.atom.getCharge() );
            if ( self.atom.getNumProtons() == 0 ) {
                self.ionIndicator.text = "";
            }
            else if ( self.atom.getCharge() == 0 ) {
                self.ionIndicator.text = "Neutral Atom";
                self.ionIndicator.color = "black";
            }
            else if ( self.atom.getCharge() < 0 ) {
                self.ionIndicator.text = "-Ion";
                self.ionIndicator.color = "blue";
            }
            else {
                self.ionIndicator.text = "+Ion";
                self.ionIndicator.color = "red";
            }
        } );
    };

    return AtomView;
} );
