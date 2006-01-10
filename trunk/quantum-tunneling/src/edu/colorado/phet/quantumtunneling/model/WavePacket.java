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

import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.ClockListener;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.enum.Direction;
import edu.colorado.phet.quantumtunneling.view.WaveFunctionPlot;


/**
 * WavePacket is the model of a wave packet.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WavePacket extends AbstractWave implements Observer, ClockListener {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double DX = QTConstants.POSITION_SAMPLE_STEP;
    private static final double DT = QTConstants.CLOCK_STEP;
    
    private static final int STEPS_PER_CLOCK_TICK = 3;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private TotalEnergy _te;
    private AbstractPotential _pe;
    private Direction _direction;
    private boolean _enabled;
    private double _width;
    private double _center;
    private double _time;
    private SchrodingerSolver _solver;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public WavePacket() {
        super();
        _te = null;
        _pe = null;
        _direction = Direction.LEFT_TO_RIGHT;
        _enabled = true;
        _width = QTConstants.DEFAULT_PACKET_WIDTH;
        _center = QTConstants.DEFAULT_PACKET_CENTER;
        _time = 0;
        _solver = new SchrodingerSolver( this, DX, DT );
    }
    
    public void cleanup() {
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
        return _time;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public SchrodingerSolver getSolver() {
        return _solver;
    }
    
    public void setEnabled( boolean enabled ) {
        if ( enabled != _enabled ) {
            _enabled = enabled;
            if ( _enabled ) {
                _solver.update();
                notifyObservers();
            }
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
            _solver.update();
            notifyObservers();
        }
    }
    
    public double getWidth() {
        return _width;
    }
    
    public void setCenter( double center ) {
        if ( center != _center ) {
            _center = center;
            _solver.update();
            notifyObservers();
        }
    }
    
    public double getCenter() {
        return _center;
    }
    
    public boolean isInitialized() {
        return ( _te != null && _pe != null );
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
        _solver.update();
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
        _solver.update();
        notifyObservers();
    }

    public AbstractPotential getPotentialEnergy() {
        return _pe;
    }

    public void setDirection( Direction direction ) {
        _direction = direction;
        _solver.update();
        notifyObservers();
    }

    public Direction getDirection() {
        return _direction;
    }

    public WaveFunctionSolution solveWaveFunction( double x ) {    
        return null; //XXX
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( _enabled ) {
            _solver.update();
            notifyObservers();    
        }
    }

    //----------------------------------------------------------------------------
    // ClockListener implementation
    //----------------------------------------------------------------------------

    public void clockTicked( ClockEvent clockEvent ) {
        if ( _enabled ) {
            for ( int i = 0; i < STEPS_PER_CLOCK_TICK; i++ ) {
                _solver.propogate();
            }
            notifyObservers();
        }
    }

    public void clockStarted( ClockEvent clockEvent ) {}

    public void clockPaused( ClockEvent clockEvent ) {}

    public void simulationTimeChanged( ClockEvent clockEvent ) {}

    public void simulationTimeReset( ClockEvent clockEvent ) {
        if ( _enabled ) {
            _solver.update();
            notifyObservers();
        }
    }
}
