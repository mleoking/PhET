// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
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

    public double getY() {
        return y;
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

    public Shape getFluidShape( double volume ) {
        double area = width * width;//assumes a square "cylinder"
        double height = volume / area;
        return new Rectangle2D.Double( x, y, width, height );
    }

    //Gets the bottom right corner for attaching the output faucet
    public Point2D getOutputFaucetAttachmentPoint() {
        return new Point2D.Double( x + width, y );
    }

    //Determine how much water could this beaker hold
    public double getMaxFluidVolume() {
        return width * width * height;//Assumes a square cylinder
    }
}
