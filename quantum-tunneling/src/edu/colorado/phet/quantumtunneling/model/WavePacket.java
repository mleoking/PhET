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
import edu.colorado.phet.quantumtunneling.util.Complex;


/**
 * WavePacket
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WavePacket extends AbstractWave implements ModelElement, Observer, QTClockChangeListener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private QTClock _clock;
    private TotalEnergy _te;
    private AbstractPotentialSpace _pe;
    private Direction _direction;
    private boolean _enabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public WavePacket( QTClock clock ) {
        super();
        _clock = clock;
        _clock.addChangeListener( this );
        _te = null;
        _pe = null;
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
    
    private double getTime() {
        return _clock.getRunningTime() * QTConstants.TIME_SCALE;
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

    public void setPotentialEnergy( AbstractPotentialSpace pe ) {
        if ( _pe != null ) {
            _pe.deleteObserver( this );
        }
        _pe = pe;
        _pe.addObserver( this );
        //XXX
        notifyObservers();
    }

    public AbstractPotentialSpace getPotentialEnergy() {
        return _pe;
    }

    public void setDirection( Direction direction ) {
        _direction = direction;
        // TODO Auto-generated method stub   
    }

    public Direction getDirection() {
        return _direction;
    }

    public WaveFunctionSolution solveWaveFunction( double x ) {
        // TODO Auto-generated method stub
        WaveFunctionSolution solution = null;
        double t = getTime();
        solution = new WaveFunctionSolution( x, t, new Complex(0,0), new Complex(0,0) );
        return solution;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    public void stepInTime( double dt ) {
        // TODO Auto-generated method stub    
    }

    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        // TODO Auto-generated method stub    
    }

    //----------------------------------------------------------------------------
    // QTClockListener implementation
    //----------------------------------------------------------------------------
    
    public void clockReset( QTClockChangeEvent event ) {
        // TODO Auto-generated method stub
    }

}
