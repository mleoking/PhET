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
import edu.colorado.phet.mechanics.Body;
import edu.colorado.phet.mri.MriConfig;

import java.awt.geom.Point2D;
import java.util.Random;
import java.util.EventListener;

/**
 * Dipole
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Dipole extends Body {

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

    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

    private Random random = new Random();
    // Range of precession, in radians
    private double precession = MriConfig.InitialConditions.DIPOLE_PRECESSION;
    private Spin spin;
    private double orientation;

    public Dipole() {
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

    //----------------------------------------------------------------
    // Design & debug methods
    //----------------------------------------------------------------

    public void setPrecession( double precession ) {
        this.precession = precession;
    }
}
