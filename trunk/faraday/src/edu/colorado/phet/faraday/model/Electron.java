/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.model.ModelElement;


/**
 * Electron is the model of an electron, capable of moving along some path.
 * The path is described by an ordered set of ElectronPathDescriptors.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Electron extends SpacialObservable implements ModelElement {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Maximum distance along a path that can be traveled in one clock tick.
    private static final double MAX_PATH_POSITION_DELTA = 0.15;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Is this electron enabled?
    private boolean _enabled;
    
    // Describes the electron's path (array of ElectronPathDescriptor)
    private ArrayList _path;
    
    // Index of the descriptor that describes the curve the electron is currently on.
    private int _pathIndex;
    
    // Electron's position along the current curve (1=startPoint, 0=endPoint)
    private double _pathPosition;
    
    // Electron's speed & direction (-1...+1)
    private double _speed;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public Electron() {
        super();
        _enabled = true;
        _path = null;
        _pathIndex = 0; // first curve
        _pathPosition = 1.0; // curve's start point
        _speed = 0.0;  // not moving
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Enables or disabled the electron.
     * 
     * @param enabled true to enable, false to disable
     */
    public void setEnabled( boolean enabled ) {
        if ( enabled != _enabled ) {
            _enabled = enabled;
            notifyObservers();
        }
    }

    /**
     * Determines whether the electron is enabled or disabled.
     * 
     * @return true if enabled, false if disabled
     */
    public boolean isEnabled() {
        return _enabled;
    }
    
    /**
     * Sets the path that the electron will follow.
     * 
     * @param path array of ElectronPathDescriptor
     */
    public void setPath( ArrayList path ) {
        assert ( path != null );
        assert ( path.size() > 0 );
        assert ( path.get(0) instanceof ElectronPathDescriptor );
        _path = path;
        _pathIndex = 0;
        _pathPosition = 1.0;
    }
    
    /**
     * Gets the descriptor for the curve that the electron is currently on.
     * 
     * @return an ElectronPathDescriptor
     */
    public ElectronPathDescriptor getPathDescriptor() {
        return (ElectronPathDescriptor)_path.get( _pathIndex );
    }
    
    /**
     * Clears the path.
     */
    public void clearPath() {
        _path = null;
    }

    /**
     * Sets the position of the electron along its path.
     * 
     * @param pathIndex index of the ElectronPathDescriptor
     * @param pathPosition position on the curve, 0-1 (1=startPoint, 0=endPoint)
     */
    public void setPositionAlongPath( int pathIndex, double pathPosition  ) {

        assert( pathPosition >=0.0 && pathPosition <= 1.0 );
        assert( pathIndex >= 0 && pathIndex < _path.size() );
        
        _pathIndex = pathIndex;
        _pathPosition = pathPosition;
        
        // Evaluate the quadratic to determine XY location.
        ElectronPathDescriptor descriptor = (ElectronPathDescriptor) _path.get( _pathIndex );
        QuadBezierSpline curve = descriptor.getCurve();
        Point2D location = curve.evaluate( _pathPosition );
        super.setLocation( location );
    }
    
    /**
     * Sets the electron speed and direction
     * <p>
     * Speed/direction is a number between -1.0 and +1.0 inclusive.
     * The magnitude determines the speed, while the sign determines the direction.
     * A value of zero cause the electron to stop moving.
     * A value of 1 or +1 causes the electron to move the full length of a curve
     * segment in one clock tick.
     * <p>
     * Values outside of the valid range are silently clamped.
     * 
     * @param speed
     */
    public void setSpeed( double speed ) {
        assert( speed >= -1 && speed <= 1 );
        _speed = speed;
    }
    
    /**
     * Gets the electron speed.
     * See setSpeed for a description of this value.
     * 
     * @return the speed
     */
    public double getSpeed() {
        return _speed;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------

    /**
     * Moves the electron along the path.  
     * <p>
     * The electron's path is described by the ElectronPathDescriptor array.
     * <p>
     * The electron's speed & direction determine its position along a curve.
     * Speed is scaled to account for possible differences in the lengths 
     * of the curves. Shorter curves will have a larger scaling factor.
     * <p>
     * When an electron gets to the end of the current curve, it jumps
     * to the next curve, to a point that represent the "overshoot".
     * The order of curves is determined by the order of elements in the 
     * ElectronPathDescriptor array.
     */
    public void stepInTime( double dt ) {
        if ( _enabled && _speed != 0 && _path != null ) {
            
            // Move the electron along the path.
            double speedScale = ((ElectronPathDescriptor)_path.get( _pathIndex )).getSpeedScale();
            double delta = MAX_PATH_POSITION_DELTA * _speed * speedScale;
            _pathPosition -= delta;
            
            // Do we need to switch curves?
            if ( _pathPosition <= 0 || _pathPosition >= 1 ) {
                switchCurves();  // sets _pathIndex and _pathPosition !
            }
            
            // Evaluate the quadratic to determine XY location.
            ElectronPathDescriptor descriptor = (ElectronPathDescriptor)_path.get( _pathIndex );
            QuadBezierSpline curve = descriptor.getCurve();
            Point2D location = curve.evaluate( _pathPosition );
            super.setLocation( location );
        }
    }
    
    /**
     * Moves the electron to an appropriate point on the next/previous curve.
     * Rescales any "overshoot" of position so the distance moved looks 
     * approximately the same when moving between curves that have different 
     * lengths.  
     * <p>
     * If curves have different lengths, it is possible that we may totally
     * skip a curve.  This is handled via recursive calls to switchCurves.
     */
    private void switchCurves() {
       
        double oldSpeedScale = ((ElectronPathDescriptor)_path.get( _pathIndex )).getSpeedScale();
        
        if ( _pathPosition <= 0 ) {
            
            // We've passed the end point, so move to the next curve.
            _pathIndex++;
            if ( _pathIndex > _path.size() - 1 ) {
                _pathIndex = 0;
            }
            
            // Set the position on the curve.
            double newSpeedScale = ((ElectronPathDescriptor)_path.get( _pathIndex )).getSpeedScale();
            double overshoot = Math.abs( _pathPosition * newSpeedScale / oldSpeedScale );
            _pathPosition = 1.0 - overshoot;
            
            // Did we overshoot the curve?
            if ( _pathPosition < 0.0 ) {
                switchCurves();
            }
        }
        else if ( _pathPosition >= 1.0 ) {
            
            // We've passed the start point, so move to the previous curve.
            _pathIndex--;
            if ( _pathIndex < 0 ) {
                _pathIndex = _path.size() - 1;
            }
            
            // Set the position on the curve.
            double newSpeedScale = ((ElectronPathDescriptor)_path.get( _pathIndex )).getSpeedScale();
            double overshoot = Math.abs( ( 1 - _pathPosition ) * newSpeedScale / oldSpeedScale );
            _pathPosition = 0.0 + overshoot;
            
            // Did we overshoot the curve?
            if ( _pathPosition > 1.0 ) {
                switchCurves();
            }
        }
    }
}