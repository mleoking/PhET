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
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.clock.QTClock;
import edu.colorado.phet.quantumtunneling.clock.QTClockChangeEvent;
import edu.colorado.phet.quantumtunneling.clock.QTClockChangeListener;
import edu.colorado.phet.quantumtunneling.enum.Direction;


/**
 * PlaneWave is the model of a plane wave.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PlaneWave extends AbstractWave implements ModelElement, Observer, QTClockChangeListener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private QTClock _clock;
    private TotalEnergy _te;
    private AbstractPotential _pe;
    private AbstractPlaneSolver _solver;
    private Direction _direction;
    private boolean _enabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public PlaneWave( QTClock clock ) {
        _clock = clock;
        _clock.addChangeListener( this );
        _te = null;
        _pe = null;
        _solver = null;
        _direction = Direction.LEFT_TO_RIGHT;
        _enabled = true;
    }
    
    public void cleanup() {
        _clock.removeChangeListener( this );
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
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setEnabled( boolean enabled ) {
        if ( enabled != _enabled ) {
            _enabled = enabled;
            updateSolver();
            notifyObservers();
        }
    }
    
    public boolean isEnabled() {
        return _enabled;
    }
    
    private double getTime() {
        return _clock.getRunningTime() * QTConstants.TIME_SCALE;
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
        updateSolver();
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
        updateSolver();
        notifyObservers();
    }
    
    public AbstractPotential getPotentialEnergy() {
        return _pe;
    }
    
    public void setDirection( Direction direction ) {
        _direction = direction;
        if ( _solver != null ) {
            _solver.setDirection( direction );
        }
    }

    public Direction getDirection() {
        return _direction;
    }
    
    public WaveFunctionSolution solveWaveFunction( double x ) {
        WaveFunctionSolution solution = null;
        if ( _solver != null ) {
            double t = getTime();
            solution = _solver.solve( x, t );
        }
        return solution;
    }
    
    private void updateSolver() {
        if ( _enabled && _pe != null && _te != null ) {
            _solver = SolverFactory.createSolver( _te, _pe, _direction );
        }
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    public void stepInTime( double dt ) {
        if ( _enabled && _te != null && _pe != null ) {
            notifyObservers();
        }
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable observable, Object arg ) {
        if ( _enabled ) {
            _solver.update();
            notifyObservers();
        }
    }
    
    //----------------------------------------------------------------------------
    // QTClockListener implementation
    //----------------------------------------------------------------------------
    
    public void clockReset( QTClockChangeEvent event ) {
        if ( _enabled && _te != null && _pe != null ) {
            notifyObservers();
        }
    }
}
