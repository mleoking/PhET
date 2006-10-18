/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;

import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;


/**
 * User: Sam Reid
 * Date: Sep 26, 2005
 * Time: 7:33:57 PM
 * Copyright (c) Sep 26, 2005 by Sam Reid
 */

public class FreeFall extends ForceMode implements Derivable {
    private EnergyConservationModel energyConservationModel;
    private double rotationalVelocity;

    public FreeFall( EnergyConservationModel energyConservationModel, double rotationalVelocity ) {
        this.energyConservationModel = energyConservationModel;
        this.rotationalVelocity = rotationalVelocity;
    }

    public void stepInTime( Body body, double dt ) {
        double origEnergy = body.getTotalEnergy();
        setNetForce( getTotalForce( body ) );
        super.stepInTime( body, dt );
        body.setCMRotation( body.getCMRotation() + rotationalVelocity * dt );
        new EnergyConserver().fixEnergy( body, origEnergy );
        double DE = Math.abs( body.getMechanicalEnergy() - origEnergy );
        if( DE > 1E-6 ) {
            System.out.println( "energy conservation error in free fall: " + DE );
        }
        doGrab( body );
    }

    private Vector2D.Double getTotalForce( Body body ) {
        return new Vector2D.Double( 0 + body.getThrust().getX(), body.getMass() * body.getGravity() + body.getThrust().getY() );
    }

    public void reset() {
        rotationalVelocity = 0.0;
    }

    public void init( Body body ) {
        //going from spline to freefall mode
        body.convertToFreefall();
    }

    private void doGrab( Body body ) {
        double bestScore = Double.POSITIVE_INFINITY;
        AbstractSpline bestSpline = null;
        ArrayList allSplines = energyConservationModel.getAllSplines();
        for( int i = 0; i < allSplines.size(); i++ ) {
            AbstractSpline splineSurface = (AbstractSpline)allSplines.get( i );
            double score = getGrabScore( splineSurface, body );
            if( score < bestScore ) {
                bestScore = score;
                bestSpline = splineSurface;
            }
        }
        if( bestSpline != null ) {
            body.setSplineMode( energyConservationModel, bestSpline );
        }
    }

    private double getGrabScore( AbstractSpline splineSurface, Body body ) {
        body.convertToSpline();
        double x = splineSurface.getDistAlongSpline( body.getAttachPoint(), 0, splineSurface.getLength(), 100 );
        Point2D pt = splineSurface.evaluateAnalytical( x );
        double dist = pt.distance( body.getAttachPoint() );
        if( dist < 0.05 ) {
            return dist;
        }
        else {
            return Double.POSITIVE_INFINITY;
        }
    }

    boolean intersectsOrig( AbstractSpline spline, Body body ) {
        Area area = new Area( body.getShape() );
        area.intersect( spline.getArea() );
        return !area.isEmpty();
    }


    public double getRotationalVelocity() {
        return rotationalVelocity;
    }

    public void setRotationalVelocity( double rotationalVelocity ) {
        this.rotationalVelocity = rotationalVelocity;
    }

}
