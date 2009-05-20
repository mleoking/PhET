/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.beaker;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PClip;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * SolutionNode is the macroscopic view of the solution in the beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class SolutionNode extends PComposite {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color COLOR = ABSConstants.H2O_COLOR;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public SolutionNode( PDimension size ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        PClip solutionNode = new PClip(); // clip particles to liquid
        solutionNode.setPathTo( new Rectangle2D.Double( 0, 0, size.getWidth(), size.getHeight() ) );
        solutionNode.setStroke( null );
        solutionNode.setPaint( COLOR );
        addChild( solutionNode );
    }
}
