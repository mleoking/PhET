/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.shaper.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;


/**
 * DiffractionGrating
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DiffractionGrating extends PhetShapeGraphic {

    private static final float GRATING_WIDTH = 100f;
    private static final float GRATING_HEIGHT = 15f;
    private static final int NUMBER_OF_TEETH = 20;
    private static final float TOOTH_HEIGHT = 3f;
    private static final Color COLOR = Color.LIGHT_GRAY;
    
    public DiffractionGrating( Component component ) {
        super( component );
        
        setIgnoreMouse( true );
        
        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

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
        setColor( COLOR );
        setBorderColor( Color.BLACK );
        setStroke( new BasicStroke( 1f ) );
    }
}
