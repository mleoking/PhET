// Copyright 2002-2012, University of Colorado
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

        // Create the textual readout of the element name.
        this.label = new Easel.Text( "Test", 'bold 36px Arial', 'red' );
        var self = this;

        atom.events.on( Atom.CONFIG_CHANGE_EVENT, function () {
            console.log( "Atom config change event received." );
            self.label.text = AtomIdentifier.getName( self.atom.getNumProtons() );
        } );

        this.addChild( this.label );
    };

    return AtomView;
} );
