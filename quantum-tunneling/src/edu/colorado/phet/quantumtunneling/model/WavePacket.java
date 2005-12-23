/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.model;

import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.enum.Direction;
import edu.colorado.phet.quantumtunneling.util.Complex;


/**
 * WavePacket is the model of a wave packet.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WavePacket extends AbstractWave implements ModelElement, Observer {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private IClock _clock;
    private TotalEnergy _te;
    private AbstractPotential _pe;
    private Direction _direction;
    private boolean _enabled;
    private double _width;
    private double _center;
    private SimulationTimeChangeListener _timeChangeListener;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public WavePacket( IClock clock ) {
        super();
        _timeChangeListener = new SimulationTimeChangeListener();
        _clock = clock;
        _clock.addClockListener( _timeChangeListener );
        _te = null;
        _pe = null;
        _direction = Direction.LEFT_TO_RIGHT;
        _enabled = true;
        _width = QTConstants.DEFAULT_PACKET_WIDTH;
        _center = QTConstants.DEFAULT_PACKET_CENTER;
    }
    
    public void cleanup() {
        _clock.removeClockListener( _timeChangeListener );
        _clock = null;
        if ( _te != null ) {
            _te.deleteObserver( this );
            _te = null;
        }
        if ( _pe != null ) {
            _pe.deleteObserver( this );
            _pe = null;
        }
    }
    
    private double getTime() {
        return _clock.getSimulationTime() * QTConstants.TIME_SCALE;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setEnabled( boolean enabled ) {
        if ( enabled != _enabled ) {
            _enabled = enabled;
            //XXX
        }
    }

    public boolean isEnabled() {
        return _enabled;
    }
    
    public void setWidth( double width ) {
        if ( width <= 0 ) {
            throw new IllegalArgumentException( "width must be > 0: " + width );
        }
        if ( width != _width ) {
            _width = width;
            //XXX
            notifyObservers();
        }
    }
    
    public double getWidth() {
        return _width;
    }
    
    public void setCenter( double center ) {
        if ( center != _center ) {
            _center = center;
            //XXX
            notifyObservers();
        }
    }
    
    public double getCenter() {
        return _center;
    }
    
    //----------------------------------------------------------------------------
    // AbstractWave implementation
    //----------------------------------------------------------------------------
    
    public void setTotalEnergy( TotalEnergy te ) {
        if ( _te != null ) {
            _te.deleteObserver( this );
        }
        _te = te;
        _te.addObserver( this );
        //XXX
        notifyObservers();
    }

    public TotalEnergy getTotalEnergy() {
        return _te;
    }

    public void setPotentialEnergy( AbstractPotential pe ) {
        if ( _pe != null ) {
            _pe.deleteObserver( this );
        }
        _pe = pe;
        _pe.addObserver( this );
        //XXX
        notifyObservers();
    }

    public AbstractPotential getPotentialEnergy() {
        return _pe;
    }

    public void setDirection( Direction direction ) {
        _direction = direction;
        //XXX
    }

    public Direction getDirection() {
        return _direction;
    }

    public WaveFunctionSolution solveWaveFunction( double x ) {
        WaveFunctionSolution solution = null;
        double t = getTime();
        solution = new WaveFunctionSolution( x, t, new Complex(0,0), new Complex(0,0) );//XXX HACK
        //XXX
        return solution;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    public void stepInTime( double dt ) {
        if ( _enabled ) {
            //XXX    
        }
    }

    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( _enabled ) {
            //XXX    
        }   
    }

    //----------------------------------------------------------------------------
    // ClockListener
    //----------------------------------------------------------------------------
    
    private class SimulationTimeChangeListener extends ClockAdapter {

        public void simulationTimeChanged( ClockEvent clockEvent ) {
            if ( _enabled && _te != null && _pe != null ) {
                notifyObservers();
            }
        }

        public void simulationTimeReset( ClockEvent clockEvent ) {
            if ( _enabled && _te != null && _pe != null ) {
                notifyObservers();
            }
        }
    }

}
