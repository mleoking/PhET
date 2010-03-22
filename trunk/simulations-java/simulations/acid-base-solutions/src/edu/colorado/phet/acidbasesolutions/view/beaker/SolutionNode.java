/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.beaker;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.acidbasesolutions.AABSColors;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * SolutionNode is the macroscopic view of the solution in the beaker.
 * Implemented as a PClip so that we can clip things to the bounds of the solution.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class SolutionNode extends PPath {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color COLOR = AABSColors.H2O;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public SolutionNode( PDimension size ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        setPathTo( new Rectangle2D.Double( 0, 0, size.getWidth(), size.getHeight() ) );
        setStroke( null );
        setPaint( COLOR );
    }
}
