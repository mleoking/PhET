// Copyright 2002-2012, University of Colorado
define( [
            'underscore',
            'easel',
            'common/Point2D',
            'common/DragHandler'
        ], function ( _, Easel, Point2D, DragHandler ) {

    //-------------------------------------------------------------------------
    // Constants
    //-------------------------------------------------------------------------

    var TOUCH_INFLATION_MULTIPLIER = 3;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    function ParticleView() {
        this.initialize.apply( this, arguments );
    }

    _.extend( ParticleView.prototype, Easel.Shape.prototype );

    ParticleView.prototype.initialize = function ( particle, mvt ) {

        // Call super constructor.
        Easel.Shape.prototype.initialize.call( this );

        // Set up fields.
        this.particle = particle;
        this.mvt = mvt;
        this.drawRadius = mvt.modelToView( particle.radius );

        // Perform initial creation of the shape.
        this.render();

        // Position the shape.
        var centerInViewSpace = mvt.modelToView( new Point2D( particle.x, particle.y ) );
        this.x = centerInViewSpace.x;
        this.y = centerInViewSpace.y;

        // Set up event handlers.
        var self = this;
        DragHandler.register( this,
                              function ( point ) {
                                  particle.setLocation( mvt.viewToModel( point ) );
                              },
                              function ( pressEvent ) {

                                  pressEvent.onMouseUp = function () {
                                      particle.setUserControlled( false );
                                  };

                                  particle.setUserControlled( true );
                              } );

        particle.events.on( 'locationChange', function () {
            var newLocation = mvt.modelToView( new Point2D( particle.x, particle.y ) );
            self.x = newLocation.x;
            self.y = newLocation.y;
        } );

        particle.events.on( 'userGrabbed', function () {
            self.drawRadius = self.mvt.modelToView( self.particle.radius ) * TOUCH_INFLATION_MULTIPLIER;
            self.render();
        } );

        particle.events.on( 'userReleased', function () {
            self.drawRadius = self.mvt.modelToView( self.particle.radius );
            self.render();
        } );
    }

    ParticleView.prototype.render = function () {
        this.graphics.clear();
        this.graphics
                .beginStroke( "black" )
                .beginRadialGradientFill( [this.particle.color, "white"],
                                          [0.5, 1],
                                          0,
                                          0,
                                          this.drawRadius * 2,
                                          -this.drawRadius / 3,
                                          -this.drawRadius / 3,
                                          this.drawRadius / 8 )
                .setStrokeStyle( 1 )
                .drawCircle( 0, 0, this.drawRadius )
                .endFill();
    }

    return ParticleView;
} );
