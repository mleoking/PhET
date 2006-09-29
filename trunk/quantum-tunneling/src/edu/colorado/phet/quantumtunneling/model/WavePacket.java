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
import java.util.Random;

import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.ClockListener;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.enums.Direction;
import edu.colorado.phet.quantumtunneling.util.Distribution;
import edu.colorado.phet.quantumtunneling.util.LightweightComplex;
import edu.colorado.phet.quantumtunneling.util.Distribution.DistributionAccessor;


/**
 * WavePacket is the model of a wave packet.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WavePacket extends AbstractWave implements Observer, ClockListener {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private TotalEnergy _te;
    private AbstractPotential _pe;
    private Direction _direction;
    private boolean _enabled;
    private double _width;
    private double _center;
    private IWavePacketSolver _solver;
    private boolean _measureEnabled;
    private double _saveCenter;
    private double _saveWidth;
    private double _saveTotalEnergy;
    private AbstractPlaneSolver _planeSolver; // used to calculate reflection probability

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public WavePacket() {
        super();
        _te = null;
        _pe = null;
        _direction = Direction.LEFT_TO_RIGHT;
        _enabled = true;
        _width = QTConstants.DEFAULT_PACKET_WIDTH;
        _center = QTConstants.DEFAULT_PACKET_CENTER;
        _solver = new RichardsonSolver( this );
        _measureEnabled = false;
        _saveCenter = _center;
        _saveWidth = _width;
        _saveTotalEnergy = 0; // don't care what this value is
        _planeSolver = null;
    }
    
    /**
     * Call this method before releasing all references to objects of this type.
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
     * Is the wave packet initialized?
     * 
     * @return true or false
     */
    public boolean isInitialized() {
        return ( _te != null && _pe != null );
    }
    
    /**
     * Gets the reflection probability.
     * A return value < 0 indicates that reflection probability
     * doesn't make sense for the state of the wave.
     * <p>
     * Wave packet uses a plane wave to determine the reflection probability.
     * The solution is the same for a plane wave and wave packet.
     * (This is sort of a hack, but was necessary since this feature
     * was added many months after the sim was release.)
     * 
     * @return double
     */
    public double getReflectionProbability() {
        double R = -1;
        
        if ( _planeSolver != null ) {
            
            double E = _te.getEnergy();
            double V = _pe.getEnergyAt( _center );
            boolean isSolutionZero = ( E < V );
            
            if ( !isSolutionZero ) {

                // Determine if wave is in first or last region.
                boolean inFirstRegion;
                boolean inLastRegion;
                if ( _direction == Direction.LEFT_TO_RIGHT ) {
                    inFirstRegion = ( _pe.isInFirstRegion( _center ) );
                    inLastRegion = ( _pe.isInLastRegion( _center ) );
                }
                else {
                    // Reverse first & last, potential energy numbers regions from left-to-right.
                    inFirstRegion = ( _pe.isInLastRegion( _center ) );
                    inLastRegion = ( _pe.isInFirstRegion( _center ) );
                }

                if ( inFirstRegion ) {
                    // If the wave is in the first region, then get R from plane wave solver.
                    R = _planeSolver.getReflectionProbability();
                }
                else if ( inLastRegion ) {
                    // If the wave is in the last region, then R=0.
                    R = 0;
                }
            }
        }
        
        return R;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets a reference to the solver.
     * 
     * @return
     */
    public IWavePacketSolver getSolver() {
        return _solver;
    }
    
    /**
     * Sets the dx used by the solver.
     * 
     * @param dx
     */
    public void setDx( double dx ) {
        _solver.setDx( dx );
    }
    
    /**
     * Gets the position values (x axis) used in calculation the wave function solution.
     * 
     * @return
     */
    public double[] getPositionValues() {
        return _solver.getPositionValues();
    }
    
    /**
     * Gets the wave function values (y axis).
     * 
     * @return
     */
    public LightweightComplex[] getWaveFunctionValues() {
        return _solver.getWaveFunctionValues();
    }
    
    /**
     * Enables or disables this model object.
     * 
     * @param enabled
     */
    public void setEnabled( boolean enabled ) {
        if ( enabled != _enabled ) {
            _enabled = enabled;
        }
    }

    /**
     * Is this object enabled?
     * 
     * @return
     */
    public boolean isEnabled() {
        return _enabled;
    }
    
    /**
     * Sets the wave packet's initial width.
     * 
     * @param width
     */
    public void setWidth( double width ) {
        if ( width <= 0 ) {
            throw new IllegalArgumentException( "width must be > 0: " + width );
        }
        _width = width;
        update();
    }
    
    /**
     * Gets the wave packet's initial width.
     * 
     * @return
     */
    public double getWidth() {
        return _width;
    }
    
    /**
     * Sets the wave packet's initial center.
     * 
     * @param center
     */
    public void setCenter( double center ) {
        _center = center;
        update();
    }
    
    /**
     * Gets the wave packet's initial center.
     * 
     * @return
     */
    public double getCenter() {
        return _center;
    }
    
    /**
     * Sets the total energy.
     * 
     * @param te
     */
    public void setTotalEnergy( TotalEnergy te ) {
        if ( _te != null ) {
            _te.deleteObserver( this );
        }
        _te = te;
        _te.addObserver( this );
        updatePlaneSolver();
        update();
    }

    /**
     * Gets the total energy.
     * 
     * @return
     */
    public TotalEnergy getTotalEnergy() {
        return _te;
    }

    /**
     * Sets the potential energy.
     * 
     * @param pe
     */
    public void setPotentialEnergy( AbstractPotential pe ) {
        if ( _pe != null ) {
            _pe.deleteObserver( this );
        }
        _pe = pe;
        _pe.addObserver( this );
        updatePlaneSolver();
        update();
    }

    /**
     * Gets the potential energy.
     * 
     * @return
     */
    public AbstractPotential getPotentialEnergy() {
        return _pe;
    }

    /**
     * Sets the direction of the wave packet.
     * 
     * @param direction
     */
    public void setDirection( Direction direction ) {
        _direction = direction;
        updatePlaneSolver();
        update();
    }

    /**
     * Gets the direction of the wave packet.
     * 
     * @return
     */
    public Direction getDirection() {
        return _direction;
    }

    /**
     * Enables measuring.
     * Note that each time this is called with true, the wave packet should be measured again.
     * 
     * @param enabled true or false
     */
    public void setMeasureEnabled( boolean enabled ) {
        boolean wasMeasureEnabled = _measureEnabled;
        _measureEnabled = enabled;
        if ( enabled ) {
            if ( !wasMeasureEnabled ) {
                _saveCenter = _center;
                _saveWidth = _width;
                _saveTotalEnergy = _te.getEnergy();
            }
            double position = chooseRandomPosition();
            setCenter( position );
            setWidth( QTConstants.MEASURING_WIDTH );

            adjustTotalEnergyIfInForbiddenRegion( position ); // WORKAROUND
        }
        else if ( !enabled && wasMeasureEnabled ) {
            setCenter( _saveCenter );
            setWidth( _saveWidth );
            _te.setEnergy( _saveTotalEnergy );
        }
        update();
    }
    
    /*
     * A wave packet is in a "forbidden region" if the average total energy is less
     * that the potential energy at the wave packet's center.  If this is the case,
     * then we adjust the total energy so that it is slightly higher than the 
     * potential energy.
     */
    private void adjustTotalEnergyIfInForbiddenRegion( double wavePacketCenter ) {
        setEnabled( false );
        double E0 = _te.getEnergy();
        double V0 = _pe.getEnergyAt( wavePacketCenter );
        if ( E0 < V0 ) {
            _te.setEnergy( V0 + 0.1 );  
        }
        setEnabled( true );
    }
    
    /*
     * Select a random position, weighted by the probability density.
     * The probability that a particle is at a particular sample position
     * is given by abs( Psi[x,t] )^2 * dx, where dx is the space between
     * sample positions.
     * 
     * @return a position
     */
    private double chooseRandomPosition() {
        double center = 0;
        double[] positions = _solver.getPositionValues();
        LightweightComplex[] energies = _solver.getWaveFunctionValues();
        if ( positions.length > 1 ) {
            double dx = positions[1] - positions[0];
            Distribution distribution = new Distribution();
            for ( int i = 0; i < positions.length; i++ ) {
                double abs = energies[i].getAbs();
                double weight = abs * abs * dx;
                distribution.add( new Double( positions[i] ), weight );
            }
            DistributionAccessor accessor = new DistributionAccessor( distribution, new Random() );
            Object o = accessor.nextObject();
            if ( o != null ) {
                center = ((Double)o).doubleValue();
            }
            else {
                /* 
                 * If the distribution returned null, then it's likely because the 
                 * wave function solution is zero.  In this case, we want to 
                 * simply leave the wave function center where it is.
                 */
                center = getCenter(); 
            }
        }
        return center;
    }
    
    private void updatePlaneSolver() {
        if ( isInitialized() ) {
            _planeSolver = SolverFactory.createSolver( _te, _pe, _direction );
        }
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Resets the solver whenever the total energy or potential energy changes.
     * 
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if ( _enabled ) {
            update();
        }
    }
    
    /*
     * Resets the solver.
     */
    private void update() {
        if ( _planeSolver != null ) {
            _planeSolver.update();
        }
        _solver.update();
        notifyObservers();
    }

    //----------------------------------------------------------------------------
    // ClockListener implementation
    //----------------------------------------------------------------------------

    /**
     * Propagates the solver each time clock ticks.
     * 
     * @param clockEvent
     */
    public void clockTicked( ClockEvent clockEvent ) {
        if ( _enabled && isInitialized() ) {
            _solver.propagate();
            notifyObservers();
        }
    }

    public void clockStarted( ClockEvent clockEvent ) {}

    public void clockPaused( ClockEvent clockEvent ) {}

    public void simulationTimeChanged( ClockEvent clockEvent ) {}

    /**
     * Resets the solver when the clock is reset.
     * 
     * @param clockEvent
     */
    public void simulationTimeReset( ClockEvent clockEvent ) {
        if ( _enabled && isInitialized() ) {
            update();
        }
    }
}
