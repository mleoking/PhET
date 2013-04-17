// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.phetgraphicsdemo.view;

import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * RoadGraphic is the graphical representation of a road.
 * The origin is at the lower-left corner of the road.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RoadGraphic extends PhetShapeGraphic {

    public RoadGraphic( Component component, int width ) {
        super( component );
        
        // Road shape, with (0,0) at lower-left corner.
        GeneralPath path = new GeneralPath();
        path.moveTo( 0, 0 );
        path.lineTo( width, 0 );
        path.lineTo( width + 20, -20 );
        path.lineTo( 20, -20 );
        path.closePath();
        
        setShape( path );
        setPaint( Color.BLACK );
        
        RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        setRenderingHints( hints );
    }

}
