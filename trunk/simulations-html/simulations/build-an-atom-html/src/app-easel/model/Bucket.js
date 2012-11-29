// Copyright 2002-2012, University of Colorado
define( [ 'underscore' ], function ( _ ) {

    function Bucket( xPos, yPos, width, particleRadius, labelText ) {
        this.x = xPos;
        this.y = yPos;
        this.width = width;
        this.particleRadius = particleRadius;
        this.labelText = labelText;
        this.particles = [];
    }

    Bucket.prototype.addParticle = function ( particle ) {

        var maxAcross = this.width / ( this.particleRadius * 2 ) - 1;
        var xPos = ( this.x - this.width / 2 ) + ( this.particleRadius ) + _.random( 0, maxAcross ) * this.particleRadius * 2;

        particle.setLocation( {x:xPos, y:this.y } );
        this.particles.push( particle );
        var self = this;
        particle.events.one( 'particleGrabbed', function () {
            self.particles = _.without( self.particles, particle );
            console.log( "particle removed from bucket" );
        } )
    }

    return Bucket;
} );
