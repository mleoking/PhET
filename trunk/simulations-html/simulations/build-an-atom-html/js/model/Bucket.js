// Copyright 2002-2012, University of Colorado
define( [ 'underscore' ], function ( _ ) {

    function Bucket( xPos, yPos, width, particleRadius, color, labelText ) {
        this.x = xPos;
        this.y = yPos;
        this.width = width;
        this.particleRadius = particleRadius;
        this.labelText = labelText;
        this.particles = [];
        this.yOffset = this.particleRadius; // Empirically determined, for positioning particles inside the bucket.
        this.color = color;
    }

    Bucket.prototype.addParticle = function ( particle ) {
        var maxAcross = this.width / ( this.particleRadius * 2 ) - 1;
        var xPos = ( this.x - this.width / 2 ) + ( this.particleRadius ) + _.random( 0, maxAcross ) * this.particleRadius * 2;

        particle.setLocation( this.getFirstOpenLocation() );
        this.particles.push( particle );
        var self = this;
        particle.events.one( 'particleGrabbed', function () {
            self.particles = _.without( self.particles, particle );
        } )
    }

    Bucket.prototype.getFirstOpenLocation = function(){
        var openLocation = {x:0, y:0};
        var usableWidth = this.width - 2 * this.particleRadius;
        var offsetFromBucketEdge = this.particleRadius * 2;
        var numParticlesInLayer = Math.floor( usableWidth / ( this.particleRadius * 2 ) );
        var row = 0;
        var positionInLayer = 0;
        var  found = false;
        while ( !found ) {
            var yPos = this.getYPositionForLayer( row );
            var xPos = this.x - this.width / 2 + offsetFromBucketEdge + positionInLayer * 2 * this.particleRadius;
            if ( this.isPositionOpen( xPos, yPos ) ) {
                // We found a location that is open.
                openLocation.x = xPos;
                openLocation.y = yPos;
                found = true;
                continue;
            }
            else {
                positionInLayer++;
                if ( positionInLayer >= numParticlesInLayer ) {
                    // Move to the next layer.
                    row++;
                    positionInLayer = 0;
                    numParticlesInLayer--;
                    offsetFromBucketEdge += this.particleRadius;
                    if ( numParticlesInLayer == 0 ) {
                        // This algorithm doesn't handle the situation where
                        // more particles are added than can be stacked into
                        // a pyramid of the needed size, but so far it hasn't
                        // needed to.  If this requirement changes, the
                        // algorithm will need to change too.
                        numParticlesInLayer = 1;
                        offsetFromBucketEdge -= this.particleRadius;
                    }
                }
            }
        }
        return openLocation;
    }

    Bucket.prototype.getYPositionForLayer = function ( row ) {
        return this.y - row * this.particleRadius * 2 * 0.866 + this.yOffset;
    }

    Bucket.prototype.isPositionOpen = function( x, y ) {
        var positionOpen = true;
        for ( var i = 0; i < this.particles.length; i++ ) {
            var particle = this.particles[ i ];
            if ( particle.x == x && particle.y == y ) {
                positionOpen = false;
                break;
            }
        }
        return positionOpen;
    }


    return Bucket;
} );
