/* Copyright 2007, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.opticaltweezers.util.ColorUtils;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;


public class BeamInNode extends PhetPNode {

    private PPath _pathNode;
    private Color _laserColor;
    
    public BeamInNode( double width, double height, double wavelength, int alpha ) {
        super();
        
        _pathNode = new PPath();
        _pathNode.setPathTo( new Rectangle2D.Double( 0, 0, width, height ) );
        Color wavelengthColor = VisibleColor.wavelengthToColor( wavelength );
        _laserColor = ColorUtils.addAlpha( wavelengthColor, alpha );
        _pathNode.setPaint( _laserColor );
        _pathNode.setStroke( null );
        addChild( _pathNode );
    }
    
    public void setAlpha( int alpha ) {
        Color newColor = ColorUtils.addAlpha( _laserColor, alpha );
        _pathNode.setPaint( newColor );
    }
}
