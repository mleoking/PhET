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
import edu.colorado.phet.quantumtunneling.enums.Direction;
import edu.colorado.phet.quantumtunneling.util.Complex;


/**
 * PlaneWave is the model of a plane wave.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PlaneWave extends AbstractWave implements Observer, ClockListener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private TotalEnergy _te;
    private AbstractPotential _pe;
    private AbstractPlaneSolver _solver;
    private Direction _direction;
    private boolean _enabled;
    private double _time;
    private boolean _measureEnabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public PlaneWave() {
        super();
        _te = null;
        _pe = null;
        _solver = null;
        _direction = Direction.LEFT_TO_RIGHT;
        _enabled = true;
        _time = 0;
        _measureEnabled = false;
    }
    
    /**
     * Call this method before releasing all references to an object of this type.
     */
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
    
    //----------------------------------------------------------------------------
    // AbstractWave implementation
    //---------------------------------------------------------------------------- 
    
    /**
     * Is this object fully initialized?
     * @return true or false
     */
    public boolean isInitialized() {
        return ( _te != null && _pe != null && _solver != null );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Enables or disables this object.
     * @param enabled
     */
    public void setEnabled( boolean enabled ) {
        if ( enabled != _enabled ) {
            _enabled = enabled;
            updateSolver();
            notifyObservers();
        }
    }
    
    /**
     * Is this object enabled?
     * @return true or false
     */
    public boolean isEnabled() {
        return _enabled;
    }
    
    /**
     * Gets the current simulation time.
     * The value returned is valid only if the object is enabled.
     * @return
     */
    private double getTime() {
        return _time;
    }
    
    /**
     * Sets the total energy model.
     * @param te
     */
    public void setTotalEnergy( TotalEnergy te ) {
        if ( _te != null ) {
            _te.deleteObserver( this );
        }
        _te = te;
        _te.addObserver( this );
        updateSolver();
        notifyObservers();
    }
    
    /**
     * Gets the total energy model.
     * @return
     */
    public TotalEnergy getTotalEnergy() { 
        return _te;
    }
    
    /**
     * Sets the potential energy model.
     * @param pe
     */
    public void setPotentialEnergy( AbstractPotential pe ) {
        if ( _pe != null ) {
            _pe.deleteObserver( this );
        }
        _pe = pe;
        _pe.addObserver( this );
        updateSolver();
        notifyObservers();
    }
    
    /**
     * Gets the potential energy model.
     * @return
     */
    public AbstractPotential getPotentialEnergy() {
        return _pe;
    }
    
    /**
     * Sets the wave's direction.
     * @param direction
     */
    public void setDirection( Direction direction ) {
        _direction = direction;
        if ( _solver != null ) {
            _solver.setDirection( direction );
        }
    }

    /**
     * Gets the wave's direction.
     * @return
     */
    public Direction getDirection() {
        return _direction;
    }
    
    /**
     * Solves the wave function for a specified position.
     * @param x positon, in model corrdinates
     * @return
     */
    public WaveFunctionSolution solveWaveFunction( double x ) {
        WaveFunctionSolution solution = null;
        if ( _solver != null ) {
            final double t = getTime();
            if ( _measureEnabled ) {
                // When a measurement is made for a plane wave, the value is zero everywhere.
                solution = new WaveFunctionSolution( x, t, Complex.ZERO, Complex.ZERO );
            }
            else {
                solution = _solver.solve( x, t );
            }
        }
        return solution;
    }
    
    /*
     * Updates the solver.
     */
    private void updateSolver() {
        if ( _enabled && _pe != null && _te != null ) {
            _solver = SolverFactory.createSolver( _te, _pe, _direction );
        }
    }
    
    /**
     * Enables or disables "measure" mode.
     * @param enabled true or false
     */
    public void setMeasureEnabled( boolean enabled ) {
        if ( enabled != _measureEnabled ) {
            _measureEnabled = enabled;
            notifyObservers();
        }
    }
    
    /**
     * Use this method to ask if the solution is zero with energy values
     * that this wave is observing.
     * 
     * @param te
     * @param pe
     * @return true or false
     */
    public boolean isSolutionZero() {
        return isSolutionZero( _te, _pe );
    }
    
    /**
     * Use this method to ask if the solution would be zero with some hypothetical
     * energy values. This is useful when we're using the Configure Energy dialog.
     * 
     * @param te
     * @param pe
     * @return true or false
     */
    public boolean isSolutionZero( TotalEnergy te, AbstractPotential pe ) {
        return AbstractPlaneSolver.isSolutionZero( te, pe, _direction );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the wave packet in response to some change in 
     * total energy or potential energy.
     */
    public void update( Observable observable, Object arg ) {
        if ( _enabled ) {
            _solver.update();
            notifyObservers();
        }
    }
    
    //----------------------------------------------------------------------------
    // ClockListener implementation
    //----------------------------------------------------------------------------

    public void clockTicked( ClockEvent clockEvent ) {}

    public void clockStarted( ClockEvent clockEvent ) {}

    public void clockPaused( ClockEvent clockEvent ) {}

    /**
     * When the simulation time changes, record the time and notify observers. 
     * The observers are then responsible for calling solveWaveFunction for 
     * the new time value. 
     */
    public void simulationTimeChanged( ClockEvent clockEvent ) {
        if ( _enabled && _te != null && _pe != null ) {
            _time = clockEvent.getSimulationTime();
            notifyObservers();
        }
    }

    public void simulationTimeReset( ClockEvent clockEvent ) {}
}
