/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model;

import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * CompoundMolecule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CompoundMolecule extends Molecule {
    private Molecule[] components;
    private Rectangle2D boundingBox = new Rectangle2D.Double();
    private Point2D cm = new Point2D.Double();
    private double orientation;

    public CompoundMolecule( Molecule[] components ) {
        super();
        this.components = components;
    }

    public CompoundMolecule( Molecule[] components, Point2D location, Vector2D velocity, Vector2D acceleration, double mass, double charge ) {
        super( location, velocity, acceleration, mass, charge );
        this.components = components;
    }

    public Molecule[] getComponentMolecules() {
        return components;
    }

    public Rectangle2D getBoundingBox() {
        boundingBox.setRect( components[0].getBoundingBox() );
        for( int i = 1; i < components.length; i++ ) {
            boundingBox.createUnion( components[i].getBoundingBox() );
        }
        return boundingBox;
    }

    public Point2D getCM() {
        double xSum = 0;
        double ySum = 0;
        double massSum = 0;
        for( int i = 0; i < components.length; i++ ) {
            Molecule component = components[i];
            double mass = component.getMass();
            xSum += mass * component.getCM().getX();
            ySum += mass * component.getCM().getY();
            massSum += mass;
        }
        cm.setLocation( xSum / massSum, ySum / massSum );
        return cm;
    }

    public double getMomentOfInertia() {
        double moi = 0;
        Point2D cm = this.getCM();
        for( int i = 0; i < components.length; i++ ) {
            Molecule component = components[i];
            double dist = cm.distance( component.getCM() );
            double mOfIComponent = component.getMomentOfInertia() + component.getMass()
                                                                    * dist * dist;
            moi += mOfIComponent;
        }
        return moi;
    }

    public double getOrientation() {
        return orientation;
    }

    public void setOrientation( double orientation ) {
        this.orientation = orientation;
        notifyObservers();
    }

    public void stepInTime( double dt ) {

        double thetaOld = getOrientation();
        double omegaOld = getOmega();
        double alphaOld = getAlpha();

        double emfMag = 0;
        double phi = 0;

        double alpha = 0;
//        double alpha = s_c * Math.sin( phi ) * emfMag - s_b * omegaOld;

        // 1. Compute new orientation
        double thetaNew = ( thetaOld + ( omegaOld * dt ) + ( alpha * dt * dt / 2 ) ) % ( Math.PI * 2 );

        // 2. Compute temporary new angular velocity
        double omegaNewTemp = omegaOld + ( alpha * dt );

        // 3. Compute new angular acceleration
//        if( emf != null && emf.getLength() != 0 ) {
//            double emfOrientation = Math.atan2( emf.getY(), emf.getX() );
//            phi = emfOrientation - thetaNew;
//            emfMag = emf.getLength();
//        }
//        double alphaNew = s_c * Math.sin( phi ) * emfMag - s_b * omegaNewTemp;
        double alphaNew = alphaOld;

        // 4. Compute new angular velocity
        double omegaNew = omegaOld + ( ( alphaNew + alpha ) / 2 ) * dt;

        // Update state attributes
        setOrientation( thetaNew );
        setOmega( omegaNew );
        setAlpha( alphaNew );

        super.stepInTime( dt );

        // We have to wait until the lobes compute their linear kinematics
        // (which happens in super.stepInTime() before we update their angular positions
        // relative to the center of the molecule. Otherwise, their previous locations
        // don't get set properly. (Previous location is used in collision detection)
        updateLobes();

        notifyObservers();
    }

    /**
     *
     */
    public void updateLobes() {

        // Set the locations of the hydrogen atoms
        double x;
        double y;

//        lobes[0].setCenter( this.getLocation().getX(), this.getLocation().getY() );
//
//        x = this.getLocation().getX()
//                + s_hydrogenOxygenDist * Math.cos( dipoleOrientation + s_hydrogenAngleRad / 2 );
//        y = this.getLocation().getY()
//                + s_hydrogenOxygenDist * Math.sin( dipoleOrientation + s_hydrogenAngleRad / 2 );
//        lobes[1].setCenter( x, y );
//
//        x = this.getLocation().getX()
//                + s_hydrogenOxygenDist * Math.cos( dipoleOrientation - s_hydrogenAngleRad / 2 );
//        y = this.getLocation().getY()
//                + s_hydrogenOxygenDist * Math.sin( dipoleOrientation - s_hydrogenAngleRad / 2 );
//        lobes[2].setCenter( x, y );
//
//        super.setDipoleOrientation( dipoleOrientation );

    }

}
