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

import edu.colorado.phet.mechanics.Body;
import edu.colorado.phet.collision.Collidable;
import edu.colorado.phet.collision.CollidableAdapter;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.common.math.Vector2D;

import java.util.EventListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

/**
 * Molecule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
abstract public class AbstractMolecule extends Body implements Collidable {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------
    public static interface ClassListener extends EventListener {
        void statusChanged( AbstractMolecule molecule );
    }

    public static EventChannel classEventChannel = new EventChannel( ClassListener.class );
    private ClassListener classListenerProxy = (ClassListener)classEventChannel.getListenerProxy();

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

    protected AbstractMolecule() {
        this( new Point2D.Double(), new Vector2D.Double(), new Vector2D.Double(), 0, 0 );
        collidableAdapter = new CollidableAdapter( this );
    }

    protected AbstractMolecule( Point2D location, Vector2D velocity, Vector2D acceleration, double mass, double charge ) {
        super( location, velocity, acceleration, mass, charge );
        collidableAdapter = new CollidableAdapter( this );
    }

    public void setPosition( double x, double y ) {
        if( collidableAdapter != null ) {
            collidableAdapter.updatePosition();
        }
        super.setPosition( x, y );
    }

    public void setPosition( Point2D position ) {
        if( collidableAdapter != null ) {
            collidableAdapter.updatePosition();
        }
        super.setPosition( position );
    }

    public Vector2D getVelocity() {
        return super.getVelocity();
    }

    public void setVelocity( Vector2D velocity ) {
        if( collidableAdapter != null ) {
            collidableAdapter.updateVelocity();
        }
        super.setVelocity( velocity );
    }

    public void setVelocity( double vx, double vy ) {
        if( collidableAdapter != null ) {
            collidableAdapter.updateVelocity();
        }
        super.setVelocity( vx, vy );
    }

    public Vector2D getVelocityPrev() {
        return collidableAdapter.getVelocityPrev();
    }

    public Point2D getPositionPrev() {
        return collidableAdapter.getPositionPrev();
    }

    public boolean isPartOfComposite() {
        return parentComposite != null;
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
     * @return an AbstractMolecule 
     */
    public AbstractMolecule getFullMolecule() {
        if( this.isPartOfComposite() ) {
            return getParentComposite().getFullMolecule();
        }
        else {
            return this;
        }
    }

    /**
     * If the molecule is part of a larger composite, there should be no stepInTime
     * behavior. It will be taken care of by the CompositeMolecule
     *
     * @param dt
     */
    public void stepInTime( double dt ) {
        if( !isPartOfComposite() ) {
            super.stepInTime( dt );
        }
    }

    public void applyForce( Vector2D force, Point2D ptOfApplication ) {

        // Compute the torque
        // Get the vector from the cm to the point of application
        Vector2D r = new Vector2D.Double( getCM(), ptOfApplication );
        // Torque = F x r
        double t = force.getCrossProductScalar( r );

        // Compute the angular acceleration
        this.setAlpha( t / getMomentOfInertia() );

        // Compute the acceleration
        this.setAcceleration( force.getX() / this.getMass(),
                              force.getY() / this.getMass() );
    }

    //--------------------------------------------------------------------------------------------------
    //  Abstract methods
    //--------------------------------------------------------------------------------------------------

    abstract public SimpleMolecule[] getComponentMolecules();

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
    private ChangeListener changeListenerProxy = (ChangeListener)listenerChannel.getListenerProxy();

    public void addListener( ChangeListener listener ) {
        listenerChannel.addListener( listener );
    }

    public void removeListener( ChangeListener listener ) {
        listenerChannel.removeListener( listener );
    }

}