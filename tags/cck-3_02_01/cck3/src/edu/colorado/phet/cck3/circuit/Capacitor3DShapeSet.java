package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jul 8, 2006
 * Time: 6:07:22 PM
 * Copyright (c) Jul 8, 2006 by Sam Reid
 */

public class Capacitor3DShapeSet {
    private double tiltAngle;
    private double width;
    private double length;
    private Point2D inPt;
    private Point2D outPt;
    private double distBetweenPlates;
    private Shape plate1Path;
    private Shape plate2Path;
    private Shape plate2Wire;
    private Shape plate1Wire;

    public Capacitor3DShapeSet( double tiltAngle, double width, double height, Point2D inPt, Point2D outPt, double distBetweenPlates ) {
        this.tiltAngle = tiltAngle;
        this.width = width;
        this.length = height;
        this.inPt = inPt;
        this.outPt = outPt;
        this.distBetweenPlates = distBetweenPlates;
        updateShape();
    }

    private void updateShape() {
        Point2D plate1Point = getPlate1Point();
        Point2D plate2Point = getPlate2Point();
        plate1Path = createPlatePath( plate1Point );
        plate2Path = createPlatePath( plate2Point );
        plate1Wire = createWirePath( inPt, plate1Point );
        plate2Wire = createWirePath( outPt, plate2Point );
    }

    private Shape createWirePath( Point2D a, Point2D b ) {
        return new Line2D.Double( a, b );
    }

    private Shape createPlatePath( Point2D point ) {
        DoubleGeneralPath path = new DoubleGeneralPath( point );
        moveUV( path, -width / 2, 0 );
        lineUV( path, 0, -length / 2 );
        lineUV( path, width, 0 );
        lineUV( path, 0, length );
        lineUV( path, -width, 0 );
        lineUV( path, 0, -length / 2 );
        return path.getGeneralPath();
    }

    private void moveUV( DoubleGeneralPath path, double u, double v ) {
        Vector2D x = getVector( u, v );
        path.moveToRelative( x );
    }

    private Vector2D.Double getVector( double u, double v ) {
        Vector2D.Double vector = new Vector2D.Double( u * Math.cos( tiltAngle ), u * Math.sin( tiltAngle ) + v );
//        vector.rotate( tiltAngle );
        vector.rotate( getSegmentAngle() );
        return vector;
    }

    public Point2D getPlate1Location( double u, double v ) {
        Vector2D.Double vec = getVector( u, v );
        return vec.getDestination( getPlate1Point() );
    }

    public Point2D getPlate2Location( double u, double v ) {
        Vector2D.Double vec = getVector( u, v );
        return vec.getDestination( getPlate2Point() );
    }

    private double getSegmentAngle() {
        return new Vector2D.Double( inPt, outPt ).getAngle();
    }

    private void lineUV( DoubleGeneralPath path, double u, double v ) {
        Vector2D x = getVector( u, v );
        path.lineToRelative( x );
    }

    public Point2D getPlate2Point() {
        Vector2D.Double vector = new Vector2D.Double( outPt, inPt );
        return vector.getInstanceOfMagnitude( getDistToPlate() ).getDestination( outPt );
    }

    public Point2D getPlate1Point() {
        Vector2D.Double vector = new Vector2D.Double( inPt, outPt );
        return vector.getInstanceOfMagnitude( getDistToPlate() ).getDestination( inPt );
    }

    private double getDistToPlate() {
        return ( new Vector2D.Double( inPt, outPt ).getMagnitude() - distBetweenPlates ) / 2.0;
    }

    public Shape getPlate1Shape() {
        return plate1Path;
    }

    public Shape getPlate2Shape() {
        return plate2Path;
    }

    public Shape getPlate2Wire() {
        return plate2Wire;
    }

    public Shape getPlate1Wire() {
        return plate1Wire;
    }

    public Rectangle getBounds() {
        Area totalArea = getArea();
        return totalArea.getBounds();
    }

    public Area getArea() {
        Area a = new Area();
        a.add( new Area( getPlate1Shape() ) );
        a.add( new Area( getPlate2Shape() ) );
        Stroke stroke = new BasicStroke();
        a.add( new Area( stroke.createStrokedShape( getPlate1Wire() ) ) );
        a.add( new Area( stroke.createStrokedShape( getPlate2Wire() ) ) );
        return a;
    }

    public Point2D getPlate2EdgePoint() {
        AbstractVector2D vector = new Vector2D.Double( inPt, outPt );
//        double totalDist = vector.getMagnitude();
        double a = width * Math.cos( tiltAngle );
        double initDist = new Vector2D.Double( inPt, getPlate2Point() ).getMagnitude();
        vector = vector.getInstanceOfMagnitude( initDist + a / 2.0 );
        return vector.getDestination( inPt );
    }

    public double getWidth() {
        return width;
    }

    public double getLength() {
        return length;
    }

}
