// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.view;

import java.awt.BasicStroke;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.beerslawlab.concentration.model.PrecipitateParticle;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Visual representation of a precipitate particle.
 * We use the same representation for all solutes, but vary the size and orientation.
 * Origin is at the center of the particle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class PrecipitateParticleNode extends PPath {

    private static final float STROKE_WIDTH = 1f;

    public PrecipitateParticleNode( PrecipitateParticle particle ) {
        setPaint( particle.getColor() );
        setStrokePaint( particle.getColor().darker() );
        setStroke( new BasicStroke( STROKE_WIDTH ) );
        setPathTo( new Rectangle2D.Double( -particle.getSize() / 2, -particle.getSize() / 2, particle.getSize(), particle.getSize() ) ); // square
        setRotation( particle.getOrientation() );
        setOffset( particle.getLocation().getX(), particle.getLocation().getY() - STROKE_WIDTH ); // account for stroke width so there's no overlap with bottom of beaker
    }
}
