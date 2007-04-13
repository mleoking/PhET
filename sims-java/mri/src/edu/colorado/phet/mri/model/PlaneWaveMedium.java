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

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.mri.util.IScalar;

import java.awt.geom.Point2D;
import java.util.EventListener;

/**
 * PlaneWaveMedium
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PlaneWaveMedium extends SimpleObservable implements ModelElement {

    public static class Direction extends Vector2D.Double {
        private Direction( double dx, double dy ) {
            super( dx, dy );
        }
    }

    public static Direction NORTH = new Direction( 0, -1 );
    public static Direction SOUTH = new Direction( 0, 1 );
    public static Direction EAST = new Direction( 1, 0 );
    public static Direction WEST = new Direction( 0, -1 );


    private IScalar source;
    private Point2D origin;
    private double lateralExtent;
    private Direction direction;
    private double speed;
    private double[] values;
    private double longitudinalExtent;

    /**
     * @param source
     * @param origin
     * @param lateralExtent      lateralExtent of the medium in distance units
     * @param longitudinalExtent longitudinalExtent of the medium in the direction the wave is traveling
     * @param direction
     * @param speed              number of distance units the wave travels in a time unit
     */
    public PlaneWaveMedium( IScalar source,
                            Point2D origin,
                            double lateralExtent,
                            double longitudinalExtent,
                            Direction direction,
                            double speed ) {
        this.source = source;
        this.origin = origin;
        this.lateralExtent = lateralExtent;
        this.direction = direction;
        this.speed = speed;
        this.longitudinalExtent = longitudinalExtent;

//        values = new double[ (int)( longitudinalExtent )];
        values = new double[ (int)( longitudinalExtent / speed )];
    }

    public IScalar getSource() {
        return source;
    }

    public Point2D getOrigin() {
        return origin;
    }

    public void setOrigin( Point2D origin ) {
        this.origin = origin;
    }

    public void setSpeed( double speed ) {
        this.speed = speed;
    }

    public double getLateralExtent() {
        return lateralExtent;
    }

    public void setLateralExtent( double lateralExtent ) {
        this.lateralExtent = lateralExtent;
    }

    public double getLongitudinalExtent() {
        return longitudinalExtent;
    }

    public void setLongitudinalExtent( double longitudinalExtent ) {
        this.longitudinalExtent = longitudinalExtent;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection( Direction direction ) {
        this.direction = direction;
    }

    public void stepInTime( double dt ) {
        double newValue = source.getValue();
        for( int i = values.length - 1; i > 0; i-- ) {
            values[i] = values[i - 1];
        }
        values[0] = newValue;
        notifyObservers();
    }

    public double getAmplitudeAt( double x ) {
        return values[(int)( x / speed )];
    }

    public double getSpeed() {
        return speed;
    }

    public void leaveSystem() {
        listenerProxy.leftSystem( this );
        eventChannel.removeAllListeners();
        removeAllObservers();
    }

    //--------------------------------------------------------------------------------------------------
    //
    //--------------------------------------------------------------------------------------------------
    public interface Listener extends EventListener {
        void leftSystem( PlaneWaveMedium planeWaveMedium );
    }

    private EventChannel eventChannel = new EventChannel( Listener.class );
    private Listener listenerProxy = (Listener)eventChannel.getListenerProxy();

    public void addListener( Listener listener ) {
        eventChannel.addListener( listener );
    }

    public void removeListener( Listener listener ) {
        eventChannel.removeListener( listener );
    }
}
