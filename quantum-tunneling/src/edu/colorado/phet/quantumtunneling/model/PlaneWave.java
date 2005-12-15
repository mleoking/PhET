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
public class PlaneWave extends QTObservable implements ModelElement, Observer, QTClockChangeListener {

    private QTClock _clock;
    private TotalEnergy _te;
    private AbstractPotentialSpace _pe;
    private AbstractSolver _solver;
    private Direction _direction;
    
    public PlaneWave( QTClock clock ) {
        _clock = clock;
        _clock.addChangeListener( this );
        _te = null;
        _pe = null;
        _solver = null;
        _direction = Direction.LEFT_TO_RIGHT;
    }
    
    public void cleanup() {
        _clock.removeChangeListener( this );
        _clock = null;
        if ( _solver != null ) {
            _solver.cleanup();
            _solver = null;
        }
        if ( _te != null ) {
            _te.deleteObserver( this );
            _te = null;
        }
        if ( _pe != null ) {
            _pe.deleteObserver( this );
            _pe = null;
        }
    }
    
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
    
    public void setPotentialEnergy( AbstractPotentialSpace pe ) {
        if ( _pe != null ) {
            _pe.deleteObserver( this );
        }
        _pe = pe;
        _pe.addObserver( this );
        updateSolver();
        notifyObservers();
    }
    
    public AbstractPotentialSpace getPotentialEnergy() {
        return _pe;
    }
    
    public void setDirection( Direction direction ) {
        _direction = direction;
        if ( _solver != null ) {
            _solver.setDirection( direction );
        }
    }
    
    public double getTime() {
        return _clock.getRunningTime() * QTConstants.TIME_SCALE;
    }
    
    public AbstractSolver getSolver() {
        return _solver;
    }
    
    private void updateSolver() {
        if ( _solver != null ) {
            _solver.cleanup();
            _solver = null;
        }
        if ( _pe != null && _te != null ) {
            _solver = SolverFactory.createSolver( _te, _pe );
            _solver.setDirection( _direction );
        }
    }
    
    public void update( Observable observable, Object arg ) {
        notifyObservers();
    }
    
    public void clockReset( QTClockChangeEvent event ) {
        if ( _te != null && _pe != null ) {
            notifyObservers();
        }
    }
    
    public void stepInTime( double dt ) {
        if ( _te != null && _pe != null ) {
            notifyObservers();
        }
    }
}
