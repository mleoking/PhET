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

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.mechanics.Vector3D;
import edu.colorado.phet.mechanics.Body;
import edu.colorado.phet.molecularreactions.model.collision.HardBodyCollision;
import edu.colorado.phet.molecularreactions.model.reactions.Reaction;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;
import java.text.DecimalFormat;

/**
 * CompositeMolecule
 * <p/>
 * A composite molecule is a molecule composed of other molecules. Its position is its
 * center of mass.
 * <p/>
 * A CompsiteMolecule has an array of Bond instance. How they get added to the model is
 * not the responsibility of the CompositeMolecule class. Currently (9/27/2006), the MRModel
 * class does this itself when a CompositeMolecule is added to it.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
abstract public class CompositeMolecule extends AbstractMolecule implements PotentialEnergySource {
//    private Vector2D velocity;

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    private static int numSimpleMolecules( AbstractMolecule molecule ) {
        int n = 0;
        if( molecule instanceof SimpleMolecule ) {
            n = 1;
        }
        else if( molecule instanceof CompositeMolecule ) {
            AbstractMolecule[] components = ( (CompositeMolecule)molecule ).components;
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
    private Rectangle2D boundingBox = new Rectangle2D.Double();
    private double orientation;
    private Bond[] bonds;

    /**
     * Default constructor is protected
     */
    protected CompositeMolecule() {
    }

    /**
     * Creates a CompositeMolecule from an array of molecules. The kinematic
     * attributes of the new CompositeMolecule are set based on those of its
     * components.
     *
     * @param molecules
     */
    public CompositeMolecule( SimpleMolecule[] molecules ) {
        setComponents( molecules );
    }

    public Bond[] getBonds() {
        if( bonds == null && getComponentMolecules().length >= 1 ) {
            bonds = new Bond[]{new Bond( getComponentMolecules()[0], getComponentMolecules()[1] )};
        }
        return bonds;
    }

    /**
     * Set the components that make up the composite
     *
     * @param components
     */
    protected void setComponents( SimpleMolecule[] components ) {
        this.components = components;
        // Tell each of the components that they are now part of a composite
        for( int i = 0; i < components.length; i++ ) {
            AbstractMolecule component = components[i];
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

        // Remove the simple molecule from the composite
        List componentList = new ArrayList( Arrays.asList( components ) );
        componentList.remove( molecule );
        components = (SimpleMolecule[])componentList.toArray( new SimpleMolecule[componentList.size()] );
        molecule.setParentComposite( null );

        // Compute composite properties
        computeKinematicsFromComponents( components );

        // Compute the kinematics of the released molecule
        // todo: something may be wrong here. The velocity shouldn't be set to 0.
        HardBodyCollision collision = new HardBodyCollision();
        this.setMomentum( this.getMomentum().add( molecule.getMomentum() ) );
        molecule.setVelocity( 0, 0 );
        collision.detectAndDoCollision( this, molecule );
    }


    /**
     * Determines the CM (and position), velocity and acceleration of the
     * composite molecules from those of its components
     *
     * @param components
     */
    private void computeKinematicsFromComponents( AbstractMolecule[] components ) {

        computeCM();
        double mass = 0;
        Vector2D compositeMomentum = new Vector2D.Double();
        Vector2D acceleration = new Vector2D.Double();
        Vector3D angularMomentum = new Vector3D();
        Vector2D compositeCmToComponentCm = new Vector2D.Double();
        for( int i = 0; i < components.length; i++ ) {
            AbstractMolecule component = components[i];
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

        for( int i = 0; i < components.length; i++ ) {
            components[i].setVelocity( getVelocity() );
        }
    }

    protected AbstractMolecule getMoleculeOfType( Class moleculeType ) {
        AbstractMolecule m = null;
        for( int i = 0; i < components.length && m == null; i++ ) {
            if( moleculeType.isInstance( components[i] ) ) {
                m = components[i];
            }
        }

        // Check to see if we found something
        if( m == null ) {
            throw new RuntimeException( "internal error" );
        }
        return m;
    }

    public void translate( double dx, double dy ) {
        AbstractMolecule[] components = getComponentMolecules();
        for( int i = 0; components != null && i < components.length; i++ ) {
            AbstractMolecule component = components[i];
            component.translate( dx, dy );
        }
    }

    public SimpleMolecule[] getComponentMolecules() {
        return components;
    }

    public Rectangle2D getBoundingBox() {
        if( components.length >= 1 ) {
            boundingBox.setRect( components[0].getBoundingBox() );
            for( int i = 1; i < components.length; i++ ) {
                boundingBox = boundingBox.createUnion( components[i].getBoundingBox() );
            }
        }
        else {
            boundingBox.setRect( 0,0,0,0 );
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
            AbstractMolecule component = components[i];
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
            AbstractMolecule component = components[i];
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
        double omegaNew = omegaOld + getAlpha() * dt;

        // Update state attributes
        setOrientation( thetaNew );
        setOmega( omegaNew );
        setAlpha( alphaNew );

//        double vx = 0;
//        double vy = 0;
//        double px = 0;
//        double py = 0;
//        for( int i = 0; i < components.length; i++ ) {
//            Body body = (Body)components[i];
//            vx += body.getVelocity().getX() * body.getMass() / this.getMass();
//            vy += body.getVelocity().getY() * body.getMass() / this.getMass();
//            px += body.getPosition().getX() * body.getMass() / this.getMass();
//            py += body.getPosition().getY() * body.getMass() / this.getMass();
//        }
//        setVelocity( vx, vy );

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

    public double getFullMass() {
        return getMass();
    }

    public double getFullKineticEnergy() {
        return getKineticEnergy();
    }

    public Point2D getFullCM() {
        return getCM();
    }

    /**
     * Rotates the molecule through a specified angle
     *
     * @param theta
     */
    public void rotate( double theta ) {
        for( int i = 0; i < components.length; i++ ) {
            SimpleMolecule component = components[i];
            Vector2D v = new Vector2D.Double( getCM(), component.getPosition() );
            v.rotate( theta );
            component.setPosition( getCM().getX() + v.getX(), getCM().getY() + v.getY() );
        }
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
    // Inner classes
    //--------------------------------------------------------------------------------------------------

    /**
     * Adds the bonds of a CompositeMolecule to the model when the CompositeMolecule is added to
     * the model, and removes the bonds when the CompositeMolecule is removed from the model
     */
    public static class DependentModelElementMonitor extends PublishingModel.ModelListenerAdapter {
        private PublishingModel model;

        public DependentModelElementMonitor( PublishingModel model ) {
            this.model = model;
        }

        public void modelElementAdded( ModelElement element ) {
            if( element instanceof CompositeMolecule ) {
                CompositeMolecule cm = (CompositeMolecule)element;
                Bond[] bonds = cm.getBonds();
                for( int i = 0; i < bonds.length; i++ ) {
                    Bond bond = bonds[i];
                    if( !model.getModelElements().contains( bond ) ) {
                        model.addModelElement( bond );
                    }
                }
            }
        }

        public void modelElementRemoved( ModelElement element ) {
            if( element instanceof CompositeMolecule ) {
                CompositeMolecule cm = (CompositeMolecule)element;
                Bond[] bonds = cm.getBonds();
                for( int i = 0; i < bonds.length; i++ ) {
                    Bond bond = bonds[i];
                    if( model.getModelElements().contains( bond ) ) {
                        model.removeModelElement( bond );
                    }
                }
            }
        }
    }

//    public double getKineticEnergy() {
//        DecimalFormat df = new DecimalFormat( "#.0000");
//        System.out.println( "getOmega() = " + df.format( getOmega() ) + "\tgetSpeed() = " + df.format( getSpeed() ));
////        return super.getKineticEnergy();
//
//        double angKE = getMomentOfInertia() * getOmega()* getOmega()/ 2;
//        Vector2D v = new Vector2D.Double( );
//        for( int i = 0; i < components.length; i++ ) {
//            SimpleMolecule component = components[i];
//            v.add( component.getVelocity());
//        }
//        double ke = 0.5 * getMass() * v.getMagnitudeSq();
//        return ke + angKE;
//    }
}
