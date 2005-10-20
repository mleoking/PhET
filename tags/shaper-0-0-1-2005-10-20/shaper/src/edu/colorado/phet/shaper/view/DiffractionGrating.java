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

import java.awt.Color;
import java.awt.Component;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;


/**
 * DiffractionGrating
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DiffractionGrating extends PhetShapeGraphic {

    private static final double WIDTH = 100;
    private static final double HEIGHT = 15;
    private static final Color COLOR = Color.DARK_GRAY;
    
    public DiffractionGrating( Component component ) {
        super( component );
        
        setIgnoreMouse( true );
        
        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        Rectangle2D rectangle = new Rectangle2D.Double( 0, 0, WIDTH, HEIGHT );
        setShape( rectangle );
        setColor( COLOR );
    }
}
