/* Copyright 2003-2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.view.graphics;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Arrow
 *
 * @author ?
 */
public class Arrow {

    private GeneralPath arrowPath = new GeneralPath();//This causes real problems because equals is not overriden.

    private Point2D tailLocation;
    private Point2D tipLocation;
    private double headHeight;
    private double headWidth;
    private double tailWidth;
    private double fractionalHeadHeight;
    private boolean scaleTailToo;

    private AbstractVector2D direction;
    private AbstractVector2D norm;
    boolean isHeadDynamic = false;

    public boolean equals( Object obj ) {
        boolean equals = false;
        if ( obj instanceof Arrow ) {
            Arrow a = (Arrow) obj;
            equals = ( a.tailLocation.equals( tailLocation ) && a.tipLocation.equals( tipLocation ) && a.headHeight == headHeight
                       && a.headWidth == headWidth && a.tailWidth == tailWidth && a.isHeadDynamic == isHeadDynamic );
        }
        return equals;
    }

    public Arrow( Point2D tailLocation, Point2D tipLocation, double headHeight, double headWidth, double tailWidth ) {
        init( tailLocation, tipLocation, headHeight, headWidth, tailWidth );
        computeArrow();
    }

    /**
     * Constructs an Arrow specified by a source Point2D and a direction and magnitude Vector2D.
     *
     * @param src
     * @param vector
     * @param headHeight
     * @param headWidth
     * @param tailWidth
     * @param fractionalHeadHeight
     * @param scaleTailToo
     */
    public Arrow( Point2D src, AbstractVector2D vector,
                  double headHeight, double headWidth, double tailWidth,
                  double fractionalHeadHeight, boolean scaleTailToo ) {
        this( src, vector.getDestination( src ), headHeight, headWidth, tailWidth, fractionalHeadHeight, scaleTailToo );
    }

    /**
     * @param tailLocation
     * @param tipLocation
     * @param headHeight
     * @param headWidth
     * @param tailWidth
     * @param fractionalHeadHeight When the head size is less than fractionalHeadHeight*total Arrow size,
     *                             the head will be scaled.
     * @param scaleTailToo
     */
    public Arrow( Point2D tailLocation, Point2D tipLocation,
                  double headHeight, double headWidth, double tailWidth,
                  double fractionalHeadHeight, boolean scaleTailToo ) {
        this.fractionalHeadHeight = fractionalHeadHeight;
        this.scaleTailToo = scaleTailToo;
        this.isHeadDynamic = true;
        init( tailLocation, tipLocation, headHeight, headWidth, tailWidth );
        computeArrow();
    }


    void init( Point2D tailLocation, Point2D tipLocation, double headHeight, double headWidth, double tailWidth ) {
        this.tailLocation = tailLocation;
        this.tipLocation = tipLocation;
        this.headHeight = headHeight;
        this.headWidth = headWidth;
        this.tailWidth = tailWidth;
    }

