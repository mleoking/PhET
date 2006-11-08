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

    public static interface Action {

    }

    public void interactWithSplines( Body body ) {
        body.convertToSpline();
//        Action action=getAction(body);
        AbstractSpline grabSpline = getGrabSpline( body );
        if( grabSpline != null ) {
            body.setSplineMode( energyConservationModel, grabSpline );
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

    private void tryCollision( Body body ) {
        body.convertToSpline();
        AbstractSpline collisionSpline = getCollisionSpline( body );
        if( collisionSpline != null ) {
            doCollision( collisionSpline, body );
        }
        else {
            body.convertToFreefall();
        }
    }

    private void doCollision( AbstractSpline collisionSpline, Body body ) {
        Area area = new Area( collisionSpline.getArea() );//todo: remove this duplicate computation
        //todo: add restitution
        area.intersect( new Area( body.getShape() ) );
        Rectangle2D intersectionBounds = area.getBounds2D();
        double x = collisionSpline.getDistAlongSpline( new Point2D.Double( intersectionBounds.getCenterX(), intersectionBounds.getCenterY() ), 0, collisionSpline.getLength(), 100 );
        if( !feetAreClose( body, x, collisionSpline ) ) {
            double epsilon = 0.05;
            if( ( x <= epsilon || x >= collisionSpline.getLength() - epsilon ) && isBodyMovingTowardSpline( body, collisionSpline, x ) )
            {
                collideWithEnd( body );
            }
            else {
                collideWithTrack( collisionSpline, x, body );
            }
        }
    }

    private void collideWithTrack( AbstractSpline collisionSpline, double x, Body body ) {
        double origSpeed = body.getSpeed();
        double origEnergy = body.getTotalEnergy();
        double parallelPart = collisionSpline.getUnitParallelVector( x ).dot( body.getVelocity() );
        double perpPart = collisionSpline.getUnitNormalVector( x ).dot( body.getVelocity() );
        Vector2D.Double newVelocity = new Vector2D.Double();
        newVelocity.add( collisionSpline.getUnitParallelVector( x ).getScaledInstance( parallelPart ) );
        newVelocity.add( collisionSpline.getUnitNormalVector( x ).getScaledInstance( -perpPart ) );

        double alpha = 5;
        double speedScale = 1.0 * Math.exp( -alpha * body.getFrictionCoefficient() );
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

    private void collideWithEnd( Body body ) {
        System.out.println( "Collision with end." );
        body.setVelocity( body.getVelocity().getScaledInstance( -1 ) );
        double angle = body.getVelocity().getAngle();
        double maxDTheta = Math.PI / 16;
        double dTheta = ( Math.random() * 2 - 1 ) * maxDTheta;
        body.setVelocity( Vector2D.Double.parseAngleAndMagnitude( body.getVelocity().getMagnitude(), angle + dTheta ) );
        body.setAngularVelocity( dTheta * 10 );
        body.convertToFreefall();
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

    private boolean isBodyMovingTowardSpline( Body body, AbstractSpline bestSpline, double x ) {
        Point2D loc = bestSpline.evaluateAnalytical( x );
        Point2D cm = body.getCenterOfMass();
        Vector2D.Double dir = new Vector2D.Double( loc, cm );
        double v = dir.dot( body.getVelocity() );
        return v < 0;
    }

    private boolean feetAreClose( Body body, double x, AbstractSpline bestSpline ) {
        return bestSpline.evaluateAnalytical( x ).distance( body.getAttachPoint() ) < bestSpline.evaluateAnalytical( x ).distance( body.getCenterOfMass() );
    }

    private double getCollisionScore( AbstractSpline splineSurface, Body body ) {
        Area area = new Area( splineSurface.getArea() );
        area.intersect( new Area( body.getShape() ) );
        boolean bounce = !area.isEmpty();
        return bounce ? 0.0 : Double.POSITIVE_INFINITY;
    }

    private double getGrabScore( AbstractSpline splineSurface, Body body ) {
        double x = splineSurface.getDistAlongSpline( body.getAttachPoint(), 0, splineSurface.getLength(), 100 );
        Point2D pt = splineSurface.evaluateAnalytical( x );
        double dist = pt.distance( body.getAttachPoint() );
        if( dist < 0.5 && !justLeft( body, splineSurface ) && movingTowards( splineSurface, body, x, pt ) ) {
            return dist;
        }
        else {
            return Double.POSITIVE_INFINITY;
        }
    }

    private boolean movingTowards( AbstractSpline splineSurface, Body body, double x, Point2D pt ) {
        Point2D cm = body.getCenterOfMass();
        Vector2D.Double cmToPoint = new Vector2D.Double( cm, pt );
        return cmToPoint.dot( body.getVelocity() ) >= 0;
    }

    private boolean justLeft( Body body, AbstractSpline splineSurface ) {
        return body.getLastFallSpline() == splineSurface && ( System.currentTimeMillis() - body.getLastFallTime() ) < 500;
    }

}
