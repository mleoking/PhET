// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticalquantumcontrol.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;


/**
 * Mirror is the graphical representation of a mirror.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Mirror extends PhetShapeGraphic {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int WIDTH = 320;
    private static final int HEIGHT = 50;
    private static final Color COLOR = Color.LIGHT_GRAY;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component
     */
    public Mirror( Component component ) {
        super( component);
       
        setIgnoreMouse( true );
        
        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        // Mirror shape, with concave part facing up.
        GeneralPath path = new GeneralPath();
        path.moveTo( 0, 0 );
        path.lineTo( 0, HEIGHT );
        path.lineTo( WIDTH, HEIGHT );
        path.lineTo( WIDTH, 0 );
        path.quadTo( WIDTH/2, HEIGHT, 0, 0 );
        path.closePath();
        
        setShape( path );
        setColor( COLOR );
    }
}
