// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.view;

import java.awt.BasicStroke;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.beerslawlab.model.ShakerParticle;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.umd.cs.piccolo.nodes.PPath;

//TODO look at duplication with PrecipitateParticleNode

/**
 * Visual representation of a solid solute particle exiting the shaker.
 * We use the same representation for all solutes, but vary the size and orientation.
 * Origin is at the center of the particle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ShakerParticleNode extends PPath {

    private static final float STROKE_WIDTH = 1f;

    private final ShakerParticle particle;
    private final VoidFunction1<ImmutableVector2D> locationObserver;

    public ShakerParticleNode( ShakerParticle particle ) {

        this.particle = particle;

        setPaint( particle.getColor() );
        setStrokePaint( particle.getColor().darker() );
        setStroke( new BasicStroke( STROKE_WIDTH ) );
        setPathTo( new Rectangle2D.Double( -particle.getSize() / 2, -particle.getSize() / 2, particle.getSize(), particle.getSize() ) ); // square
        setRotation( particle.getOrientation() );

        locationObserver = new VoidFunction1<ImmutableVector2D>() {
            public void apply( ImmutableVector2D location ) {
                setOffset( location.toPoint2D() );
            }
        };
        particle.addLocationObserver( locationObserver );
    }

    public void cleanup() {
        particle.removeLocationObserver( locationObserver );
    }
}
