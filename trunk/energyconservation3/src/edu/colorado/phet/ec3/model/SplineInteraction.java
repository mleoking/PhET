package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;

import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Nov 7, 2006
 * Time: 10:16:31 PM
 * Copyright (c) Nov 7, 2006 by Sam Reid
 */
public class SplineInteraction {
    private EnergyConservationModel energyConservationModel;

    public SplineInteraction( EnergyConservationModel energyConservationModel ) {
        this.energyConservationModel = energyConservationModel;
    }

    public void interactWithSplines( Body body ) {
        body.convertToSpline();
        AbstractSpline grabSpline = getGrabSpline( body );
        if( grabSpline != null ) {
            body.setSplineMode( energyConservationModel, grabSpline );
//            System.out.println( "grabSpline = " + grabSpline );
        }
        else {
            tryCollision( body );
            body.convertToFreefall();
        }
    }

    private AbstractSpline getGrabSpline( Body body ) {
        double bestScore = Double.POSITIVE_INFINITY;
        AbstractSpline bestSpline = null;
        ArrayList allSplines = energyConservationModel.getAllSplines();
        for( int i = 0; i < allSplines.size(); i++ ) {
            double score = getGrabScore( (AbstractSpline)allSplines.get( i ), body );
            if( score < bestScore ) {
                bestScore = score;
                bestSpline = (AbstractSpline)allSplines.get( i );
            }
        }
        return bestSpline;
    }

    private double getGrabScore( AbstractSpline spline, Body body ) {
        Area feet = new Area( body.getFeetShape() );
        Area splineArea = new Area( spline.getArea() );
        splineArea.intersect( feet );
        boolean collide = !splineArea.isEmpty();
        boolean recentGrab = justLeft( body, spline );
        Point2D pt = spline.evaluateAnalytical( spline.getDistAlongSpline( body.getCenterOfMass(), 0, spline.getLength(), 100 ) );
        return collide && !recentGrab && movingTowards( body, pt ) ? 0 : Double.POSITIVE_INFINITY;
    }

    private boolean movingTowards( Body body, Point2D pt ) {
        Point2D cm = body.getCenterOfMass();
        Vector2D.Double cmToPoint = new Vector2D.Double( cm, pt );
        return cmToPoint.dot( body.getVelocity() ) >= 0;
    }

    private boolean justLeft( Body body, AbstractSpline splineSurface ) {
        return body.getLastFallSpline() == splineSurface && ( System.currentTimeMillis() - body.getLastFallTime() ) < 1000;
    }

    private void tryCollision( Body body ) {
        body.convertToSpline();
        AbstractSpline collisionSpline = getCollisionSpline( body );
        if( collisionSpline != null ) {
            doCollision( collisionSpline, body );
//            System.out.println( "collisionSpline = " + collisionSpline );
        }
        else {
            body.convertToFreefall();
        }
    }

    private AbstractSpline getCollisionSpline( Body body ) {
        double bestScore = Double.POSITIVE_INFINITY;
        AbstractSpline bestSpline = null;
        ArrayList allSplines = energyConservationModel.getAllSplines();
        for( int i = 0; i < allSplines.size(); i++ ) {
            double score = getCollisionScore( (AbstractSpline)allSplines.get( i ), body );
            if( score < bestScore ) {
                bestScore = score;
                bestSpline = (AbstractSpline)allSplines.get( i );
            }
        }
        return bestSpline;
    }

    private double getCollisionScore( AbstractSpline spline, Body body ) {
        Point2D pt = spline.evaluateAnalytical( spline.getDistAlongSpline( body.getCenterOfMass(), 0, spline.getLength(), 100 ) );
        boolean a = isColliding( spline, body ) && !wasColliding( spline, body ) && movingTowards( body, pt );
        return a ? 0 : Double.POSITIVE_INFINITY;
    }

    private boolean wasColliding( AbstractSpline splineSurface, Body body ) {
        if( body.getNumHistoryPoints() >= 2 ) {
            Body.StateRecord state = body.getCollisionStateFromEnd( 1 );
            if( state.containsSpline( splineSurface ) ) {
                return true;
            }
        }
        return false;
    }

    public boolean isColliding( AbstractSpline spline, Body body ) {//todo called from several places; duplicate operation
        Area area = new Area( spline.getArea() );
        area.intersect( new Area( body.getReducedShape( 0.9 ) ) );
        return !area.isEmpty();
    }

    public void doCollision( AbstractSpline collisionSpline, Body body ) {
        Area area = new Area( collisionSpline.getArea() );//todo: remove this duplicate computation if it is a performance problem
        area.intersect( new Area( body.getReducedShape( 0.9 ) ) );
        Rectangle2D intersectionBounds = area.getBounds2D();
        double x = collisionSpline.getDistAlongSpline( new Point2D.Double( intersectionBounds.getCenterX(), intersectionBounds.getCenterY() ), 0, collisionSpline.getLength(), 100 );
        collideWithTrack( collisionSpline, x, body );
    }

    private void collideWithTrack( AbstractSpline collisionSpline, double x, Body body ) {
        double origSpeed = body.getSpeed();
        double origEnergy = body.getTotalEnergy();
        double parallelPart = collisionSpline.getUnitParallelVector( x ).dot( body.getVelocity() );
        double perpPart = collisionSpline.getUnitNormalVector( x ).dot( body.getVelocity() );
        Vector2D.Double newVelocity = new Vector2D.Double();
        newVelocity.add( collisionSpline.getUnitParallelVector( x ).getScaledInstance( parallelPart ) );
        newVelocity.add( collisionSpline.getUnitNormalVector( x ).getScaledInstance( -perpPart ) );

        //override for testing
//        double alpha = 5;
//        double speedScale = 1.0 * Math.exp( -alpha * body.getFrictionCoefficient() );
        double speedScale = body.getCoefficientOfRestitution();
        double newSpeed = body.getSpeed() * speedScale;
        if( newSpeed > origSpeed ) {
            throw new RuntimeException( "Body gained speed on collision" );
        }
        body.setVelocity( newVelocity.getInstanceOfMagnitude( newSpeed ) );

        double newTotalEnergy = body.getTotalEnergy();
        double energyLostInCollision = newTotalEnergy - origEnergy;
        body.addThermalEnergy( -energyLostInCollision );
//            System.out.println( "speedScale=" + speedScale + ", origSpeed=" + origSpeed + ", newSpeed=" + newSpeed + ", friction=" + body.getFrictionCoefficient() + ", energyLostInCollision=" + energyLostInCollision );
        body.convertToFreefall();
        body.setAngularVelocity( parallelPart / 2 );
    }
}
