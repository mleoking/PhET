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
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Electron extends SpacialObservable implements ModelElement {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double POSITION_DELTA = 0.01;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private boolean _enabled;
    private ArrayList _curves; // array of QuadBezierSpline
    private int _curveIndex;
    private double _positionAlongCurve;

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
        notifyObservers();
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
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------

    /*
     * @see edu.colorado.phet.common.model.ModelElement#stepInTime(double)
     */
    public void stepInTime( double dt ) {
        if ( _enabled && _curves != null ) {

            // Move the electron along the path.
            _positionAlongCurve -= POSITION_DELTA;

            // If we've reached the end of the current path, move to the next path.
            if ( _positionAlongCurve <= 0 ) {
                _positionAlongCurve = 1.0;
                _curveIndex++;
                if ( _curveIndex >= _curves.size() ) {
                    _curveIndex = 0;
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