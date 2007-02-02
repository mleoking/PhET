/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.hacks;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.hydrogenatom.control.WavelengthControl;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * GunWavelengthControl adds behavior to the standard WavelengthControl.
 * <p>
 * Behavior #1:
 * The knob is hilited when it is dragged "sufficiently close" to a 
 * value that would cause the atom to undergo a transition from state=1
 * to some other state. If the knob is released while it is hilited,
 * it snaps to the closest transition wavelength, and the hilite is cleared.
 * <p>
 * Behavior #2:
 * Markers are drawn in the track to indicate the position of transition
 * wavelengths. These markers are vertical lines.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GunWavelengthControl extends WavelengthControl {

    //----------------------------------------------------------------------------
    // Public class data
    //----------------------------------------------------------------------------

    public static int KNOB_HILITE_THRESHOLD = 3; // nm

    //----------------------------------------------------------------------------
    // Private class data
    //----------------------------------------------------------------------------

    private static final double NO_MATCH = -1;
    
    private static final int TRACK_WIDTH = 300;
    private static final int TRACK_HEIGHT = 25;

    private static final Stroke KNOB_NORMAL_STROKE = new BasicStroke( 1f );
    private static final Stroke KNOB_HILITE_STROKE = new BasicStroke( 3f );
    private static final Color KNOB_NORMAL_COLOR = Color.BLACK;
    private static final Color KNOB_HILITE_COLOR = Color.WHITE;

    private static final Stroke TRANSITION_MARKS_STROKE = new BasicStroke( 1f );
    private static final Color TRANSITION_MARKS_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private double[] _transitionWavelengths;
    private double _bestMatch; // the best matching transition wavelength
    private boolean _dragging;
    private boolean _hiliteTransitionWavelengths; // whether to hilite the knob when it is near a transition wavelength
    private PNode _transitionMarksNode; // markings in the track at transition wavelengths
    private boolean _transitionMarksVisible;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     * 
     * @param wavelengthControl
     */
    public GunWavelengthControl( PSwingCanvas canvas, double minWavelength, double maxWavelength, Color uvTrackColor, Color uvLabelColor, Color irTrackColor, Color irLabelColor ) {
        super( canvas, TRACK_WIDTH, TRACK_HEIGHT, minWavelength, maxWavelength, uvTrackColor, uvLabelColor, irTrackColor, irLabelColor );

        /* 
         * Hide the cursor, because it visually obscures the transition marks.
         * This is not a mouse cursor; it's the rectangle that appears above the tip of the knob.
         */
        setCursorVisible( false );
        
        // Do things when the user starts and stops dragging the knob.
        addKnobListener( new PBasicInputEventHandler() {

            public void mousePressed( PInputEvent event ) {
                startDragging();
            }

            public void mouseReleased( PInputEvent event ) {
                stopDragging();
            }
        } );

        // When the control's value changes, update the knob hiliting.
        addChangeListener( new ChangeListener() {

            public void stateChanged( ChangeEvent e ) {
                if ( _dragging && _hiliteTransitionWavelengths ) {
                    updateKnob();
                }
            }
        } );

        _transitionWavelengths = null;
        _hiliteTransitionWavelengths = false;
        _bestMatch = NO_MATCH;
        _dragging = false;
        _transitionMarksVisible = false;

        setKnobStroke( KNOB_NORMAL_STROKE );
        setKnobStrokeColor( KNOB_NORMAL_COLOR );
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
        updateTransitionMarks();
    }

    /**
     * Controls whether the knob hilights when we drag "near" a transitions wavelength.
     * @param b
     */
    public void setKnobHilitingEnabled( boolean b ) {
        _hiliteTransitionWavelengths = b;
    }
    
    /**
     * Controls whether we display vertical lines in the track to denote the transition wavelengths.
     * @param b
     */
    public void setTransitionMarksVisible( boolean b ) {
        System.out.println( "GunWavelengthControl.setTransitionWavelengthMarksVisible " + b );//XXX
        _transitionMarksVisible = b;
        if ( _transitionMarksNode != null ) {
            _transitionMarksNode.setVisible( b );
        }
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
            setWavelength( _bestMatch );
        }
        setKnobStroke( KNOB_NORMAL_STROKE );
        setKnobStrokeColor( KNOB_NORMAL_COLOR );
    }

    /*
     * Determines whether some wavelength is sufficiently 
     * close to some transition wavelength.
     */
    private boolean isClose( double wavelength, double transitionWavelength ) {
        double min = transitionWavelength - KNOB_HILITE_THRESHOLD;
        double max = transitionWavelength + KNOB_HILITE_THRESHOLD;
        return ( ( wavelength >= min ) && ( wavelength <= max ) );
    }

    /*
     * Called while the knob is being dragged.
     * Hilites the knob whenever the wavelength is sufficiently close to
     * one of the atom's transition wavelengths.
     */
    private void updateKnob() {

        double wavelength = getWavelength();

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
            setKnobStroke( KNOB_NORMAL_STROKE );
            setKnobStrokeColor( KNOB_NORMAL_COLOR );
        }
        else {
            setKnobStroke( KNOB_HILITE_STROKE );
            setKnobStrokeColor( KNOB_HILITE_COLOR );
        }
    }
    
    /*
     * Updates the marks for transition wavelengths.
     * These marks are verticle lines that appear in the tracks.
     */
    private void updateTransitionMarks() {
        
        PNode trackBorder = getTrackBorder();

        if ( _transitionMarksNode != null ) {
            trackBorder.removeChild( _transitionMarksNode );
            _transitionMarksNode = null;
        }

        if ( _transitionWavelengths != null ) {
            
            final double trackBorderWidth = trackBorder.getFullBounds().getWidth();
            final double trackBorderHeight = trackBorder.getFullBounds().getHeight();
            final double minWavelength = getMinWavelength();
            final double maxWavelength = getMaxWavelength();
            
            double trackWidth = getTrackFullBounds().getWidth();
            double widthDiff = trackBorderWidth - trackWidth;

            _transitionMarksNode = new PComposite();
            _transitionMarksNode.setVisible( _transitionMarksVisible );
            trackBorder.addChild( _transitionMarksNode );
            
            for ( int i = 0; i < _transitionWavelengths.length; i++ ) {
                
                double wavelength = _transitionWavelengths[i];
                double x = ( trackWidth * ( wavelength - minWavelength ) / ( maxWavelength - minWavelength ) ) + ( widthDiff / 2 );
                PPath path = new PPath( new Line2D.Double( x, 0, x, trackBorderHeight ) );
                path.setStroke( TRANSITION_MARKS_STROKE );
                path.setStrokePaint( TRANSITION_MARKS_COLOR );
                
                _transitionMarksNode.addChild( path );
            }
        }
    }
}
