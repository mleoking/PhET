/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.quantum.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.EventChannel;

import java.awt.geom.Point2D;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Random;

/**
 * ElectronSource
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ElectronSource implements ModelElement {

    //-----------------------------------------------------------------
    // Class data
    //-----------------------------------------------------------------

    public static Object SINGLE_SHOT_MODE = new Object();
    public static Object CONTINUOUS_MODE = new Object();
    private static HashSet electronProductionModes = new HashSet();

    static {
        electronProductionModes.add( SINGLE_SHOT_MODE );
        electronProductionModes.add( CONTINUOUS_MODE );
    }

    //-----------------------------------------------------------------
    // Instance data
    //-----------------------------------------------------------------

    private Random random = new Random( System.currentTimeMillis() );
    private double electronsPerSecond;
    private double timeSincelastElectronEmitted;
    private ElectromotiveForce emf;
//    private DischargeLampModel emf;
    private Point2D p1;
    private Point2D p2;
    private Plate plate;
    private Object electronProductionMode;
//    private BaseModel model;

    /**
     * Emits electrons along a line between two points
     *
     * @param emf
     * @param p1    One endpoint of the line
     * @param p2    The other endpoint of the line
     */
    public ElectronSource( ElectromotiveForce emf, Point2D p1, Point2D p2, Plate plate ) {
//    public ElectronSource( DischargeLampModel emf, Point2D p1, Point2D p2, Plate plate ) {
//        this.model = model;
        this.emf = emf;
        this.p1 = p1;
        this.p2 = p2;
        this.plate = plate;
    }

    /**
     * Produces electrons when the time is right
     *
     * @param dt
     */
    public void stepInTime( double dt ) {
        timeSincelastElectronEmitted += dt;

        // Note that we only produce one electron at a time. Otherwise, we get a bunch of
        // electrons produced if the electronsPerSecond is suddently increased, especially
        // if it had been 0.
        double period = 1 / electronsPerSecond;
        if( timeSincelastElectronEmitted > period && electronProductionMode == CONTINUOUS_MODE ) {
            timeSincelastElectronEmitted = 0;
            produceElectron();
        }
    }

    /**
     * Produce a single electron, and notify all listeners that it has happened.
     * <p/>
     * When an electron is produced, its initial position must be away from the source, so it is not
     * immediately captured if the source is part or a composite object that includes a sink.
     */
    public Electron produceElectron() {
        Electron electron = null;
        if( plate.getPotential() > 0 ) {
            electron = new Electron();

            // Determine where the electron will be emitted from
            double x = random.nextDouble() * ( p2.getX() - p1.getX() ) + p1.getX();
            double y = random.nextDouble() * ( p2.getY() - p1.getY() ) + p1.getY();
            Vector2D direction = new Vector2D.Double( emf.getElectronAcceleration() );
            if( direction.getMagnitude() > 0 ) {
                direction.normalize().scale( Electron.ELECTRON_RADIUS );
            }
            electron.setPosition( x + direction.getX(), y + direction.getY() );
//            model.addModelElement( electron );
            electronProductionListenerProxy.electronProduced( new ElectronProductionEvent( this, electron ) );
        }
        return electron;
    }

    //-----------------------------------------------------------------
    // Getters and setters
    //-----------------------------------------------------------------
    public double getElectronsPerSecond() {
        return electronsPerSecond;
    }

    public void setElectronsPerSecond( double electronsPerSecond ) {
        this.electronsPerSecond = electronsPerSecond;
    }

    public void setCurrent( double current ) {
        setElectronsPerSecond( current );
    }

    /**
     * Sets the length of the electrode. Fields p1 and p2 are modified
     *
     * @param newLength
     */
    public void setLength( double newLength ) {
        double x0 = ( p1.getX() + p2.getX() ) / 2;
        double y0 = ( p1.getY() + p2.getY() ) / 2;

        double currLength = p1.distance( p2 );
        double ratio = newLength / currLength;
        p1.setLocation( x0 + ( p1.getX() - x0 ) * ratio, y0 + ( p1.getY() - y0 ) * ratio );
        p2.setLocation( x0 + ( p2.getX() - x0 ) * ratio, y0 + ( p2.getY() - y0 ) * ratio );
    }

    /**
     * @param electronProductionMode
     */
    public void setElectronProductionMode( Object electronProductionMode ) {
        if( !electronProductionModes.contains( electronProductionMode ) ) {
            throw new RuntimeException( "Invalid parameter " );
        }
        this.electronProductionMode = electronProductionMode;
    }

    //----------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------
    private EventChannel listenerChannel = new EventChannel( ElectronProductionListener.class );
    private ElectronProductionListener electronProductionListenerProxy = (ElectronProductionListener)listenerChannel.getListenerProxy();

    public ElectronProductionListener getElectronProductionListenerProxy() {
        return electronProductionListenerProxy;
    }

    public interface ElectronProductionListener extends EventListener {
        void electronProduced( ElectronProductionEvent event );
    }

    /**
     * Event class for the production of electrons
     */
    public static class ElectronProductionEvent extends EventObject {
        private Electron electron;

        public ElectronProductionEvent( Object source, Electron electron ) {
            super( source );
            this.electron = electron;
        }

        public Electron getElectron() {
            return electron;
        }
    }

    public void addListener( ElectronProductionListener electronProductionListener ) {
        listenerChannel.addListener( electronProductionListener );
    }

    public void removeListener( ElectronProductionListener electronProductionListener ) {
        listenerChannel.removeListener( electronProductionListener );
    }
}
