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
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Electron extends SpacialObservable implements ModelElement {
   
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double POSITION_DELTA = 0.01; // 100 points along each curve
    
    private static final double MIN_SPEED = 0.10;
    private static final double MAX_SPEED = 0.50;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private boolean _enabled;
    private ArrayList _curves; // array of QuadBezierSpline
    private int _curveIndex;
    private double _positionAlongCurve;
    private double _speed; // -1...+1, controls speed and direction

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public Electron() {
        super();
        _enabled = true;
        _curves = null;
        _curveIndex = 0;
        _positionAlongCurve = 1.0;
        _speed = 0.0;
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
    public void setCurves( ArrayList curves ) {
        assert ( curves != null );
        assert ( curves.size() > 0 );
        _curves = curves;
        _curveIndex = 0;
        _positionAlongCurve = 1.0;
    }

    /**
     * Clears the set of curves.
     */
    public void clearCurves() {
        _curves = null;
    }

    /**
     * Sets the location of the electron, the curve and postion on the curve.
     * 
     * @param curveIndex index of the starting curve
     * @param positionAlongCurve position on the curve, 0-1 (1=startPoint, 0=endPoint)
     */
    public void setCurveLocation( int curveIndex, double positionAlongCurve ) {
        assert( curveIndex >= 0 && curveIndex < _curves.size() );
        assert( positionAlongCurve >=0.0 && positionAlongCurve <= 1.0 );
        
        _curveIndex = curveIndex;
        _positionAlongCurve = positionAlongCurve;
        
        // Evaluate the quadratic to determine XY location.
        QuadBezierSpline path = (QuadBezierSpline) _curves.get( _curveIndex );
        Point2D location = path.evaluate( _positionAlongCurve );
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

    /*
     * @see edu.colorado.phet.common.model.ModelElement#stepInTime(double)
     */
    public void stepInTime( double dt ) {
        if ( _enabled && _speed != 0 && _curves != null ) {

            // Move the electron along the path.
            _positionAlongCurve -= _speed;

            // If we've reached the end of the current path, move to the next path.
            // Handle motion in both directions.
            if ( _positionAlongCurve <= 0 ) {
                _positionAlongCurve = 1.0;
                _curveIndex++;
                if ( _curveIndex >= _curves.size() ) {
                    _curveIndex = 0;
                }
            }
            else if ( _positionAlongCurve >= 1 ) {
                _positionAlongCurve = 0.0;
                _curveIndex--;
                if ( _curveIndex < 0 ) {
                    _curveIndex = _curves.size() - 1;
                }
            }

            // Evaluate the quadratic to determine XY location.
            QuadBezierSpline path = (QuadBezierSpline) _curves.get( _curveIndex );
            Point2D location = path.evaluate( _positionAlongCurve );
            super.setLocation( location );

            notifyObservers();
        }
    }
}