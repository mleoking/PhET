package edu.colorado.phet.bernoulli.pipe;

import edu.colorado.phet.coreadditions.math.PhetVector;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Aug 22, 2003
 * Time: 12:08:16 PM
 * Copyright (c) Aug 22, 2003 by Sam Reid
 */
public class ControlSection {
    PhetVector top;
    PhetVector bottom;
    private double height;

    public String toString() {
        return "top=" + top + ", bottom=" + bottom;
    }

    public ControlSection( double bottomx, double bottomy, double height ) {
        this.height = height;
        top = new PhetVector( bottomx, bottomy + height );
        bottom = new PhetVector( bottomx, bottomy );
    }

    public ControlSection createChild( double dx ) {
        return new ControlSection( bottom.getX() + dx, bottom.getY(), height );
    }

    public double getTopX() {
        return top.getX();
    }

    public double getTopY() {
        return top.getY();
    }

    public double getBottomY() {
        return bottom.getY();
    }

    public double getBottomX() {
        return bottom.getX();
    }

    public PhetVector getFractionalPoint( double dx ) {
        PhetVector vector = top.getSubtractedInstance( bottom );
        double magnitude = vector.getMagnitude();
        double newMagniutde = magnitude * dx;
//        vector.setMagnitude(Math.abs(dx / vector.getMagnitude()));
        vector.setMagnitude( newMagniutde );
        vector.add( bottom );
        return vector;
    }

    public void setTopLocation( Point2D.Double modelDX ) {
        top = new PhetVector( modelDX.x, modelDX.y );
    }

    public void setBottomLocation( Point2D.Double modelDX ) {
        bottom = new PhetVector( modelDX.x, modelDX.y );
    }

    public void translateTopPoint( Point2D.Double modelDX ) {
        top.add( modelDX.x, modelDX.y );
    }

    public void translateBottomPoint( Point2D.Double modelDX ) {
        bottom.add( modelDX.x, modelDX.y );
    }

    public PhetVector getTopPoint() {
        return top;
    }

    public PhetVector getBottomPoint() {
        return bottom;
    }

    public double getHeight() {
        return top.getY() - bottom.getY();
    }

    public PhetVector getMidpoint() {
        return getTopPoint().getSubtractedInstance( getBottomPoint() ).getScaledInstance( 0.5 ).getAddedInstance( this.getBottomPoint() );
    }


}
