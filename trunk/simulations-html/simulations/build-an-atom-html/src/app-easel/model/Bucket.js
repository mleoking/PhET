// Copyright 2002-2012, University of Colorado
define( [ 'underscore' ], function ( _ ) {

    function Bucket( xPos, yPos, width, labelText ) {
        this.x = xPos;
        this.y = yPos;
        this.width = width;
        this.labelText = labelText;
        this.particles = [];
    }

    Bucket.prototype.addParticle = function ( particle ){
        particle.setLocation( {x: this.x, y: this.y });
        this.particles.push( particle );
        var self = this;
        particle.events.one('particleGrabbed', function(){
            self.particles = _.without( self.particles, particle );
            console.log( "particle removed from bucket" );
        })
    }

    return Bucket;
} );
