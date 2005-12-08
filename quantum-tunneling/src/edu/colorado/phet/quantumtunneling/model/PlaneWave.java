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

import edu.colorado.phet.common.model.ModelElement;


/**
 * PlaneWave is the model of a plane wave.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PlaneWave extends QTObservable implements ModelElement {

    private TotalEnergy _te;
    private AbstractPotentialSpace _pe;
    private AbstractSolver _solver;
    private double _t;
    
    public PlaneWave() {
        _te = null;
        _pe = null;
        _solver = null;
        _t = 0;
    }
    
    public void cleanup() {
        _solver.cleanup();
        _solver = null;
    }
    
    public void setTotalEnergy( TotalEnergy te ) {
        _te = te;
        updateSolver();
        notifyObservers();
    }
    
    public TotalEnergy getTotalEnergy() { 
        return _te;
    }
    
    public void setPotentialEnergy( AbstractPotentialSpace pe ) {
        _pe = pe;
        updateSolver();
        notifyObservers();
    }
    
    public AbstractPotentialSpace getPotentialEnergy() {
        return _pe;
    }
    
    public double getTime() {
        return _t;
    }
    
    public void resetTime() {
        _t = 0;
        notifyObservers();
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
        }
    }
    
    public void stepInTime( double dt ) {
        if ( _te != null && _pe != null ) {
            _t += dt;
            notifyObservers();
        }
    }
}
