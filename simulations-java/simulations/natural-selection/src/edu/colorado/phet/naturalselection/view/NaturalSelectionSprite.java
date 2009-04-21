/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionCanvas;
import edu.umd.cs.piccolo.PNode;

public abstract class NaturalSelectionSprite extends PNode implements Comparable {


    private double spriteX = 0;
    private double spriteY = 0;
    private double spriteZ = 0;

    public NaturalSelectionSprite() {

    }

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

    public Point2D getCanvasLocation() {
        double locationY = ( -NaturalSelectionDefaults.VIEW_SIZE.getHeight() + NaturalSelectionCanvas.HORIZON ) * ( 1 - ( spriteZ - 1 ) / 1.5 ) + spriteY;
        return new Point2D.Double( spriteX, NaturalSelectionCanvas.HORIZON - ( locationY / spriteZ ) );
    }

    public double getInverseGroundZDepth( double y ) {
        return ( ( 5 * NaturalSelectionCanvas.HORIZON - 5 * NaturalSelectionDefaults.VIEW_SIZE.getHeight() ) / ( -3 * y + 5 * NaturalSelectionCanvas.HORIZON - 2 * NaturalSelectionDefaults.VIEW_SIZE.getHeight() ) );
    }

    public double getCanvasScale() {
        return 1.0 / spriteZ;
    }

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
