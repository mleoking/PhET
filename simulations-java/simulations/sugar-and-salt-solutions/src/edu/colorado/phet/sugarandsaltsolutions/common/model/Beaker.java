// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
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
    private final double y;//the y-location of the base of the beaker
    private final double width;
    private final double height;
    private final double depth;//dimension of the beaker in the z-direction (into the screen)
    private final float wallWidth = 0.0025f;

    public Beaker( double x, double y, double width, double height, double depth ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    //Gets the y-location of the base of the beaker
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
        return new Area( wallShape ) {{subtract( new Area( toRectangle() ) );}};
    }

    //Returns a rectangle of the bounds of the beaker
    private Rectangle2D.Double toRectangle() {
        return new Rectangle2D.Double( x, y, width, height );
    }

    //Get the Shape of the fluid given the volume
    public Shape getFluidShape( double volume ) {
        return new Rectangle2D.Double( x, y, width, getHeightForVolume( volume ) );
    }

    // Rearrange the equation "Volume = width * height * depth"  To solve for height, assumes a square tank like a fish tank
    public double getHeightForVolume( double volume ) {
        return volume / width / depth;
    }

    //Gets the bottom right corner for attaching the output faucet
    public Point2D getOutputFaucetAttachmentPoint() {
        return new Point2D.Double( x + width, y );
    }

    //Determine how much water could this beaker hold
    public double getMaxFluidVolume() {
        return width * height * depth;//Rectangular like a fish tank
    }

    //Get the center of the empty beaker
    public double getCenterX() {
        return toRectangle().getCenterX();
    }

    //Get the top of the empty beaker
    public double getTopY() {
        return y + height;
    }

    //Get the height of the empty beaker
    public double getHeight() {
        return height;
    }

    //Gets the leftmost x component of the water-containing part of the beake
    public double getX() {
        return x;
    }

    //Gets the vertical center of the beaker
    public double getCenterY() {
        return toRectangle().getCenterY();
    }

    //Gets the width of the walls (edges) of the container
    public double getWallWidth() {
        return wallWidth;
    }

    public double getMaxX() {
        return x + width;
    }

    public Line2D.Double getLeftWall() {
        return new Line2D.Double( x, y, x, y + height );
    }

    public Line2D.Double getRightWall() {
        return new Line2D.Double( getMaxX(), y, getMaxX(), y + height );
    }
}