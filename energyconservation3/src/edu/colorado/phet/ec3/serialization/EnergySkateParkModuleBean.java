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

    private void addBody( Body body ) {
        bodies.add( new BodyElement( body ) );
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
        module.getEnergyConservationModel().addFloorSpline();
    }

    public static class BodyElement {
        private Point2D.Double position;
        private Point2D.Double velocity;
        private Point2D.Double acceleration;

        private double cmRotation;
        private double angularVelocity;
        private double frictionCoefficient;
        private double mass;
        private double thermalEnergy;
        private boolean freeFrame;
        private double attachmentPointRotation;

        public BodyElement() {
        }

        public BodyElement( Body body ) {
            position = body.getPosition();
            velocity = new Point2D.Double( body.getVelocity().getX(), body.getVelocity().getY() );
            acceleration = new Point2D.Double( body.getAcceleration().getX(), body.getAcceleration().getY() );
            cmRotation = body.getCMRotation();
            angularVelocity = body.getAngularVelocity();
            frictionCoefficient = body.getFrictionCoefficient();
            mass = body.getMass();
            thermalEnergy = body.getThermalEnergy();
            freeFrame = body.isFreeFallMode();
            this.attachmentPointRotation = body.getAttachmentPointRotation();
        }

        public void apply( Body body ) {
            body.setAttachmentPointPosition( position );
            body.setVelocity( velocity.x, velocity.y );
            body.setAcceleration( acceleration.x, acceleration.y );
            body.setCMRotation( cmRotation );
            body.setAngularVelocity( angularVelocity );
            body.setFrictionCoefficient( frictionCoefficient );
            body.setMass( mass );
            body.setThermalEnergy( thermalEnergy );
            body.convertToFreefall( freeFrame );
            body.setAttachmentPointRotation( attachmentPointRotation );
        }

        public boolean isFreeFrame() {
            return freeFrame;
        }

        public void setFreeFrame( boolean freeFrame ) {
            this.freeFrame = freeFrame;
        }

        public double getAttachmentPointRotation() {
            return attachmentPointRotation;
        }

        public void setAttachmentPointRotation( double attachmentPointRotation ) {
            this.attachmentPointRotation = attachmentPointRotation;
        }

        public double getCmRotation() {
            return cmRotation;
        }

        public void setCmRotation( double cmRotation ) {
            this.cmRotation = cmRotation;
        }

        public double getAngularVelocity() {
            return angularVelocity;
        }

        public void setAngularVelocity( double angularVelocity ) {
            this.angularVelocity = angularVelocity;
        }

        public double getFrictionCoefficient() {
            return frictionCoefficient;
        }

        public void setFrictionCoefficient( double frictionCoefficient ) {
            this.frictionCoefficient = frictionCoefficient;
        }

        public double getMass() {
            return mass;
        }

        public void setMass( double mass ) {
            this.mass = mass;
        }

        public double getThermalEnergy() {
            return thermalEnergy;
        }

        public void setThermalEnergy( double thermalEnergy ) {
            this.thermalEnergy = thermalEnergy;
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

    }

    public static class SplineElement {
        private Point2D[] controlPoints;
        private boolean rollerCoaster;

        public SplineElement() {
        }

        public SplineElement( SplineSurface splineSurface ) {
            controlPoints = splineSurface.getSpline().getControlPoints();
            rollerCoaster = splineSurface.getSpline().isRollerCoasterMode();
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
            SplineSurface splineSurface = new SplineSurface( top );
            splineSurface.getSpline().setRollerCoasterMode( rollerCoaster );
            return splineSurface;
        }
    }
}
