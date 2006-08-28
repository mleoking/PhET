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
import java.util.HashMap;

/**
 * CompositeMolecule
 * <p/>
 * A composite molecule is a molecule composed of other molecules. Its position is its
 * center of mass.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CompositeMolecule extends Molecule {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    private static int numSimpleMolecules( Molecule molecule ) {
        int n = 0;
        if( molecule instanceof SimpleMolecule ) {
            n = 1;
        }
        else if( molecule instanceof CompositeMolecule ) {
            Molecule[] components = ( (CompositeMolecule)molecule ).components;
            for( int i = 0; i < components.length; i++ ) {
                n += numSimpleMolecules( components[i] );
            }
        }
        return n;
    }

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private SimpleMolecule[] components;
    private Bond[] bonds;
    private Rectangle2D boundingBox = new Rectangle2D.Double();
    private double orientation;

    /**
     * Creates a CompositeMolecule from an array of molecules. The kinematic
     * attributes of the new CompositeMolecule are set based on those of its
     * components.
     *
     * @param components
     */
    public CompositeMolecule( SimpleMolecule[] components, Bond[] bonds ) {
        super();
        this.components = components;
        this.bonds = bonds;

        // Tell each of the components that they are now part of a composite
        for( int i = 0; i < components.length; i++ ) {
            Molecule component = components[i];
            component.setPartOfComposite( true );
        }

        // Compute compiste properties
        computeKinematicsFromComponents( components );
    }

    /**
     * Adds a component molecule to the composite molecule
     *
     * @param molecule
     */
    private void addComponent( SimpleMolecule molecule ) {
        // Add the molecule to the list of components
        SimpleMolecule[] newComponents = new SimpleMolecule[components.length + 1];
        for( int i = 0; i < components.length; i++ ) {
            newComponents[i] = components[i];
        }
        newComponents[newComponents.length - 1] = molecule;
        components = newComponents;

        // Factor in the new component's kinematics
        addComponentKinematics( molecule );

        // Tell the new component that it is part of a composite
        molecule.setPartOfComposite( true );
    }


    /**
     * Determines the CM (and position), velocity and acceleration of the
     * composite molecules from those of its components
     */
    private void addComponentKinematics( Molecule component ) {
        computeCM();
        double mass = 0;
        Vector2D compositeMomentum = getMomentum();
//        Vector2D acceleration = getAcceleration();
        Vector3D angularMomentum = new Vector3D( 0, 0, getOmega() * getMomentOfInertia() );
        Vector2D compositeCmToComponentCm = new Vector2D.Double();
        mass += component.getMass();
        Vector2D momentum = new Vector2D.Double( component.getVelocity() ).scale( component.getMass() );
        compositeMomentum.add( momentum );
//        acceleration.add( component.getAcceleration() );
        compositeCmToComponentCm.setComponents( component.getPosition().getX() - getCM().getX(),
                                                component.getPosition().getY() - getCM().getY() );
        angularMomentum.add( Vector3D.createCrossProduct( compositeCmToComponentCm, momentum ) );
        setMass( mass );
        setVelocity( compositeMomentum.scale( 1 / mass ) );
//        setAcceleration( acceleration );
        setOmega( angularMomentum.getZ() / getMomentOfInertia() );
    }

    /**
     * Determines the CM (and position), velocity and acceleration of the
     * composite molecules from those of its components
     *
     * @param components
     */
    private void computeKinematicsFromComponents( Molecule[] components ) {

        computeCM();
        double mass = 0;
        Vector2D compositeMomentum = new Vector2D.Double();
        Vector2D acceleration = new Vector2D.Double();
        Vector3D angularMomentum = new Vector3D();
        Vector2D compositeCmToComponentCm = new Vector2D.Double();
        for( int i = 0; i < components.length; i++ ) {
            Molecule component = components[i];
            mass += component.getMass();
            Vector2D momentum = new Vector2D.Double( component.getVelocity() ).scale( component.getMass() );
            compositeMomentum.add( momentum );
            acceleration.add( component.getAcceleration() );
            compositeCmToComponentCm.setComponents( component.getPosition().getX() - getCM().getX(),
                                                    component.getPosition().getY() - getCM().getY() );
            angularMomentum.add( Vector3D.createCrossProduct( compositeCmToComponentCm, momentum ) );
        }
        setMass( mass );
        setVelocity( compositeMomentum.scale( 1 / mass ) );
        setAcceleration( acceleration );
        setOmega( angularMomentum.getZ() / getMomentOfInertia() );
    }

    public Molecule[] getComponentMolecules() {
        return components;
    }

    public Bond[] getBonds() {
        return bonds;
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
     * @param theta the rotation of the composite molecule
     */
    public void updateComponents( double theta ) {

        // Set the position and velocity of the component
        for( int i = 0; i < components.length; i++ ) {
            SimpleMolecule component = components[i];
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

    public int numSimpleMolecules() {
        return numSimpleMolecules( this );
    }

    public double getKineticEnergy() {
        return super.getKineticEnergy();
    }
}
