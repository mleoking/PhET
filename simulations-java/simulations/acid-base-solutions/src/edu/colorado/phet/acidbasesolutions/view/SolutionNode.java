/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.model.Solution;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PClip;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * SolutionNode represents the solution in the beaker.
 * It plays a secondary role, used as a clipping path for the particles shown
 * in the ratio views.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SolutionNode extends PComposite {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color COLOR = ABSConstants.H2O_COLOR;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final ParticlesNode _particlesNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public SolutionNode( Solution solution, PDimension size ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        PClip liquidNode = new PClip(); // clip particles to liquid
        liquidNode.setPathTo( new Rectangle2D.Double( 0, 0, size.getWidth(), size.getHeight() ) );
        liquidNode.setStroke( null );
        liquidNode.setPaint( COLOR );
        addChild( liquidNode );
        
        PBounds containerBounds = new PBounds( 0, 0, size.getWidth(), size.getHeight() );
        _particlesNode = new ParticlesNode( solution, containerBounds );
        liquidNode.addChild( _particlesNode ); // clip particles to liquid
    }
    
    public void cleanup() {
        _particlesNode.cleanup();
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setParticlesVisible( boolean visible ) {
        _particlesNode.setVisible( visible );
    }
    
    public boolean isParticlesVisible() {
        return _particlesNode.getVisible();
    }
    
    // for attaching developer control panel
    public ParticlesNode getParticlesNode() {
        return _particlesNode;
    }
}
