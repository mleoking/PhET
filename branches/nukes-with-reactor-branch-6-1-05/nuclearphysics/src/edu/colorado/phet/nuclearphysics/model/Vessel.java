/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.nuclearphysics.controller.ControlledFissionModule;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Vessel
 * <p/>
 * The containment vessel for the reactor. It has channels for control rods.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Vessel {
    private Rectangle2D boundary;
    private double channelThickness = 100;
    private Rectangle2D[] rodChannels;

    /**
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public Vessel( double x, double y, double width, double height, int numChannels ) {
        rodChannels = new Rectangle2D[numChannels];
        boundary = new Rectangle2D.Double( x, y, width, height );
        for( int i = 0; i < rodChannels.length; i++ ) {
            int orientation = ControlledFissionModule.VERTICAL;
            if( orientation == ControlledFissionModule.VERTICAL ) {
                double spacing = getWidth() / ( numChannels + 1 );
                double channelX = getX() + spacing * ( i + 1 );
                double channelY = getY();
                rodChannels[i] = new Rectangle2D.Double( channelX - channelThickness / 2, channelY,
                                                         channelThickness, getHeight() );
            }
        }
    }

    //----------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------

    private Shape getShape() {
        return boundary;
    }

    public double getX() {
        return boundary.getX();
    }

    public double getY() {
        return boundary.getY();
    }

    public double getWidth() {
        return boundary.getWidth();
    }

    public double getHeight() {
        return boundary.getHeight();
    }

    public Rectangle2D[] getChannels() {
        return rodChannels;
    }

    public int getNumControlRodChannels() {
        return this.rodChannels.length;
    }

    public boolean contains( double x, double y ) {
        return getShape().contains( x, y );
    }

    public boolean contains( Point2D p ) {
        return getShape().contains( p.getX(), p.getY() );
    }

    public Rectangle2D getBounds() {
        return getShape().getBounds2D();
    }

    /**
     * Tells if a specified point lies in one of the channels
     *
     * @param p
     * @return
     */
    public boolean isInChannel( Point2D p ) {
        for( int i = 0; i < rodChannels.length; i++ ) {
            Rectangle2D rodChannel = rodChannels[i];
            if( rodChannel.contains( p ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tells if a specified shape intersects one of the channel
     *
     * @param shape
     * @return
     */
    public boolean intersectsChannel( Shape shape ) {
        return false;
    }

    /**
     * Returns an array of points laid out on a grid with a specified number of columns.
     * The grid is laid out so that the points are evenly distributed between the channels,
     * and the horizontal and vertical spacings are the same.
     *
     * @param nCols
     * @return
     */
    public Point2D[] getInitialNucleusLocations( int nCols ) {
        ArrayList locations = new ArrayList();

        // Determine the number of inter-channel areas where the nuclei will go,
        // and the number of columns in each
        int nInterChannelAreas = rodChannels.length + 1;
        int nColsPerArea = nCols / nInterChannelAreas;

        // Get the width of the area and the spacing between columns, the spacing between rows,
        // and the number of rows
        double areaWidth = ( this.getWidth() - ( rodChannels.length * channelThickness ) ) / nInterChannelAreas;
        double colSpacing = areaWidth / ( nColsPerArea + 1 );
        double rowSpacing = colSpacing;
        int nRows = (int)( getHeight() / rowSpacing ) - 1;

        // Now, lay out the columns
        for( int k = 0; k < nInterChannelAreas; k++ ) {
            double xOffset = ( areaWidth + channelThickness ) * k;
            for( int i = 1; i <= nColsPerArea; i++ ) {
                double x = colSpacing * i + this.getX() + xOffset;
                for( int j = 1; j <= nRows; j++ ) {
                    double y = rowSpacing * j + this.getY();
                    Point2D p = new Point2D.Double( x, y );
                    locations.add( p );
                }
            }
        }
        return (Point2D[])locations.toArray( new Point2D[locations.size()] );
    }
}
