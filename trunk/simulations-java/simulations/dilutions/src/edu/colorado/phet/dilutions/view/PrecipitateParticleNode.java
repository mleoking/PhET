// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.view;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.dilutions.model.Solute;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Visual representation of a precipitate particle.
 * We use the same representation for all solutes, but vary the size and orientation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PrecipitateParticleNode extends PPath {

    public PrecipitateParticleNode( Solute solute ) {
        setPaint( solute.precipitateColor );
        setStrokePaint( solute.precipitateColor.darker() );
        setPathTo( new Rectangle2D.Double( 0, 0, 5, 5 ) ); //TODO make this look like a grain of sand (whatever that looks like...)
        setScale( solute.precipitateScale );
        setRotation( Math.random() * 2 * Math.PI );
    }
}
