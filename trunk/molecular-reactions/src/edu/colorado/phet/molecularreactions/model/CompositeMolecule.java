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
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.mechanics.Vector3D;
import edu.colorado.phet.molecularreactions.model.collision.HardBodyCollision;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

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
    public static class Type {
        private Type(){}
    }
    public static Type AA = new Type();
    public static Type BB = new Type();
    public static Type AB = new Type();
    public static Type BC = new Type();
    public static Type OTHER = new Type();

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
    // Events and listeners
    //--------------------------------------------------------------------------------------------------
    public interface Listener extends EventListener {
        void componentAdded( SimpleMolecule component, Bond bond );

        void componentRemoved( SimpleMolecule component, Bond bond );
    }

    private EventChannel eventChannel = new EventChannel( Listener.class );
    Listener listenerProxy = (Listener)eventChannel.getListenerProxy();

    public void addListener( Listener listener ) {
        eventChannel.addListener( listener );
    }

    public void removeListener( Listener listener ) {
        eventChannel.removeListener( listener );
    }

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private SimpleMolecule[] components;
    private Bond[] bonds;
    private Rectangle2D boundingBox = new Rectangle2D.Double();
    private double orientation;

    /**
     * Creates a CompositeMolecule from two simple molecules
     *
     * @param sm1
     * @param sm2
     */
    public CompositeMolecule( SimpleMolecule sm1, SimpleMolecule sm2 ) {
        this( new SimpleMolecule[]{sm1, sm2}, new Bond[]{new Bond( sm1, sm2 )} );
    }

    /**
     * Creates a CompositeMolecule from an array of molecules. The kinematic
     * attributes of the new CompositeMolecule are set based on those of its
     * components.
     *
     * @param components
     */
    public CompositeMolecule( SimpleMolecule[] components, Bond[] bonds ) {
        this.components = components;
        this.bonds = bonds;

        // Tell each of the components that they are now part of a composite
        for( int i = 0; i < components.length; i++ ) {
            Molecule component = components[i];
            component.setParentComposite( this );
        }

        // Compute composite properties
        computeKinematicsFromComponents( components );
    }

    /**
     * Removes a component molecule from the composite.
     * <p/>
     * A component can only be removed from the end of a molecule. Throws a
     * RuntimeException if requested to remove a component that isn't on the end
     *
     * @param molecule
     */
    public void removeSimpleMolecule( SimpleMolecule molecule ) {
        // Find the bonds that the component participates in. If there is more than one,
        // throw an exception
        Bond bond = null;
        for( int i = 0; i < getBonds().length && bond == null; i++ ) {
            Bond testBond = getBonds()[i];
            if( testBond.getParticipants()[0] == molecule ) {
                if( bond != null ) {
                    throw new RuntimeException( "attempt to remove an inner component" );
                }
                else {
                    bond = testBond;
                }
            }
            if( testBond.getParticipants()[1] == molecule ) {
                if( bond != null ) {
                    throw new RuntimeException( "attempt to remove an inner component" );
                }
                else {
                    bond = testBond;
                }
            }
        }

        // Remove the simple molecule from the composite
        List componentList = new ArrayList( Arrays.asList( components ) );
        componentList.remove( molecule );
        components = (SimpleMolecule[])componentList.toArray( new SimpleMolecule[componentList.size()] );
        molecule.setParentComposite( null );

        // Remove the bond from our list of bonds
        List bondList = new ArrayList( Arrays.asList( bonds ) );
        bondList.remove( bond );
        bonds = (Bond[])bondList.toArray( new Bond[bondList.size()] );

        // Compute composite properties
        computeKinematicsFromComponents( components );

        // Compute the kinematics of the released molecule
        // todo: something may be wrong here. The velocity shouldn't be set to 0.
        HardBodyCollision collision = new HardBodyCollision();
        molecule.setVelocity( 0,0 );
        collision.detectAndDoCollision( this, molecule );

        listenerProxy.componentRemoved( molecule, bond );
    }

    /**
     * Adds a component molecule to the composite molecule
     *
     * @param molecule
     */
    public void addSimpleMolecule( SimpleMolecule molecule, Bond bond ) {

        // Place the new molecule so it's in line with the current components
//        setNewComponentPosition( bond, molecule );

        // Add the molecule to the list of components
        List componentList = new ArrayList( Arrays.asList( components ) );
        componentList.add( molecule );
        components = (SimpleMolecule[])componentList.toArray( new SimpleMolecule[componentList.size()] );

        // Add the bond to the list of bonds
        List bondList = new ArrayList( Arrays.asList( bonds ));
        bondList.add( bond );
        bonds = (Bond[])bondList.toArray( new Bond[bondList.size()] );

        // Factor in the new component's kinematics
//        addComponentKinematics( molecule );
        this.computeKinematicsFromComponents( components );

        // Tell the new component that it is part of a composite
        molecule.setParentComposite( this );

        // Notify listeners
        listenerProxy.componentAdded( molecule, bond );

        // DEBUG!!!
        // For now, release the other component in the molecule. There should be a better way to do this
        SimpleMolecule moleculeToRemove = null;
        for( int i = 0; i < components.length; i++ ) {
            SimpleMolecule component = components[i];
            if( component != bond.getParticipants()[0] && component != bond.getParticipants()[1] ) {
                moleculeToRemove = component;
                break;
            }
        }
        if( moleculeToRemove != null ) {
            removeSimpleMolecule( moleculeToRemove );
        }
    }

    /**
     * Repositions a new component so it is in line with the exisiting components of the molecule
     *
     * @param bond
     * @param molecule
     */
    private void setNewComponentPosition( Bond bond, SimpleMolecule molecule ) {
        SimpleMolecule existingComponent = bond.getParticipants()[0] == molecule ? bond.getParticipants()[1]
                                           : bond.getParticipants()[1];
        Bond[] existingBonds = getBonds();
        Bond bondToAlignWith = null;
        Point2D rootPosition = null;
        for( int i = 0; i < existingBonds.length && bondToAlignWith == null; i++ ) {
            Bond existingBond = existingBonds[i];
            if( existingBond.getParticipants()[0] == existingComponent ) {
                rootPosition = existingBond.getParticipants()[1].getPosition();
                bondToAlignWith = existingBond;
            }
            if( existingBond.getParticipants()[1] == existingComponent ) {
                rootPosition = existingBond.getParticipants()[0].getPosition();
                bondToAlignWith = existingBond;
            }
        }
        Vector2D v1 = new Vector2D.Double( existingComponent.getPosition().getX() - rootPosition.getX(),
                                           existingComponent.getPosition().getY() - rootPosition.getY() );
        Vector2D v2 = new Vector2D.Double( molecule.getPosition().getX() - existingComponent.getPosition().getX(),
                                           molecule.getPosition().getY() - existingComponent.getPosition().getY() );
        v2.rotate( v1.getAngle() - v2.getAngle() );
        molecule.setPosition( existingComponent.getPosition().getX() + v2.getX(),
                              existingComponent.getPosition().getY() + v2.getY() );
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

    public SimpleMolecule[] getComponentMolecules() {
        return components;
    }

    public Bond[] getBonds() {
        return bonds;
    }

    public Rectangle2D getBoundingBox() {
        boundingBox.setRect( components[0].getBoundingBox() );
        for( int i = 1; i < components.length; i++ ) {
            boundingBox = boundingBox.createUnion( components[i].getBoundingBox() );
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

//        computeCM();

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
//            v.setComponents( v.getX() + getOmega() * -compositeCmToComponentCm.getY(),
//                             v.getY() +getOmega() * compositeCmToComponentCm.getX() );
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

    public Type getType() {
        if( numSimpleMolecules() !=  2) {
            return OTHER;
        }
        if( components[0] instanceof MoleculeA && components[1] instanceof MoleculeA ) {
            return AA;
        }
        if( components[0] instanceof MoleculeA && components[1] instanceof MoleculeA ) {
            return AA;
        }
        if( components[0] instanceof MoleculeA && components[1] instanceof MoleculeB ) {
            return AB;
        }
        if( components[0] instanceof MoleculeB && components[1] instanceof MoleculeA ) {
            return AB;
        }
        if( components[0] instanceof MoleculeB && components[1] instanceof MoleculeB ) {
            return BB;
        }
        if( components[0] instanceof MoleculeB && components[1] instanceof MoleculeC ) {
            return BC;
        }
        if( components[0] instanceof MoleculeC && components[1] instanceof MoleculeB ) {
            return BC;
        }
        return OTHER;
    }
}