    private void computeArrow() {
        this.arrowPath.reset();
        if ( tailLocation.distance( tipLocation ) != 0 ) {
            AbstractVector2D.Double tailPt = new ImmutableVector2D.Double( tailLocation );
            AbstractVector2D.Double tipPt = new ImmutableVector2D.Double( tipLocation );
            AbstractVector2D distanceVector = tipPt.getSubtractedInstance( tailPt );
            direction = distanceVector.getNormalizedInstance();
            double length = tipLocation.distance( tailLocation );
            double tempHeadHeight = headHeight;
            double tempHeadWidth = headWidth;
            double tempTailWidth = tailWidth;
            if ( isHeadDynamic && length < headHeight / fractionalHeadHeight ) {
                tempHeadHeight = length * fractionalHeadHeight;
                if ( scaleTailToo ) {
                    tempTailWidth = tailWidth * tempHeadHeight / headHeight;
                    tempHeadWidth = headWidth * tempHeadHeight / headHeight;
                }
            }
            else if ( length < headHeight ) {
                throw new RuntimeException( "headHeight is bigger than arrow length: length=" + length + " headHeight=" + headHeight );
            }
            norm = direction.getNormalVector();

            AbstractVector2D.Double rightFlap = getPoint( -1 * tempHeadHeight, -tempHeadWidth / 2 );
            AbstractVector2D.Double leftFlap = getPoint( -1 * tempHeadHeight, tempHeadWidth / 2 );
            AbstractVector2D.Double rightPin = getPoint( -1 * tempHeadHeight, -tempTailWidth / 2 );
            AbstractVector2D.Double leftPin = getPoint( -1 * tempHeadHeight, tempTailWidth / 2 );
            AbstractVector2D.Double rightTail = getPoint( -1 * length, -tempTailWidth / 2 );
            AbstractVector2D.Double leftTail = getPoint( -1 * length, tempTailWidth / 2 );

            arrowPath.moveTo( (float) tipPt.getX(), (float) tipPt.getY() );
            lineTo( arrowPath, rightFlap );
            lineTo( arrowPath, rightPin );
            lineTo( arrowPath, rightTail );
            lineTo( arrowPath, leftTail );
            lineTo( arrowPath, leftPin );
            lineTo( arrowPath, leftFlap );
            arrowPath.closePath();
        }
    }

    private void lineTo( GeneralPath path, AbstractVector2D.Double loc ) {
        path.lineTo( (float) loc.getX(), (float) loc.getY() );
    }

    //parallel and normal are from the tip
    private AbstractVector2D.Double getPoint( double parallel, double normal ) {
        // do scaling and addition of vector components inline to improve performance
        double x = ( direction.getX() * parallel ) + ( norm.getX() * normal ) + tipLocation.getX();
        double y = ( direction.getY() * parallel ) + ( norm.getY() * normal ) + tipLocation.getY();
        return new ImmutableVector2D.Double( x, y );
    }

    public GeneralPath getShape() {
        return arrowPath;
    }

    public Point2D getTailLocation() {
        return new Point2D.Double( tailLocation.getX(), tailLocation.getY() );
    }

    public Point2D getTipLocation() {
        return new Point2D.Double( tipLocation.getX(), tipLocation.getY() );
    }

    public void setTailLocation( Point2D tailLocation ) {
        this.tailLocation = tailLocation;
        computeArrow();
    }

    public void setTipLocation( Point2D tipLocation ) {
        this.tipLocation = tipLocation;
        computeArrow();
    }

    public void setTipAndTailLocations( Point2D tipLocation, Point2D tailLocation ) {
        this.tipLocation = tipLocation;
        this.tailLocation = tailLocation;
        computeArrow();
    }

    public double getTailWidth() {
        return tailWidth;
    }

    public void setTailWidth( double tailWidth ) {
        this.tailWidth = tailWidth;
        computeArrow();
    }

    public void translate( double dx, double dy ) {
        tailLocation.setLocation( tailLocation.getX() + dx, tailLocation.getY() + dy );
        tipLocation.setLocation( tailLocation.getX() + dx, tailLocation.getY() + dy );
        computeArrow();
    }

    public void setHeadWidth( double headWidth ) {
        this.headWidth = headWidth;
        computeArrow();
    }

    public double getHeadWidth() {
        return headWidth;
    }

    public void setHeadHeight( double headHeight ) {
        this.headHeight = headHeight;
        computeArrow();
    }

    public double getHeadHeight() {
        return headHeight;
    }

    public void setFractionalHeadHeight( double fractionalHeadHeight ) {
        this.fractionalHeadHeight = fractionalHeadHeight;
        computeArrow();
    }

    public double getFractionalHeadHeight() {
        return fractionalHeadHeight;
    }

    public void setScaleTailToo( boolean scaleTailToo ) {
        this.scaleTailToo = scaleTailToo;
        computeArrow();
    }

    public boolean getScaleTailToo() {
        return scaleTailToo;
    }
}
