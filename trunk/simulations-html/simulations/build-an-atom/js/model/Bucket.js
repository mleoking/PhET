// Copyright 2002-2012, University of Colorado
define( [
            'underscore',
            'common/Utils'
        ], function ( _, Utils ) {

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

    Bucket.prototype.addParticleFirstOpen = function ( particle ) {
        particle.setLocation( this.getFirstOpenLocation() );
        this.particles.push( particle );
        var self = this;
        particle.events.one( 'userGrabbed', function () {
            self.removeParticle( particle );
        } )
    }

    Bucket.prototype.addParticleNearestOpen = function ( particle ) {
        particle.setLocation( this.getNearestOpenLocation( particle.x, particle.y ) );
        this.particles.push( particle );
        var self = this;
        particle.events.one( 'userGrabbed', function () {
            self.removeParticle( particle );
        } )
    }

    Bucket.prototype.removeParticle = function ( particle ) {
        if ( this.particles.indexOf( particle ) == -1 ) {
            console.log( "Error: Attempt to remove particle not contained in bucket, ignoring." );
            return;
        }
        this.particles = _.without( this.particles, particle );
//        this.relayoutBucketParticles();
    }

    Bucket.prototype.isPositionOpen = function ( x, y ) {
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

    Bucket.prototype.getFirstOpenLocation = function () {
        var openLocation = {x:0, y:0};
        var usableWidth = this.width - 2 * this.particleRadius;
        var offsetFromBucketEdge = this.particleRadius * 2;
        var numParticlesInLayer = Math.floor( usableWidth / ( this.particleRadius * 2 ) );
        var row = 0;
        var positionInLayer = 0;
        var found = false;
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

    Bucket.prototype.isPositionOpen = function ( x, y ) {
        var positionOpen = true;
        _.each( this.particles, function ( particle ) {
            if ( x === particle.x && y === particle.y ) {
                positionOpen = false;
            }
        } );
        return positionOpen;
    }

    Bucket.prototype.getLayerForYPosition = function ( yPosition ) {
        return Math.abs( Math.round( ( yPosition - ( this.y + this.yOffset ) ) / ( this.particleRadius * 2 * 0.866 ) ) );
    }

    /*
     * Get the nearest open location to the provided current location.  This
     * is used for particle stacking.
     *
     * @param location
     * @return
     */
    Bucket.prototype.getNearestOpenLocation = function ( xPos, yPos ) {
        // Determine the highest occupied layer.  The bottom layer is 0.
        var highestOccupiedLayer = 0;
        var self = this;
        _.each( this.particles, function ( particle ) {
            var layer = self.getLayerForYPosition( particle.y );
            if ( layer > highestOccupiedLayer ) {
                highestOccupiedLayer = layer;
            }
        } );

        // Make a list of all open locations in the occupied layers.
        var openLocations = [];
        var placeableWidth = this.width - 2 * this.particleRadius;
        var offsetFromBucketEdge = ( this.width - placeableWidth ) / 2 + this.particleRadius;
        var numParticlesInLayer = Math.floor( placeableWidth / ( this.particleRadius * 2 ) );

        // Loop, searching for open positions in the particle stack.
        for ( var layer = 0; layer <= highestOccupiedLayer + 1; layer++ ) {

            // Add all open locations in the current layer.
            for ( var positionInLayer = 0; positionInLayer < numParticlesInLayer; positionInLayer++ ) {
                var testYPos = this.getYPositionForLayer( layer );
                var testXPos = this.x - this.width / 2 + offsetFromBucketEdge + positionInLayer * 2 * this.particleRadius;
                if ( this.isPositionOpen( testXPos, testYPos ) ) {

                    // We found a location that is unoccupied.
                    if ( layer == 0 || this.countSupportingParticles( testXPos, testYPos ) == 2 ) {
                        // This is a valid open location.
                        openLocations.push( { x:testXPos, y:testYPos } );
                    }
                }
            }

            // Adjust variables for the next layer.
            numParticlesInLayer--;
            offsetFromBucketEdge += this.particleRadius;
            if ( numParticlesInLayer == 0 ) {
                // If the stacking pyramid is full, meaning that there are
                // no locations that are open within it, this algorithm
                // classifies the locations directly above the top of the
                // pyramid as being open.  This would result in a stack
                // of particles with a pyramid base.  So far, this hasn't
                // been a problem, but this limitation may limit
                // reusability of this algorithm.
                numParticlesInLayer = 1;
                offsetFromBucketEdge -= this.particleRadius;
            }
        }

        // Find the closest open location to the provided current location.
        // Only the X-component is used for this determination, because if
        // the Y-component is used the particles often appear to fall sideways
        // when released above the bucket, which just looks weird.
        var closestOpenLocation = openLocations[0] || {x:0, y:0};

        _.each( openLocations, function ( location ) {
            if ( Utils.distanceBetweenPoints( location.x, location.y, xPos, yPos ) < Utils.distanceBetweenPoints( closestOpenLocation.x, closestOpenLocation.y, xPos, yPos ) ) {
                // This location is closer.
                closestOpenLocation = location;
            }
        } )
        return closestOpenLocation;
    }

    Bucket.prototype.getYPositionForLayer = function ( layer ) {
        return this.y + this.yOffset - layer * this.particleRadius * 2 * 0.866;
    }

    /*
     * Determine whether a particle is 'dangling', i.e. hanging above an open
     * space in the stack of particles.  Dangling particles should fall.
     */
    Bucket.prototype.isDangling = function ( particle ) {
        var onBottomRow = particle.y === this.y + this.yOffset;
        return !onBottomRow && this.countSupportingParticles( particle ) < 2;
    }

    Bucket.prototype.countSupportingParticles = function ( xPos, yPos ) {
        var count = 0;
        for (var i = 0; i < this.particles.length; i++) {
            p = this.particles[i];
            if ( p.y > yPos && //must be in a lower layer (and larger y is further down on the page).
                 Utils.distanceBetweenPoints( p.x, p.y, xPos, yPos ) < this.particleRadius * 3 ) {
                // Must be a supporting particle.
                count++;
            }
        }
        return count;
    }

    Bucket.prototype.relayoutBucketParticles = function () {
        var self = this;
        _.each( this.particles, function ( particle ) {
            if ( self.isDangling( particle ) ) {
                self.removeParticle( particle );
                self.addParticleNearestOpen( particle );
                self.relayoutBucketParticles();
            }
        } );
    }

    return Bucket;
} );
