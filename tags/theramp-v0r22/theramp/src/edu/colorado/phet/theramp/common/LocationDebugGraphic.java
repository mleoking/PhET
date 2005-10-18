/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Feb 16, 2005
 * Time: 8:49:02 AM
 * Copyright (c) Feb 16, 2005 by Sam Reid
 */

public class LocationDebugGraphic extends CompositePhetGraphic {
    public LocationDebugGraphic( Component component, int size ) {
        super( component );
        Rectangle rect = new Rectangle( -size / 2, -size / 2, size, size );
        PhetShapeGraphic shapeGraphic = new PhetShapeGraphic( component, rect, Color.white, new BasicStroke( 1 ), Color.black );
        addGraphic( shapeGraphic );
    }

}
