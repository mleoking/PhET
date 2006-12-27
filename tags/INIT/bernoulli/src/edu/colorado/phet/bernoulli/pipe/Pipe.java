package edu.colorado.phet.bernoulli.pipe;

import edu.colorado.phet.bernoulli.Vessel;
import edu.colorado.phet.bernoulli.common.DoubleGeneralPath;
import edu.colorado.phet.bernoulli.spline.Spline;
import edu.colorado.phet.coreadditions.math.PhetVector;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Aug 22, 2003
 * Time: 11:52:15 AM
 * Copyright (c) Aug 22, 2003 by Sam Reid
 */
public class Pipe extends Vessel {
    private GeneralPath shape;
    int numSmoothingPoints;
    ArrayList controlSections = new ArrayList();
    Spline topSpline;
    Spline bottomSpline;
    ArrayList flowLines = new ArrayList();

    public Pipe( ControlSection opening, double initWidth, int numSections, int numSmoothingPoints, int numFlowLines ) {
        this.numSmoothingPoints = numSmoothingPoints;
        double dx = initWidth / numSections;
        double x = dx;
        controlSections.add( opening );
        for( int i = 0; i < numSections; i++ ) {
            ControlSection cs = opening.createChild( x );
            controlSections.add( cs );
            x += dx;
        }
        topSpline = new Spline( numSmoothingPoints );
        bottomSpline = new Spline( numSmoothingPoints );
        for( int i = 0; i < numFlowLines; i++ ) {
            flowLines.add( new Spline( numSmoothingPoints ) );
        }
        recomputeState();
    }

    public int numFlowLines() {
        return flowLines.size();
    }

    public Spline flowLineAt( int i ) {
        return (Spline)flowLines.get( i );
    }

    public GeneralPath getShape() {
        return shape;
    }

    public Spline getBottomSpline() {
        return bottomSpline;
    }

    public Spline getTopSpline() {
        return topSpline;
    }

    public int getNumSmoothingPoints() {
        return numSmoothingPoints;
    }

    public int getNumFlowLines() {
        return flowLines.size();
    }

    public ControlSection controlSectionAt( int i ) {
        return (ControlSection)controlSections.get( i );
    }

    public int numControlSections() {
        return controlSections.size();
    }

    public Point2D.Double[] getTopControlPoints() {
        Point2D.Double[] pts = new Point2D.Double[numControlSections()];
        for( int i = 0; i < controlSections.size(); i++ ) {
            ControlSection cs = controlSectionAt( i );
            pts[i] = new Point2D.Double( cs.getTopX(), cs.getTopY() );
        }
        return pts;
    }

    public Point2D.Double[] getBottomControlPoints() {
        Point2D.Double[] pts = new Point2D.Double[numControlSections()];
        for( int i = 0; i < controlSections.size(); i++ ) {
            ControlSection cs = controlSectionAt( i );
            pts[i] = new Point2D.Double( cs.getBottomX(), cs.getBottomY() );
        }
        return pts;
    }

    public void translateTopPoint( int index, Point2D.Double modelDX ) {
        ControlSection sec = controlSectionAt( index );
        sec.translateTopPoint( modelDX );
        recomputeState();
    }

    public void translateBottomPoint( int index, Point2D.Double modelDX ) {
        ControlSection sec = controlSectionAt( index );
        sec.translateBottomPoint( modelDX );
        recomputeState();
    }

    public Point2D.Double[] createFlowLine( double dx ) {
        Point2D.Double[] pts = new Point2D.Double[numControlSections()];
        for( int i = 0; i < pts.length; i++ ) {
            ControlSection cs = controlSectionAt( i );
            PhetVector loc = cs.getFractionalPoint( dx );
            pts[i] = new Point2D.Double( loc.getX(), loc.getY() );
        }
        return pts;
    }

    private void fixFlowlines() {
        int numFlowLines = numFlowLines();
        double dx = 1.0 / ( numFlowLines + 1.0 );
        for( int i = 0; i < numFlowLines; i++ ) {
            double offset = ( i + 1 ) * dx;
            Point2D.Double[] flow = createFlowLine( offset );
            Spline flowLine = (Spline)flowLines.get( i );
            flowLine.setControlPoints( flow );
        }
    }

    public void recomputeState() {
        Point2D.Double[] topControlPoints = getTopControlPoints();
        topSpline.setControlPoints( topControlPoints );
        Point2D.Double[] bottomControlPoints = getBottomControlPoints();
        bottomSpline.setControlPoints( bottomControlPoints );
        fixFlowlines();
        fixShape();
        updateObservers();
    }

    private void fixShape() {
        DoubleGeneralPath path = new DoubleGeneralPath( bottomSpline.interpolatedPointAt( 0 ) );
        for( int i = 1; i < bottomSpline.numInterpolatedPoints(); i++ ) {
            path.lineTo( bottomSpline.interpolatedPointAt( i ) );
        }
        for( int i = topSpline.numInterpolatedPoints() - 1; i >= 0; i-- ) {
            path.lineTo( topSpline.interpolatedPointAt( i ) );
        }
        this.shape = path.getGeneralPath();
    }


    public boolean waterContainsPoint( double x, double y ) {
        return shape.contains( x, y );
    }

    public double getPressure( double x, double y ) {
        return 999;
    }
//    public double speedAt(double x) {
//        return .5;
//    }

//    public PhetVector getVelocityVectorAt(double x, double y) {
//        return null;
//    }

}
