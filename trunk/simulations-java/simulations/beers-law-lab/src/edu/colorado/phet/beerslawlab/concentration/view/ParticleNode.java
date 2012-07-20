// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.view;

import java.awt.BasicStroke;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.beerslawlab.concentration.model.PrecipitateParticle;
import edu.colorado.phet.beerslawlab.concentration.model.ShakerParticle;
import edu.colorado.phet.beerslawlab.concentration.model.SoluteParticle;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Base class for all particles.
 * Origin is at the center of the particle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
abstract class ParticleNode extends PPath {

    private static final float STROKE_WIDTH = 1f;

    public ParticleNode( SoluteParticle particle ) {
        setPaint( particle.color );
        setStrokePaint( particle.color.darker() );
        setStroke( new BasicStroke( STROKE_WIDTH ) );
        setPathTo( new Rectangle2D.Double( -particle.size / 2, -particle.size / 2, particle.size, particle.size ) ); // square
        setRotation( particle.orientation );
        setOffset( particle.location.get().getX(), particle.location.get().getY() - STROKE_WIDTH ); // account for stroke width so there's no overlap with bottom of beaker
    }

    /**
     * A precipitate particle. These particles don't move, they appear on the bottom of the beaker.
     * The class exists solely to constrain the constructor to a specific type of particle.
     */
    static class PrecipitateParticleNode extends ParticleNode {
        public PrecipitateParticleNode( PrecipitateParticle particle ) {
            super( particle );
        }
    }

    /**
     * A solid solute particle exiting the shaker. These particles move.
     */
    static class ShakerParticleNode extends ParticleNode {

        private final ShakerParticle particle;
        private final VoidFunction1<Vector2D> locationObserver;

        public ShakerParticleNode( ShakerParticle particle ) {
            super( particle );

            this.particle = particle;

            // move to particle's location
            locationObserver = new VoidFunction1<Vector2D>() {
                public void apply( Vector2D location ) {
                    setOffset( location.toPoint2D() );
                }
            };
            particle.location.addObserver( locationObserver );
        }

        public void cleanup() {
            particle.location.removeObserver( locationObserver );
        }
    }
}
