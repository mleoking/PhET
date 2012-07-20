// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.reactionsandrates.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.EventListener;

import edu.colorado.phet.common.collision.Collidable;
import edu.colorado.phet.common.collision.CollidableAdapter;
import edu.colorado.phet.common.mechanics.Body;
import edu.colorado.phet.common.phetcommon.math.MutableVector2D;
import edu.colorado.phet.common.phetcommon.util.EventChannel;

/**
 * AbstractMolecule
 * <p/>
 * The base class for all molecules, simple and composite.
 * <p/>
 * If an AbstractMolecule is part of a composite, its stepInTime() method becomes a noop, and
 * its kinematics are determined entirely by the stepInTime() method of the composite.
 * <p/>
 * A set of methods named getFull<xxx>() return properties of the AbstractMolecule if it is not
 * part of a composite, or the propertis of the composite if the AbstractMolecule is part one.
 *
 * @author Ron LeMaster
 */
abstract public class AbstractMolecule extends Body implements Collidable, KineticEnergySource, Cloneable {
    public static EventChannel classEventChannel = new EventChannel( ClassListener.class );

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------
    public static interface ClassListener extends EventListener {
        void statusChanged( AbstractMolecule molecule );
    }


    public static void addClassListener( ClassListener listener ) {
        classEventChannel.addListener( listener );
    }

    public static void removeClassListener( ClassListener listener ) {
        classEventChannel.removeListener( listener );
    }

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private CollidableAdapter collidableAdapter;
    private CompositeMolecule parentComposite;
    private ClassListener classListenerProxy = (ClassListener) classEventChannel.getListenerProxy();


    public Object clone() {
        AbstractMolecule clone = (AbstractMolecule) super.clone();

        clone.collidableAdapter = new CollidableAdapter( this );
        clone.parentComposite = parentComposite == null ? null : (CompositeMolecule) parentComposite.clone();

        return clone;
    }

    protected AbstractMolecule() {
        this( new Point2D.Double(), new MutableVector2D(), new MutableVector2D(), 0, 0 );
        collidableAdapter = new CollidableAdapter( this );
    }

    protected AbstractMolecule( Point2D location, MutableVector2D velocity, MutableVector2D acceleration, double mass, double charge ) {
        super( location, velocity, acceleration, mass, charge );
        collidableAdapter = new CollidableAdapter( this );
    }

    public void setPosition( double x, double y ) {
        if ( collidableAdapter != null ) {
            collidableAdapter.updatePosition();
        }
        super.setPosition( x, y );
    }

    public void setPosition( Point2D position ) {
        if ( collidableAdapter != null ) {
            collidableAdapter.updatePosition();
        }
        super.setPosition( position );
    }

    public MutableVector2D getVelocity() {
        return super.getVelocity();
    }

    public void setVelocity( MutableVector2D velocity ) {
        if ( collidableAdapter != null ) {
            collidableAdapter.updateVelocity();
        }
        super.setVelocity( velocity );
    }

    public void setVelocity( double vx, double vy ) {
        if ( collidableAdapter != null ) {
            collidableAdapter.updateVelocity();
        }
        super.setVelocity( vx, vy );
    }

    public MutableVector2D getVelocityPrev() {
        return collidableAdapter.getVelocityPrev();
    }

    public Point2D getPositionPrev() {
        return collidableAdapter.getPositionPrev();
    }

    public boolean isPartOfComposite() {
        return parentComposite != null;
    }

    public boolean isSimpleMolecule() {
        return !isPartOfComposite() && getComponentMolecules().length == 1;
    }

    public boolean isComposite() {
        return !isPartOfComposite() && getComponentMolecules().length > 1;
    }

    public boolean isWholeMolecule() {
        return !isPartOfComposite();
    }


    public CompositeMolecule getParentComposite() {
        return parentComposite;
    }

    public void setParentComposite( CompositeMolecule parentComposite ) {
        this.parentComposite = parentComposite;
        classListenerProxy.statusChanged( this );
        changeListenerProxy.compositeStateChanged( this );
    }

    /**
     * Returns the largest molecule of which this one is a component, or
     * this molecule, if it isn't
     *
     * @return an AbstractMolecule
     */
    public AbstractMolecule getFullMolecule() {
        if ( this.isPartOfComposite() ) {
            return getParentComposite().getFullMolecule();
        }
        else {
            return this;
        }
    }

    /**
     * If the molecule is part of a larger composite, return 0, because the
     * KE is taken care of by the composite
     *
     * @return the kinetic energy, if it's not part of a composite, 0 if it is
     */
    public double getKineticEnergy() {
        if ( !isPartOfComposite() ) {
            return super.getKineticEnergy();
        }
        else {
            return 0;
        }
    }

    /**
     * If the molecule is part of a larger composite, there should be no stepInTime
     * behavior. It will be taken care of by the CompositeMolecule
     *
     * @param dt
     */
    public void stepInTime( double dt ) {
        if ( !isPartOfComposite() ) {
            super.stepInTime( dt );
        }
    }

    /**
     * Sets the temperature of the molecule
     *
     * @param temperature
     */
    public void setTemperature( double temperature ) {
        double ke = getKineticEnergy();
        double r = Math.sqrt( ke != 0 ? temperature / ke : 1 );
        setVelocity( getVelocity().scale( r ) );
        setOmega( getOmega() * r );
    }

    /**
     * @param force
     * @param ptOfApplication
     */
    public void applyForce( MutableVector2D force, Point2D ptOfApplication ) {

        // Compute the torque
        // Get the vector from the cm to the point of application
        MutableVector2D r = new MutableVector2D( getCM(), ptOfApplication );
        // Torque = F x r
        double t = force.getCrossProductScalar( r );

        // Compute the angular acceleration
        this.setAlpha( t / getMomentOfInertia() );

        // Compute the acceleration
        this.setAcceleration( force.getX() / this.getMass(),
                              force.getY() / this.getMass() );
    }

    public String toString() {
        return getClass().getName() + hashCode();
    }

    //--------------------------------------------------------------------------------------------------
    //  Abstract methods
    //--------------------------------------------------------------------------------------------------

    abstract public SimpleMolecule[] getComponentMolecules();

    public final Collection getComponentMoleculesAsList() {
        return Arrays.asList( getComponentMolecules() );
    }

    abstract public Rectangle2D getBoundingBox();

    // Mass of molecule, or parent composite
    abstract public double getFullMass();

    // Kinetic energy of molecule or parent composite
    abstract public double getFullKineticEnergy();

    // CM of molecule or parent composite
    abstract public Point2D getFullCM();

    //--------------------------------------------------------------------------------------------------
    // Events and listeners
    //--------------------------------------------------------------------------------------------------
    public interface ChangeListener extends EventListener {
        void compositeStateChanged( AbstractMolecule molecule );
    }

    private EventChannel listenerChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener) listenerChannel.getListenerProxy();

    public void addListener( ChangeListener listener ) {
        listenerChannel.addListener( listener );
    }

    public void removeListener( ChangeListener listener ) {
        listenerChannel.removeListener( listener );
    }
}