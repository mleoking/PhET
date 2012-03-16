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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;


/**
 * DiffractionGrating is the graphical representation of a diffraction grating.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DiffractionGrating extends PhetShapeGraphic {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final float GRATING_WIDTH = 100f;
    private static final float GRATING_HEIGHT = 15f;
    private static final float TOOTH_HEIGHT = 3f;
    private static final int NUMBER_OF_TEETH = 20; // determines the tooth width
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component
     */
    public DiffractionGrating( Component component ) {
        super( component );
        
        setIgnoreMouse( true );
        
        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        /* 
         * Path that defines the shape of the grating, "teeth" on top.
         * (0,0) is at the bottom left of the left-most tooth.
         * 
         *    /\/\/\/\/\/\/\/\/\/\/\/\
         *    |                       |
         *    |                       |
         *    +-----------------------+
         */
        float toothWidth = GRATING_WIDTH / NUMBER_OF_TEETH;
        GeneralPath path = new GeneralPath();
        path.moveTo( 0, 0 );
        int x = 0;
        for ( int i = 0; i < NUMBER_OF_TEETH; i++ ) {
            path.lineTo( x + toothWidth/2, -TOOTH_HEIGHT );
            path.lineTo( x + toothWidth, 0 );
            x += toothWidth;
        }
        path.lineTo( x, GRATING_HEIGHT );
        path.lineTo( 0, GRATING_HEIGHT );
        path.closePath();
        
        setShape( path );
        setColor( Color.LIGHT_GRAY );
        setBorderColor( Color.BLACK );
        setStroke( new BasicStroke( 1f ) );
    }
}
