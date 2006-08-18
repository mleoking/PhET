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
import edu.colorado.phet.mechanics.Vector3D;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * CompoundMolecule
 * <p/>
 * A compound molecule is a molecule composed of other molecules. Its position is its
 * center of mass.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CompoundMolecule extends Molecule {
    private Molecule[] components;
    private Rectangle2D boundingBox = new Rectangle2D.Double();
    private double orientation;

    /**
     * Creates a CompoundMolecule from an array of molecules. The kinematic
     * attributes of the new CompoundMolecule are set based on those of its
     * components.
     *
     * @param components
     */
    public CompoundMolecule( Molecule[] components ) {
        super();
        this.components = components;
        computeKinematicsFromComponents( components );
    }

    private void computeKinematicsFromComponents( Molecule[] components ) {
        computeCM();
        double mass = 0;
        Vector2D compositeMomentum = new Vector2D.Double( );
        Vector2D acceleration = new Vector2D.Double( );
        Vector3D angularMomentum = new Vector3D();
        Vector2D compositeCmToComponentCm = new Vector2D.Double( );
        for( int i = 0; i < components.length; i++ ) {
            Molecule component = components[i];
            mass += component.getMass();
            Vector2D momentum =  new Vector2D.Double( component.getVelocity()).scale( component.getMass() );
            compositeMomentum.add( momentum );
            acceleration.add( component.getAcceleration() );
            compositeCmToComponentCm.setComponents( component.getPosition().getX() - getCM().getX(),
                                                    component.getPosition().getY() - getCM().getY());
                        angularMomentum.add( compositeCmToComponentCm.crossProduct( momentum ));
        }
        setMass( mass );
        setVelocity( compositeMomentum.scale( 1 / mass ) );
        setAcceleration( acceleration );
        setOmega( angularMomentum.getZ() / getMomentOfInertia() );
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
        return getPosition();
    }

    private void computeCM() {
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
        setPosition( xSum / massSum, ySum / massSum );
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


        double alpha = 0;

        // 1. Compute new orientation
        double dTheta = ( omegaOld * dt ) + ( alpha * dt * dt / 2 );
        double thetaNew = ( thetaOld + dTheta ) % ( Math.PI * 2 );

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
        updateComponents( dTheta );

        notifyObservers();
    }

    /**
     * Updates the position of each component molecule.
     *
     * @param theta the rotation of the compound molecule
     */
    public void updateComponents( double theta ) {

        // Set the position and velocity of the component
        for( int i = 0; i < components.length; i++ ) {
            Molecule component = components[i];
            Vector2D compositeCmToComponentCm = new Vector2D.Double( component.getPosition().getX() - this.getPositionPrev().getX(),
                                                       component.getPosition().getY() - this.getPositionPrev().getY() );
            compositeCmToComponentCm.rotate( theta );
            component.setPosition( this.getPosition().getX() + compositeCmToComponentCm.getX(),
                                   this.getPosition().getY() + compositeCmToComponentCm.getY() );
            Vector2D v = component.getVelocity();
            v.setComponents( this.getVelocity().getX() + getOmega() * -compositeCmToComponentCm.getY(),
                             this.getVelocity().getY() + getOmega() * compositeCmToComponentCm.getX() );
        }
    }
}
