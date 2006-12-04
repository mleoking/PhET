/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.module;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.hydrogenatom.control.WavelengthControl;
import edu.colorado.phet.hydrogenatom.model.*;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * WavelengthKnobManager manages the knob on the gun's wavelength slider.
 * The knob is hilited when it is dragged "sufficiently close" to a 
 * value that would cause the atom to undergo a transition from state=1
 * to some other state. When the knob is released, the hilite is cleared.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WavelengthKnobManager {

    //----------------------------------------------------------------------------
    // Public class data
    //----------------------------------------------------------------------------
    
    public static double HILITE_THRESHOLD = 3; // nm
    
    //----------------------------------------------------------------------------
    // Private class data
    //----------------------------------------------------------------------------
    
    private static final double NO_MATCH = -1;
        
    private static final Stroke KNOB_NORMAL_STROKE = new BasicStroke( 1f );
    private static final Stroke KNOB_HILITE_STROKE = new BasicStroke( 3f );
    private static final Color KNOB_NORMAL_COLOR = Color.BLACK;
    private static final Color KNOB_HILITE_COLOR = Color.WHITE;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private WavelengthControl _wavelengthControl;
    private double[] _transitionWavelengths;
    private double _bestMatch; // the best matching transition wavelength
    private boolean _dragging;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public WavelengthKnobManager( WavelengthControl wavelengthControl ) {
        
        _wavelengthControl = wavelengthControl;

        _wavelengthControl.addKnobListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                startDragging();
            }
            public void mouseReleased( PInputEvent event ) {
                stopDragging();
            }
        });
        
        _wavelengthControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( _dragging ) {
                    updateKnob();
                }
            }
        } );
        
        _transitionWavelengths = new double[] { 94, 95, 97, 103, 122 }; //XXX get from atom
        _bestMatch = NO_MATCH;
        _dragging = false;
        
        updateKnob();
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the transition wavelengths.
     * @param transitionWavelengths
     */
    public void setTransitionWavelengths( double[] transitionWavelengths ) {
        _transitionWavelengths = transitionWavelengths;
    }
    
    //----------------------------------------------------------------------------
    // private
    //----------------------------------------------------------------------------
    
    /*
     * Called when we start dragging the slider knob.
     */
    private void startDragging() {
        _dragging = true;
    }
    
    /*
     * Called when we stop dragging the slider knob.
     * Snaps the knob to the closest transition wavelength,
     * and removes the hilite from the knob.
     */
    private void stopDragging() {
        _dragging = false;
        if ( _bestMatch != NO_MATCH ) {
          _wavelengthControl.setWavelength( _bestMatch );
        }
        _wavelengthControl.setKnobStroke( KNOB_NORMAL_STROKE );
        _wavelengthControl.setKnobStrokeColor( KNOB_NORMAL_COLOR );
    }
    
    /*
     * Determines whether some wavelength is sufficiently 
     * close to some transition wavelength.
     */
    private boolean isClose( double wavelength, double transitionWavelength ) {
        double min = transitionWavelength - HILITE_THRESHOLD;
        double max = transitionWavelength + HILITE_THRESHOLD;
        return ( ( wavelength >=  min ) && ( wavelength <= max ) ); 
    }
    
    /*
     * Called while the knob is being dragged.
     * Hilites the knob whenever the wavelength is sufficiently close to
     * one of the atom's transition wavelengths.
     */
    private void updateKnob() {
        
        double wavelength = _wavelengthControl.getWavelength();
        
        _bestMatch = NO_MATCH;
        double difference = 0;
        double prevDifference = 1000000000;
        
        // Find the best match for the current wavelength
        if ( _transitionWavelengths != null && _transitionWavelengths.length != 0 ) {
            for ( int i = 0; i < _transitionWavelengths.length; i++ ) {
                if ( isClose( wavelength, _transitionWavelengths[i] ) ) {
                    difference = Math.abs( _transitionWavelengths[i] - wavelength );
                    if ( difference < prevDifference ) {
                        prevDifference = difference;
                        _bestMatch = _transitionWavelengths[i];
                    }
                }
            }
        }
        
        // Set the knob's stroke properties
        if ( _bestMatch == NO_MATCH ) {
            _wavelengthControl.setKnobStroke( KNOB_NORMAL_STROKE );
            _wavelengthControl.setKnobStrokeColor( KNOB_NORMAL_COLOR );
        }
        else {
            _wavelengthControl.setKnobStroke( KNOB_HILITE_STROKE );
            _wavelengthControl.setKnobStrokeColor( KNOB_HILITE_COLOR );
        }
    }
}
