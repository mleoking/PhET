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

        var xPos = (this.x - this.width / 2) + Math.random() * this.width;

        particle.setLocation( {x: xPos, y: this.y });
        this.particles.push( particle );
        var self = this;
        particle.events.one('particleGrabbed', function(){
            self.particles = _.without( self.particles, particle );
            console.log( "particle removed from bucket" );
        })
    }

    return Bucket;
} );
