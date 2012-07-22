// Copyright 2002-2012, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.forces1d.view;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;

/**
 * This version of Arrow is incompatible with the Arrow in phetcommon trunk HEAD, since it has
 * references to tail or head shape explicitly.
 * Arrow
 *
 * @author ?
 * @version $Revision$
 */
public class Arrow {

    private GeneralPath arrowPath = new GeneralPath();//This causes real problems because equals is not overriden.
    private GeneralPath headShape = new GeneralPath();
    private GeneralPath tailShape = new GeneralPath();
    private Point2D tailLocation;
    private Point2D tipLocation;
    private double headHeight;
    private double headWidth;
    private double tailWidth;
    private Vector2D direction;
    private Vector2D norm;
    boolean isHeadDynamic = false;
    private double fractionalHeadHeight;
    private boolean scaleTailToo;

    public boolean equals( Object obj ) {
        if ( obj instanceof Arrow ) {
            Arrow a = (Arrow) obj;
            return a.tailLocation.equals( tailLocation ) && a.tipLocation.equals( tipLocation ) && a.headHeight == headHeight
                   && a.headWidth == headWidth && a.tailWidth == tailWidth && a.isHeadDynamic == isHeadDynamic;
        }
        return false;
    }

    public Arrow( Point2D tailLocation, Point2D tipLocation, double headHeight, double headWidth, double tailWidth ) {
        init( tailLocation, tipLocation, headHeight, headWidth, tailWidth );
        computeArrow();
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

        if ( tailLocation.equals( tipLocation ) ) {
            return;
        }

        Vector2D tailPt = new Vector2D( tailLocation );
        Vector2D tipPt = new Vector2D( tipLocation );
        direction = tipPt.minus( tailPt ).normalized();
        double dist = tipLocation.distance( tailLocation );
        double tempHeadHeight = headHeight;
        double tempHeadWidth = headWidth;
        double tempTailWidth = tailWidth;
        if ( isHeadDynamic && dist < headHeight / fractionalHeadHeight ) {
            tempHeadHeight = dist * fractionalHeadHeight;
            if ( scaleTailToo ) {
                tempTailWidth = tailWidth * tempHeadHeight / headHeight;
                tempHeadWidth = headWidth * tempHeadHeight / headHeight;
            }
        }
        else if ( dist < headHeight ) {
            throw new RuntimeException( "Head too big." );
        }
        norm = direction.getPerpendicularVector();

        Vector2D rightFlap = getPoint( -1 * tempHeadHeight, -tempHeadWidth / 2 );
        Vector2D leftFlap = getPoint( -1 * tempHeadHeight, tempHeadWidth / 2 );
        Vector2D rightPin = getPoint( -1 * tempHeadHeight, -tempTailWidth / 2 );
        Vector2D leftPin = getPoint( -1 * tempHeadHeight, tempTailWidth / 2 );
        Vector2D rightTail = getPoint( -1 * dist, -tempTailWidth / 2 );
        Vector2D leftTail = getPoint( -1 * dist, tempTailWidth / 2 );

        this.arrowPath.reset();
        arrowPath.moveTo( (float) tipPt.getX(), (float) tipPt.getY() );
        lineTo( arrowPath, rightFlap );
        lineTo( arrowPath, rightPin );
        lineTo( arrowPath, rightTail );
        lineTo( arrowPath, leftTail );
        lineTo( arrowPath, leftPin );
        lineTo( arrowPath, leftFlap );
        lineTo( arrowPath, tipPt );

        headShape.reset();
        headShape.moveTo( (float) tipPt.getX(), (float) tipPt.getY() );
        lineTo( headShape, rightFlap );
        lineTo( headShape, leftFlap );
        lineTo( headShape, tipPt );

        tailShape.reset();
        tailShape.moveTo( (float) rightPin.getX(), (float) rightPin.getY() );
        lineTo( tailShape, rightTail );
        lineTo( tailShape, leftTail );
        lineTo( tailShape, leftPin );
        lineTo( tailShape, rightPin );
    }

    private void lineTo( GeneralPath path, Vector2D loc ) {
        path.lineTo( (float) loc.getX(), (float) loc.getY() );
    }

    //parallel and normal are from the tip
    private Vector2D getPoint( double parallel, double normal ) {
        Vector2D dv = direction.times( parallel ).
                plus( norm.times( normal ) );
        Vector2D abs = new Vector2D( dv.getX() + tipLocation.getX(), dv.getY() + tipLocation.getY() );
        return abs;
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

    public double getTailWidth() {
        return tailWidth;
    }

    public void setTailWidth( double tailWidth ) {
        this.tailWidth = tailWidth;
        computeArrow();
    }

    public GeneralPath getHeadShape() {
        return headShape;
    }

    public GeneralPath getTailShape() {
        return tailShape;
    }

    public void translate( double dx, double dy ) {
        tailLocation.setLocation( tailLocation.getX() + dx, tailLocation.getY() + dy );
        tipLocation.setLocation( tailLocation.getX() + dx, tailLocation.getY() + dy );
        computeArrow();
    }
}
