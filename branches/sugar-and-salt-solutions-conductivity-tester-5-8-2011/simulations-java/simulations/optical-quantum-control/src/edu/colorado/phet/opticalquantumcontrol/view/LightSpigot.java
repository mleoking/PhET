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
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.opticalquantumcontrol.OQCConstants;


/**
 * LightSpigot is the place were light exists the "input pulse viewer"
 * and enters the "output pulse viewer".  It looks sort of like a nose
 * with one nostril.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class LightSpigot extends CompositePhetGraphic {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int WIDTH = 50;
    private static final int HEIGHT = 100;
    private static final Color SPIGOT_COLOR = OQCConstants.COMMON_GRAY;
    private static final Color HOLE_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component
     */
    public LightSpigot( Component component ) {

        /**
         * Path that defines the shape, (0,0) at top right.
         * 
         *              /|
         *             / |
         *            /  |
         *           /   |
         *          /    |
         *         +-----+
         */
        PhetShapeGraphic lightSpigot = new PhetShapeGraphic( component );
        GeneralPath path = new GeneralPath();
        path.moveTo( 0, 0 );
        path.lineTo( 0, HEIGHT );
        path.lineTo( -WIDTH, HEIGHT );
        path.closePath();
        lightSpigot.setShape( path );
        lightSpigot.setPaint( SPIGOT_COLOR );
        addGraphic( lightSpigot, 0 );
        lightSpigot.setLocation( 0, 0 );

        /**
         * Hole that light will pass through.
         * Places at the bottom of the triangle shape.
         */
        PhetShapeGraphic hole = new PhetShapeGraphic( component );
        hole.setShape( new Ellipse2D.Double( 0, 0, (.6 * WIDTH), (.1 * HEIGHT) ) );
        hole.setPaint( HOLE_COLOR );
        addGraphic( hole, 1 );
        hole.centerRegistrationPoint();
        hole.setLocation( (int) -(.4 * WIDTH), (int) (.9 * HEIGHT) );
    }
}
