// Copyright 2002-2012, University of Colorado
define( [
        ], function () {

    //-------------------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------------------

    /**
     * @param x
     * @param y
     * @param color
     * @param radius
     * @param type
     * @constructor
     */
    function Particle( x, y, color, radius, type ) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.radius = radius;
        this.type = type;
        this.events = $( {} );
        this.userControlled = false;
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    Particle.prototype.setLocation = function ( point ) {
        this.x = point.x;
        this.y = point.y;
        this.events.trigger( 'locationChange' );
    };

    // Use this when setting user controlled so that event is triggered.
    Particle.prototype.setUserControlled = function ( userControlled, event ) {
        var isTouchEvent;

        if ( userControlled && !this.userControlled ){

            isTouchEvent = !(event.nativeEvent instanceof window.MouseEvent);

            this.events.trigger( 'userGrabbed', [ isTouchEvent ] );

        }
        else if ( this.userControlled  ){
            this.events.trigger( 'userReleased' );
        }
        this.userControlled = userControlled;
    }

    return Particle;
} );
