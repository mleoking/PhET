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
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;


/**
 * LightSpigot
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class LightSpigot extends CompositePhetGraphic {

    public LightSpigot( Component component ) {

        PhetShapeGraphic lightSpigot = new PhetShapeGraphic( component );
        GeneralPath path = new GeneralPath();
        path.moveTo( 0, 0 );
        path.lineTo( 0, 100 );
        path.lineTo( -50, 100 );
        path.closePath();
        lightSpigot.setShape( path );
        lightSpigot.setPaint( Color.LIGHT_GRAY );
        addGraphic( lightSpigot, 0 );
        lightSpigot.setLocation( 0, 0 );

        PhetShapeGraphic hole = new PhetShapeGraphic( component );
        hole.setShape( new Ellipse2D.Double( 0, 0, 30, 10 ) );
        hole.setPaint( Color.BLACK );
        addGraphic( hole, 1 );
        hole.centerRegistrationPoint();
        hole.setLocation( -20, 90 );
    }
}
