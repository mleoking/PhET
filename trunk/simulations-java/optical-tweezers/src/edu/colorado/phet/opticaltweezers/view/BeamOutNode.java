/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.opticaltweezers.util.ColorUtils;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * BeamOutNode is the visual representation of the portion of the 
 * laser beam that is coming out of the microscope objective.
 * This part of the beam is shaped by the objective and shows the 
 * gradient field.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeamOutNode extends PhetPNode {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PPath _pathNode; // the beam node
    private Color _laserColor;
    
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param width width in view coordinates
     * @param height height in view coordinates
     * @param wavelength laser wavelength, in nm
     * @param alpha alpha component of the beam color
     */
    public BeamOutNode( double width, double height, double wavelength, int alpha ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        Rectangle2D r = new Rectangle2D.Double( 0, 0, width, height );

        //XXX use constructive geometry to create beam waist
        
        _pathNode = new PPath();
        _pathNode.setPathTo( r );
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
