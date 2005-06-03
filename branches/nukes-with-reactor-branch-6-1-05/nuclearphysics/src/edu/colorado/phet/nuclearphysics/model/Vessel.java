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

/**
 * Vessel
 * <p>
 * The containment vessel for the reactor. It has channels for control rods.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Vessel {
    private Rectangle2D boundary;

    private int numChannels = 5;
    private double channelThickness = 100;
    private Rectangle2D[] rodChannels = new Rectangle2D[numChannels];

    /**
     *
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public Vessel( double x, double y, double width, double height ) {
        boundary = new Rectangle2D.Double( x, y, width, height );
        for( int i = 0; i < rodChannels.length; i++ ) {
            int orientation = ControlledFissionModule.VERTICAL;
            if( orientation == ControlledFissionModule.VERTICAL ) {
                double spacing = getWidth() / ( numChannels + 1 );
                    double channelX = getX() + spacing *  ( i + 1 );
                    double channelY = getY();
                    rodChannels[i] = new Rectangle2D.Double( channelX - channelThickness / 2, channelY,
                                                             channelThickness, getHeight() );
            }
        }
    }

    public Shape getShape() {
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
        return this.numChannels;
    }

    public boolean contains( double x, double y ) {
        return getShape().contains( x, y );
    }
    
    public boolean contains( Point2D p ) {
        return getShape().contains( p.getX(), p.getY() );
    }
}
