/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.model;

import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.mechanics.Body;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.quantum.model.Photon;
import edu.colorado.phet.collision.Collidable;
import edu.colorado.phet.collision.CollidableAdapter;

import java.awt.geom.Point2D;
import java.util.Random;
import java.util.EventListener;
import java.util.EventObject;

/**
 * Dipole
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Dipole extends Body implements Collidable {

    //----------------------------------------------------------------
    // Class field and methods
    //----------------------------------------------------------------
    private static EventChannel classEventChannel = new EventChannel( ClassListener.class );
    private static ClassListener classListenerProxy = (ClassListener)classEventChannel.getListenerProxy();

    public static void addClassListener( ClassListener listener ) {
        classEventChannel.addListener( listener );
    }

    public static void removeClassListener( ClassListener listener ) {
        classEventChannel.removeListener( listener );
    }

    public interface ClassListener extends EventListener {
        void instanceCreated( Dipole dipole );
        void instanceDestroyed( Dipole dipole );
    }

    private static double RADIUS = 10;

    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

    private Random random = new Random();
    // Range of precession, in radians
    private double precession = MriConfig.InitialConditions.DIPOLE_PRECESSION;
    private Spin spin;
    private double orientation;
    private CollidableAdapter collidableAdapter;

    public Dipole() {
        collidableAdapter = new CollidableAdapter( this );

        classListenerProxy.instanceCreated( this );
    }

    public void stepInTime( double dt ) {
        double baseOrientation = ( spin == Spin.UP ? 1 : -1 ) * Math.PI / 2;
        setOrientation( baseOrientation + precession * random.nextDouble() * MathUtil.nextRandomSign() );
        notifyObservers();
    }

    public double getOrientation() {
        return orientation;
    }

    public void setOrientation( double orientation ) {
        this.orientation = orientation;
        notifyObservers();
    }

    public Spin getSpin() {
        return spin;
    }

    public void setSpin( Spin spin ) {
        this.spin = spin;
        notifyObservers();
    }

    public Point2D getCM() {
        return getPosition();
    }

    public double getMomentOfInertia() {
        return 0;
    }

    public double getRadius() {
        return RADIUS;
    }

    public void setVelocity( double vx, double vy ) {
        collidableAdapter.updateVelocity();
        super.setVelocity( vx, vy );
    }

    public void setPosition( double x, double y ) {
        collidableAdapter.updatePosition();
        super.setPosition( x, y );
    }

    public void setPosition( Point2D position ) {
        collidableAdapter.updatePosition();
        super.setPosition( position );
    }

    public Vector2D getVelocityPrev() {
        return collidableAdapter.getVelocityPrev();
    }

    public Point2D getPositionPrev() {
        return collidableAdapter.getPositionPrev();
    }

    public void collideWithPhoton( Photon photon ) {
        setSpin( Spin.DOWN );
        notifyObservers();
    }

    //----------------------------------------------------------------
    // Events and listeners
    //----------------------------------------------------------------
    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addChangeListener ( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener ( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    public class ChangeEvent extends EventObject {
        public ChangeEvent( Dipole source ) {
            super( source );
        }

        public Dipole getDipole() {
            return (Dipole)getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        public void orientationChanged( ChangeEvent event );
    }

    //----------------------------------------------------------------
    // Design & debug methods
    //----------------------------------------------------------------

    public void setPrecession( double precession ) {
        this.precession = precession;
    }
}
