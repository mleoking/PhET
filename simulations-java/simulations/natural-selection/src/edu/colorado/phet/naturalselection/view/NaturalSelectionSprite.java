/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * Abstract class for all piccolo nodes to be displayed in and around the bunnies
 *
 * @author Jonathan Olson
 */
public abstract class NaturalSelectionSprite extends PNode implements Comparable {

    // position
    private double spriteX = 0;
    private double spriteY = 0;
    private double spriteZ = 0;

    public NaturalSelectionSprite() {

    }

    //----------------------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------------------

    public double getSpriteX() {
        return spriteX;
    }

    public void setSpriteX( double spriteX ) {
        this.spriteX = spriteX;
    }

    public double getSpriteY() {
        return spriteY;
    }

    public void setSpriteY( double spriteY ) {
        this.spriteY = spriteY;
    }

    public double getSpriteZ() {
        return spriteZ;
    }

    public void setSpriteZ( double spriteZ ) {
        this.spriteZ = spriteZ;
    }

    public void setSpriteLocation( double x, double y, double z ) {
        setSpriteX( x );
        setSpriteY( y );
        setSpriteZ( z );
    }

    /**
     * Layout function to determine 2D position from the 3D coordinates
     *
     * @return 2D canvas position
     */
    public Point2D getCanvasLocation() {
        // assuming for now that we are on a hill, so 0 y == on hill. spriteY will show above and below that
        double locationY = ( -NaturalSelectionDefaults.VIEW_SIZE.getHeight() + NaturalSelectionCanvas.HORIZON ) * ( 1 - ( spriteZ - 1 ) / 1.5 ) + spriteY;
        return new Point2D.Double( spriteX, NaturalSelectionCanvas.HORIZON - ( locationY / spriteZ ) );
    }

    /**
     * Compute the necessary Z depth for a particular canvas height y (used for placing trees and shrubbery!)
     *
     * @param y The canvas location y
     * @return The necessary spriteZ depth that with spriteY == 0 will return the canvas location y
     */
    public double getInverseGroundZDepth( double y ) {
        return ( ( 5 * NaturalSelectionCanvas.HORIZON - 5 * NaturalSelectionDefaults.VIEW_SIZE.getHeight() ) / ( -3 * y + 5 * NaturalSelectionCanvas.HORIZON - 2 * NaturalSelectionDefaults.VIEW_SIZE.getHeight() ) );
    }

    /**
     * Determine the scale that should be applied
     *
     * @return The scale
     */
    public double getCanvasScale() {
        return 1.0 / spriteZ;
    }

    /**
     * Allow the sprites to be sorted quickly for display depth
     *
     * @param otherObject Another sprite to be compared to
     * @return The usual compare function return values
     */
    public int compareTo( Object otherObject ) {
        NaturalSelectionSprite other = (NaturalSelectionSprite) otherObject;
        if ( spriteZ == other.spriteZ ) {
            return 0;
        }

        if ( spriteZ > other.spriteZ ) {
            return -1;
        }
        else {
            return 1;
        }
    }

}
