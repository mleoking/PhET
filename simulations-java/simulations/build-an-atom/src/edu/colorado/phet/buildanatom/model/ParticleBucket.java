// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.model;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Class that defines a model of a bucket that can hold particles.
 *
 * IMPORTANT NOTE: The shapes that are created and that comprise the
 * bucket are set up such that the point (0,0) is in the center of the
 * bucket's hole.
 *
 * @author John Blanco
 */
public class ParticleBucket extends Bucket {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    // Particles that are in this bucket.
    private final ArrayList<SphericalParticle> containedParticles = new ArrayList<SphericalParticle>();

    // Radius of particles that will be going into this bucket.  This is
    // used for placing particles.
    private final double particleRadius;

    // Proportion of the width of the bucket to use for particle placement.
    // A value of 1 means that the entire bucket should be used.
    private final double usableWidthProportion;

    // Offset, in picometers, of the particles in the y direction.  This helps
    // to avoid the appearance of particles floating in the bucket.
    private final double yOffset;

    // Listener for events where the user grabs the particle, which is interpreted as
    // removal from the bucket.
    private final SphericalParticle.Adapter particleRemovalListener = new SphericalParticle.Adapter() {
        @Override
        public void grabbedByUser( final SphericalParticle particle ) {
            // The user has picked up this particle, so we assume
            // that they want to remove it.
            assert containedParticles.contains( particle );
            containedParticles.remove( particle );
            particle.removeListener( this );

            final Point2D initialPosition = particle.getDestination();
            particle.addPositionListener( new SimpleObserver() {
                public void update() {
                    if ( initialPosition.distance( particle.getDestination() ) > particle.getRadius() * 5 ){
                        relayoutBucketParticles();
                        particle.removePositionListener( this );
                    }
                }
            } );
        }
    };

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    /**
     * Constructor.
     */
    public ParticleBucket( Point2D position, Dimension2D size, Color baseColor, String caption, double particleRadius,
            double usableWidthProportion, double yOffset ) {
        super( position, size, baseColor, caption );
        this.particleRadius = particleRadius;
        this.usableWidthProportion = usableWidthProportion;
        this.yOffset = yOffset;
    }

    /**
     * Constructor that assumes that the entire width of the bucket should be
     * used for particle placement.
     */
    public ParticleBucket( Point2D position, Dimension2D size, Color baseColor, String caption, double particleRadius ) {
        this(position, size, baseColor, caption, particleRadius, 1, 0);
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    public void reset() {
        containedParticles.clear();
    }

    public void removeParticle( SphericalParticle particle ) {
        if (!containedParticles.contains( particle )){
           System.err.println( getClass().getName() + " - Error: Particle not here, can't remove." );
        }
        assert containedParticles.contains( particle );
        containedParticles.remove( particle );
        particle.removeListener( particleRemovalListener );
    }

    public void addParticle( final SphericalParticle particle, boolean moveImmediately ) {
        // Determine an open location in the bucket.
        Point2D freeParticleLocation = getFirstOpenLocation();

        // Move the particle.
        if ( moveImmediately ) {
            // Move the particle instantaneously to the destination.
            particle.setPositionAndDestination( freeParticleLocation );
        }
        else {
            // Set the destination and let the particle find its own way.
            particle.setDestination( freeParticleLocation );
        }

        // Listen for when the user removes this particle from the bucket.
        particle.addListener( particleRemovalListener );

        containedParticles.add( particle );
    }

    public boolean containsParticle( SphericalParticle particle ){
        return containedParticles.contains( particle );
    }

    public ArrayList<SphericalParticle> getParticleList(){
        return containedParticles;
    }

    protected double getParticleRadius() {
        return particleRadius;
    }

    /*
     * Returns the first location in a bucket that a particle could be placed without overlapping another particle.
     * Locations may be above (+y) other particles, in order to create a stacking effect.
     */
    private Point2D getFirstOpenLocation() {
        Point2D openLocation = new Point2D.Double();
        double placeableWidth = holeShape.getBounds2D().getWidth() * usableWidthProportion - 2 * particleRadius;
        double offsetFromBucketEdge = (holeShape.getBounds2D().getWidth() - placeableWidth) / 2 + particleRadius;
        int numParticlesInLayer = (int) Math.floor( placeableWidth / ( particleRadius * 2 ) );
        int row = 0;
        int positionInLayer = 0;
        boolean found = false;
        while ( !found ) {
            double yPos = getYPositionForRow( row );
            double xPos = getPosition().getX() - holeShape.getBounds2D().getWidth() / 2 + offsetFromBucketEdge + positionInLayer * 2 * particleRadius;
            if ( isPositionOpen( xPos, yPos ) ) {
                // We found a location that is open.
                openLocation.setLocation( xPos, yPos );
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
                    offsetFromBucketEdge += particleRadius;
                    if ( numParticlesInLayer == 0 ) {
                        // This algorithm doesn't handle the situation where
                        // more particles are added than can be stacked into
                        // a pyramid of the needed size, but so far it hasn't
                        // needed to.  If this requirement changes, the
                        // algorithm will need to change too.
                        numParticlesInLayer = 1;
                        offsetFromBucketEdge -= particleRadius;
                    }
                }
            }
        }
        return openLocation;
    }

    /**
     *
     * @param row 0 for the y=0 row, 1 for the next row, etc.
     * @return
     */
    private double getYPositionForRow( int row ) {
        return getPosition().getY() + row * particleRadius * 2 * 0.866 + yOffset;
    }

    private void relayoutBucketParticles() {
        ArrayList<SphericalParticle> copyOfContainedParticles = new ArrayList<SphericalParticle>( containedParticles );
        for ( SphericalParticle containedParticle : copyOfContainedParticles) {
            if (isDangling(containedParticle)){
                removeParticle( containedParticle );
                addParticle( containedParticle, false);
                relayoutBucketParticles();
            }
        }
    }

    /**
     * Determine whether a particle is 'dangling', i.e. hanging above an open
     * space in the stack of particles.  Dangling particles should fall.
     */
    private boolean isDangling( SphericalParticle particle ) {
        boolean onBottomRow = particle.getDestination().getY() == getYPositionForRow( 0 );
        return !onBottomRow && countSupportingParticles( particle ) < 2;
    }

    private int countSupportingParticles( SphericalParticle p ) {
        int count =0 ;
        for ( SphericalParticle particle : containedParticles ) {
            if ( particle != p &&//not ourself
                 particle.getDestination().getY() < p.getDestination().getY() && //must be in a lower layer
                 particle.getDestination().distance( p.getDestination() ) < p.getRadius() * 3 ) {
                count++;
            }
        }
        return count;
    }

    /**
     * Determine whether the given particle position is open (i.e.
     * unoccupied) in the bucket.
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isPositionOpen( double x, double y ) {
        boolean positionOpen = true;
        for ( SphericalParticle particle : containedParticles ) {
            Point2D position = particle.getDestination();
            if ( position.getX() == x && position.getY() == y ) {
                positionOpen = false;
                break;
            }
        }
        return positionOpen;
    }
}
