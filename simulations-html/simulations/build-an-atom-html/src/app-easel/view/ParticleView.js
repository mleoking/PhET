// Copyright 2002-2012, University of Colorado
define( [
            'underscore',
            'easel',
            'common/Point2D',
            'common/DragHandler'
        ], function ( _, Easel, Point2D, DragHandler ) {

    // Constructor

    function ParticleView() {
        this.initialize.apply( this, arguments );
    }

    _.extend( ParticleView.prototype, Easel.Shape.prototype );

    ParticleView.prototype.initialize = function ( particle, mvt ) {
        Easel.Shape.prototype.initialize.call( this );

        this.particle = particle;
        var radius = particle.radius;

        this.graphics
                .beginStroke( "black" )
//                .beginFill( particle.color )
//                .beginRadialGradientFill( [ "white, black"] , [0, 1], 0 , 0 , 0, 0, 0, 0 )
//                .beginRadialGradientFill(["red", "blue"], [1, 0], -10, -10, 15, 10, 10, 10) // Working hard-coded gradient.
                .beginRadialGradientFill([particle.color, "white"], [0.5, 1], 0, 0, radius * 2, -radius / 3, -radius / 3, radius / 8 )
                .setStrokeStyle( 1 )
                .drawCircle( 0, 0, radius )
                .endFill();

        var centerInViewSpace = mvt.modelToView( new Point2D( particle.x, particle.y ) );
        this.x = centerInViewSpace.x;
        this.y = centerInViewSpace.y;

        var self = this;

        DragHandler.register( this, function ( point ) {
            particle.setLocation( mvt.viewToModel( point ) );
        }, function( pressEvent ){

            pressEvent.onMouseUp = function(){
              particle.setUserControlled( false );
            };

            particle.setUserControlled( true );
        });

        particle.events.on( 'locationChange', function () {
            var newLocation = mvt.modelToView( new Point2D( particle.x, particle.y ) );
            self.x = newLocation.x;
            self.y = newLocation.y;
        } );
    };

    return ParticleView;
} );
