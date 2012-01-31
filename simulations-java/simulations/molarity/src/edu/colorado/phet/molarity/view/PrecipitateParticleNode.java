// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.molarity.view;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.molarity.model.Solute;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Visual representation of a precipitate particle.
 * We use the same basic representation for all solutes, but vary the color, size and orientation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PrecipitateParticleNode extends PPath {

    public PrecipitateParticleNode( Solute solute ) {
        setPaint( solute.particleColor );
        setStrokePaint( solute.particleColor.darker() );
        setPathTo( new Rectangle2D.Double( 0, 0, solute.particleSize, solute.particleSize ) ); // square
        setRotation( Math.random() * 2 * Math.PI );
    }
}
