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

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.model.ModelElement;


/**
 * Electron is the model of an electron, capable of moving along some path.
 * The path is described as a set of CurveDescriptors.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Electron extends SpacialObservable implements ModelElement {
   
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double MIN_SPEED = 0.00;
    private static final double MAX_SPEED = 1.00;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Is this electron enabled?
    private boolean _enabled;
    
    // Describes the electron's path (array of CurveDescriptor)
    private ArrayList _curveDescriptors;
    
    // Index of the descriptor that describes the curve the electron is currently on.
    private int _curveIndex;
    
    // Electron's position along the current curve (0-1)
    private double _positionAlongCurve;
    
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
        _curveDescriptors = null;
        _curveIndex = 0; // first curve
        _positionAlongCurve = 1.0; // curve's start point
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
     * Sets the set of curves that the electron will follow.
     * The curves will be followed in the order that they appear in the array.
     * 
     * @param curves
     */
    public void setCurveDescriptors( ArrayList curves ) {
        assert ( curves != null );
        assert ( curves.size() > 0 );
        _curveDescriptors = curves;
        _curveIndex = 0;
        _positionAlongCurve = 1.0;
    }
    
    /**
     * Gets the curve descriptor for the curve that the electron is currently on.
     * 
     * @return a CurveDescriptor
     */
    public CurveDescriptor getCurveDescriptor() {
        return (CurveDescriptor)_curveDescriptors.get( _curveIndex );
    }
    
    /**
     * Clears the set of curve descriptors.
     */
    public void clearCurveDescriptors() {
        _curveDescriptors = null;
    }

    /**
     * Sets the position of the electron along one of the curves.
     * 
     * @param positionAlongCurve position on the curve, 0-1 (1=startPoint, 0=endPoint)
     * @param curveIndex index of the curve
     */
    public void setPositionAlongCurve( double positionAlongCurve, int curveIndex ) {

        assert( positionAlongCurve >=0.0 && positionAlongCurve <= 1.0 );
        assert( curveIndex >= 0 && curveIndex < _curveDescriptors.size() );
        
        _curveIndex = curveIndex;
        _positionAlongCurve = positionAlongCurve;
        
        // Evaluate the quadratic to determine XY location.
        CurveDescriptor cd = (CurveDescriptor) _curveDescriptors.get( _curveIndex );
        QuadBezierSpline curve = cd.getCurve();
        Point2D location = curve.evaluate( _positionAlongCurve );
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
        if ( speed == 0 ) {
            _speed = speed;
        }
        else if ( speed > 0 ) {
            _speed = MathUtil.clamp( MIN_SPEED, speed, MAX_SPEED );
        }
        else {
            _speed = MathUtil.clamp( -MAX_SPEED, speed, -MIN_SPEED );
        }
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
     * The electron's path is described by the CurveDescriptor array.
     * <p>
     * The electron's speed & direction determine its position along a curve.
     * Speed is scaled to account for possible differences in the lengths 
     * of the curves. Shorter curves will have a larger scaling factor.
     * <p>
     * When an electron gets to the end of the current curve, it jumps
     * to the next curve, to a point that represent the "overshoot".
     * The order of curves is determined by the order of elements in the 
     * CurveDescription array.
     */
    public void stepInTime( double dt ) {
        if ( _enabled && _speed != 0 && _curveDescriptors != null ) {
            
            // Move the electron along the path.
            double oldSpeedScale = ((CurveDescriptor)_curveDescriptors.get( _curveIndex )).getSpeedScale();
            _positionAlongCurve -= _speed * oldSpeedScale;

            // If we've reached the end of the current path, move to the next path.
            // Handle motion in both directions.
            if ( _positionAlongCurve <= 0 ) {
                // Move to the next curve, with wrap around and speed re-scaling.
                _curveIndex++;
                if ( _curveIndex >= _curveDescriptors.size() ) {
                    _curveIndex = 0;
                }
                // Set the position on the next curve.
                double newSpeedScale = ((CurveDescriptor)_curveDescriptors.get( _curveIndex )).getSpeedScale();
                double overshoot = Math.abs( _positionAlongCurve * newSpeedScale / oldSpeedScale ) % 1;
                _positionAlongCurve = 1.0 - overshoot;
            }
            else if ( _positionAlongCurve >= 1 ) {
                // Move to the previous curve, with wrap around and speed re-scaling.
                _curveIndex--;
                if ( _curveIndex < 0 ) {
                    _curveIndex = _curveDescriptors.size() - 1;
                }
                // Set the position on the next curve.
                double newSpeedScale = ((CurveDescriptor)_curveDescriptors.get( _curveIndex )).getSpeedScale();
                double overshoot = Math.abs( (_positionAlongCurve % 1) * newSpeedScale / oldSpeedScale ) % 1;
                _positionAlongCurve = 0.0 + overshoot;
            }
            
            // Evaluate the quadratic to determine XY location.
            CurveDescriptor cd = (CurveDescriptor)_curveDescriptors.get( _curveIndex );
            QuadBezierSpline curve = cd.getCurve();
            Point2D location = curve.evaluate( _positionAlongCurve );
            super.setLocation( location );
            
            notifyObservers();
        }
    }
}