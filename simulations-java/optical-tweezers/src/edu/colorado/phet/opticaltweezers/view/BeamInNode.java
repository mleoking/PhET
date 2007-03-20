/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.opticaltweezers.util.ColorUtils;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * BeamInNode is the visual representation of the portion of the 
 * laser beam that is coming into the microscope objective.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeamInNode extends PhetPNode {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PPath _pathNode; // the beam node
    private Color _laserColor;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param width width in view coordinates
     * @param height height in view coordinates
     * @param wavelength laser wavelength, in nm
     * @param alpha alpha component of the beam color
     */
    public BeamInNode( double width, double height, double wavelength, int alpha ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        _pathNode = new PPath();
        _pathNode.setPathTo( new Rectangle2D.Double( 0, 0, width, height ) );
        Color wavelengthColor = VisibleColor.wavelengthToColor( wavelength );
        _laserColor = ColorUtils.addAlpha( wavelengthColor, alpha );
        _pathNode.setPaint( _laserColor );
        _pathNode.setStroke( null );
        addChild( _pathNode );
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the alpha component of the beam color.
     * 
     * @param alpha 0-255
     */
    public void setAlpha( int alpha ) {
        assert( alpha >= 0 && alpha <= 255 );
        Color newColor = ColorUtils.addAlpha( _laserColor, alpha );
        _pathNode.setPaint( newColor );
    }
}
