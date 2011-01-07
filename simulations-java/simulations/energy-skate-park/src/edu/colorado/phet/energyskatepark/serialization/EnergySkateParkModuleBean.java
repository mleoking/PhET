// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.serialization;

import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;
import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.SkaterCharacterSet;
import edu.colorado.phet.energyskatepark.model.Body;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkSpline;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Nov 7, 2006
 * Time: 12:49:52 PM
 */

public class EnergySkateParkModuleBean implements IProguardKeepClass {
    private ArrayList bodies = new ArrayList();
    private ArrayList splines = new ArrayList();
    private double gravity = EnergySkateParkModel.G_EARTH;

    public EnergySkateParkModuleBean() {
    }

    public EnergySkateParkModuleBean( EnergySkateParkModule module ) {
        for( int i = 0; i < module.getEnergySkateParkModel().getNumBodies(); i++ ) {
            addBody( module.getEnergySkateParkModel().getBody( i ) );
        }
        for( int i = 0; i < module.getEnergySkateParkModel().getNumSplines(); i++ ) {
            EnergySkateParkSpline splineSurface = module.getEnergySkateParkModel().getSpline( i );
            addSplineSurface( splineSurface );
        }
        this.gravity = module.getEnergySkateParkModel().getGravity();
    }

    public double getGravity() {
        return gravity;
    }

    public void setGravity( double gravity ) {
        this.gravity = gravity;
    }

    private void addSplineSurface( EnergySkateParkSpline splineSurface ) {
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
        module.getEnergySkateParkModel().removeAllBodies();
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = module.createBody();
            ( (BodyElement)bodies.get( i ) ).apply( body );
            module.getEnergySkateParkModel().addBody( body );
        }

        module.getEnergySkateParkModel().removeAllSplineSurfaces();
        for( int i = 0; i < splines.size(); i++ ) {
            SplineElement spline = (SplineElement)splines.get( i );
            module.getEnergySkateParkModel().addSplineSurface( spline.toEnergySkateParkSpline() );
        }
        module.getEnergySkateParkModel().updateFloorState();
        module.getEnergySkateParkModel().setGravity( gravity );
        module.setSkaterCharacter( SkaterCharacterSet.getDefaultCharacter() );
    }


    public static class BodyElement implements IProguardKeepClass {
        private Point2D.Double position;
        private Point2D.Double velocity;
        private Point2D.Double acceleration;

        private double angularVelocity;
        private double frictionCoefficient;
        private double mass;
        private double thermalEnergy;
        private double attachmentPointRotation;
        private boolean freeFrame;
        private BodyElement restorePoint;

        public BodyElement() {
        }

        public BodyElement( Body body ) {
            position = new Point2D.Double( body.getPosition().getX(), body.getPosition().getY() );
            velocity = new Point2D.Double( body.getVelocity().getX(), body.getVelocity().getY() );
            angularVelocity = body.getAngularVelocity();
            frictionCoefficient = body.getFrictionCoefficient();
            mass = body.getMass();
            thermalEnergy = body.getThermalEnergy();
            freeFrame = body.isFreeFallMode();
            this.attachmentPointRotation = body.getAngle();
            this.restorePoint = body.isRestorePointSet() ? new BodyElement( body.getRestorePoint() ) : null;
        }

        public BodyElement getRestorePoint() {
            return restorePoint;
        }

        public void setRestorePoint( BodyElement restorePoint ) {
            this.restorePoint = restorePoint;
        }

        public void apply( Body body ) {
            body.setPosition( position.getX(), position.getY() );
            body.setVelocity( velocity.x, velocity.y );

            body.setAngularVelocity( angularVelocity );
            body.setFrictionCoefficient( frictionCoefficient );
            body.setMass( mass );

            if( restorePoint != null ) {
                Body restored = new Body( body.getWidth(), body.getHeight(), body.getParticleStage(), body.getGravity(), body.getZeroPointPotentialY(), body.getSkaterCharacter() );
                restorePoint.apply( restored );
                body.setRestorePoint( restored );
            }
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

    public static class SplineElement implements IProguardKeepClass {
        private Point2D[] controlPoints;
        private boolean rollerCoaster;

        public SplineElement() {
        }

        public SplineElement( EnergySkateParkSpline splineSurface ) {
            SerializablePoint2D[] pts = splineSurface.getControlPoints();
            controlPoints = new Point2D[pts.length];
            for( int i = 0; i < pts.length; i++ ) {
                controlPoints[i] = new Point2D.Double( pts[i].getX(), pts[i].getY() );
            }
            rollerCoaster = splineSurface.isRollerCoasterMode();
        }

        public Point2D[] getControlPoints() {
            return controlPoints;
        }

        public boolean isRollerCoaster() {
            return rollerCoaster;
        }

        public void setRollerCoaster( boolean rollerCoaster ) {
            this.rollerCoaster = rollerCoaster;
        }

        public void setControlPoints( Point2D[] controlPoints ) {
            this.controlPoints = controlPoints;
        }

        public EnergySkateParkSpline toEnergySkateParkSpline() {
            SerializablePoint2D[] pt = new SerializablePoint2D[controlPoints.length];
            for( int i = 0; i < pt.length; i++ ) {
                pt[i] = new SerializablePoint2D( controlPoints[i] );
            }
            EnergySkateParkSpline skateParkSpline = new EnergySkateParkSpline( pt );
            skateParkSpline.setRollerCoasterMode( rollerCoaster );
            return skateParkSpline;
        }
    }
}
