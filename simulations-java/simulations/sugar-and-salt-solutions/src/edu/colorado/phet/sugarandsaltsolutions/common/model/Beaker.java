// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * Physical model for the beaker
 *
 * @author Sam Reid
 */
public class Beaker {
    private final double x;
    private final double y;
    private final double width;
    private final double height;
    private final float wallWidth = 0.01f;

    public Beaker( double x, double y, double width, double height ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    //Determines the model shape of the walls of the beaker that can be used to render it in the view
    public Shape getWallShape() {
        //Stroke (in model coordinates) that will be used to create the walls
        BasicStroke wallStroke = new BasicStroke( wallWidth );

        //Create a GeneralPath representing the walls as a U-shape, starting from the top left
        Shape wallShape = wallStroke.createStrokedShape( new DoubleGeneralPath( x, y + height ) {{
            lineTo( x, y );
            lineTo( x + width, y );
            lineTo( x + width, y + height );
        }}.getGeneralPath() );

        //Since the stroke goes on both sides of the line, subtract out the main area so that the water won't overlap with the edges
        return new Area( wallShape ) {{subtract( new Area( new Rectangle2D.Double( x, y, width, height ) ) );}};
    }
}
