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

        this.graphics
                .beginStroke( "black" )
                .beginFill( particle.color )
                .setStrokeStyle( 1 )
                .drawCircle( 0, 0, particle.radius )
                .endFill();

        var centerInViewSpace = mvt.modelToView( new Point2D( particle.x, particle.y ) );
        this.x = centerInViewSpace.x;
        this.y = centerInViewSpace.y;

        var self = this;

        DragHandler.register( this, function ( point ) {
            particle.setLocation( mvt.viewToModel( point ) );
        } );

        this.onMouseDown = function ( mouseEvent ){
            particle.setUserControlled( true );
            console.log( "mouse down, mousevent = " + mouseEvent );
        }

        this.onMouseUp = function ( mouseEvent ){
            particle.setUserControlled( false );
            console.log( "mouse up, mousevent = " + mouseEvent );
        }

        this.onMouseMove = function ( mouseEvent ){
            particle.setUserControlled( false );
            console.log( "mouse up, mousevent = " + mouseEvent );
        }

        var registeredPressFunction = this.onPress;
        this.onPress = function ( mouseEvent ){
            particle.setUserControlled( true );
            registeredPressFunction( mouseEvent );
        }

        this.onClick = function ( mouseEvent ){
            particle.setUserControlled( false );
        }

        particle.events.on( 'locationChange', function () {
            var newLocation = mvt.modelToView( new Point2D( particle.x, particle.y ) );
            self.x = newLocation.x;
            self.y = newLocation.y;
        } );
    };

    return ParticleView;
} );
