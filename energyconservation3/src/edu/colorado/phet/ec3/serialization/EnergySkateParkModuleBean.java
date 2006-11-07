package edu.colorado.phet.ec3.serialization;

import edu.colorado.phet.ec3.EnergySkateParkModule;
import edu.colorado.phet.ec3.FloorSpline;
import edu.colorado.phet.ec3.model.Body;
import edu.colorado.phet.ec3.model.spline.CubicSpline;
import edu.colorado.phet.ec3.model.spline.SplineSurface;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Nov 7, 2006
 * Time: 12:49:52 PM
 * Copyright (c) Nov 7, 2006 by Sam Reid
 */

public class EnergySkateParkModuleBean {
    private ArrayList bodies = new ArrayList();
    private ArrayList splines = new ArrayList();

    public EnergySkateParkModuleBean() {
    }

    public EnergySkateParkModuleBean( EnergySkateParkModule module ) {
        for( int i = 0; i < module.getEnergyConservationModel().numBodies(); i++ ) {
            addBody( module.getEnergyConservationModel().bodyAt( i ) );
        }
        for( int i = 0; i < module.getEnergyConservationModel().numSplineSurfaces(); i++ ) {
            SplineSurface splineSurface = module.getEnergyConservationModel().splineSurfaceAt( i );
            if( !( splineSurface.getSpline() instanceof FloorSpline ) ) {
                addSplineSurface( splineSurface );
            }
        }
    }

    private void addSplineSurface( SplineSurface splineSurface ) {
        splines.add( new SplineElement( splineSurface ) );
    }

    public static class SplineElement {
        private Point2D[] controlPoints;

        public SplineElement() {
        }

        public SplineElement( SplineSurface splineSurface ) {
            controlPoints = splineSurface.getSpline().getControlPoints();
        }

        public Point2D[] getControlPoints() {
            return controlPoints;
        }

        public void setControlPoints( Point2D[] controlPoints ) {
            this.controlPoints = controlPoints;
        }

        public SplineSurface toSplineSurface() {
            CubicSpline top = new CubicSpline();
            for( int i = 0; i < controlPoints.length; i++ ) {
                Point2D controlPoint = controlPoints[i];
                top.addControlPoint( controlPoint );
            }
            return new SplineSurface( top );
        }
    }

    private void addBody( Body body ) {
        bodies.add( new BodyElement( body ) );
    }

    public static class BodyElement {
        private Point2D.Double position;
        private Point2D.Double velocity;
        private Point2D.Double acceleration;

        public BodyElement() {
        }

        public BodyElement( Body body ) {
            position = body.getPosition();
            velocity = new Point2D.Double( body.getVelocity().getX(), body.getVelocity().getY() );
            acceleration = new Point2D.Double( body.getAcceleration().getX(), body.getAcceleration().getY() );
        }

        public Point2D.Double getPosition() {
            return position;
        }

        public void setPosition( Point2D.Double position ) {
            this.position = position;
        }

        public Point2D.Double getVelocity() {
            return velocity;
        }

        public void setVelocity( Point2D.Double velocity ) {
            this.velocity = velocity;
        }

        public Point2D.Double getAcceleration() {
            return acceleration;
        }

        public void setAcceleration( Point2D.Double acceleration ) {
            this.acceleration = acceleration;
        }

        public void apply( Body body ) {
            body.setAttachmentPointPosition( position );
            body.setVelocity( velocity.x, velocity.y );
            body.setAcceleration( acceleration.x, acceleration.y );
        }
    }

    public ArrayList getSplines() {
        return splines;
    }

    public void setSplines( ArrayList splines ) {
        this.splines = splines;
    }

    public ArrayList getBodies() {
        return bodies;
    }

    public void setBodies( ArrayList bodies ) {
        this.bodies = bodies;
    }

    public void apply( EnergySkateParkModule module ) {
        module.getEnergyConservationModel().removeAllBodies();
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = new Body( module.getEnergyConservationModel() );
            ( (BodyElement)bodies.get( i ) ).apply( body );
            module.getEnergyConservationModel().addBody( body );
        }

        module.getEnergyConservationModel().removeAllSplineSurfaces();
        for( int i = 0; i < splines.size(); i++ ) {
            module.getEnergyConservationModel().addSplineSurface( ( (SplineElement)splines.get( i ) ).toSplineSurface() );
        }
        module.addFloorSpline();
    }
}
