// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.hydrogenatom.hacks;

import java.awt.*;
import java.awt.geom.Line2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.piccolophet.nodes.WavelengthControl;
import edu.colorado.phet.hydrogenatom.HASimSharing;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * GunWavelengthControl adds behavior to the standard WavelengthControl.
 * <p/>
 * Behavior #1:
 * The thumb (aka knob) is highlighted when it is dragged "sufficiently close" to a
 * value that would cause the atom to undergo a transition from state=1
 * to some other state. If the thumb is released while it is highlighted,
 * it snaps to the closest transition wavelength.
 * <p/>
 * Behavior #2:
 * Markers are drawn in the track to indicate the position of transition
 * wavelengths. These markers are vertical lines.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GunWavelengthControl extends WavelengthControl {

    private static final boolean PRINT_DEBUG = false;

    //----------------------------------------------------------------------------
    // Public class data
    //----------------------------------------------------------------------------

    public static int THUMB_HIGHLIGHT_THRESHOLD = 3; // nm

    //----------------------------------------------------------------------------
    // Private class data
    //----------------------------------------------------------------------------

    private static final double NO_MATCH = -1;

    private static final int TRACK_WIDTH = 300;
    private static final int TRACK_HEIGHT = 25;

    private static final Stroke THUMB_NORMAL_STROKE = new BasicStroke( 1f );
    private static final Stroke THUMB_HIGHLIGHT_STROKE = new BasicStroke( 2f );
    private static final Color THUMB_NORMAL_COLOR = Color.BLACK;
    private static final Color THUMB_HIGHLIGHT_COLOR = Color.WHITE;

    private static final Stroke TRANSITION_MARKS_STROKE = new BasicStroke( 1f );
    private static final Color TRANSITION_MARKS_COLOR = Color.BLACK;

    private static final Color ALTERNATE_CURSOR_COLOR = Color.WHITE;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private double[] _transitionWavelengths;
    private double _bestMatch; // the best matching transition wavelength
    private boolean _dragging;
    private boolean _highlightTransitionWavelengths; // whether to highlight the thumb when it is near a transition wavelength
    private PNode _transitionMarksNode; // markings in the track at transition wavelengths
    private boolean _transitionMarksVisible;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     */
    public GunWavelengthControl( double minWavelength, double maxWavelength, Color uvTrackColor, Color uvLabelColor, Color irTrackColor, Color irLabelColor ) {
        super( HASimSharing.UserComponents.gunWavelengthControl, false, TRACK_WIDTH, TRACK_HEIGHT, minWavelength, maxWavelength, uvTrackColor, uvLabelColor, irTrackColor, irLabelColor );

        /* 
         * Change the cursor color, so that it doesn't obscure the transition marks.
         * This is not a mouse cursor; it's the rectangle that appears above the tip of the thumb.
         */
        setCursorColor( ALTERNATE_CURSOR_COLOR );

        // Do things when the user starts and stops dragging the thumb.
        addThumbListener( new PBasicInputEventHandler() {

            public void mousePressed( PInputEvent event ) {
                startDragging();
            }

            public void mouseReleased( PInputEvent event ) {
                stopDragging();
            }
        } );

        // When the control's value changes, update the thumb highlighting.
        addChangeListener( new ChangeListener() {

            public void stateChanged( ChangeEvent e ) {
                if ( _highlightTransitionWavelengths ) {
                    updateThumb();
                }
            }
        } );

        _transitionWavelengths = null;
        _highlightTransitionWavelengths = false;
        _bestMatch = NO_MATCH;
        _dragging = false;
        _transitionMarksVisible = false;

        updateThumb();
    }

    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the transition wavelengths.
     *
     * @param transitionWavelengths
     */
    public void setTransitionWavelengths( double[] transitionWavelengths ) {
        if ( PRINT_DEBUG ) {
            printTransitionWavelengths( transitionWavelengths );
        }
        _transitionWavelengths = transitionWavelengths;
        updateThumb();
        updateTransitionMarks();
    }

    /*
     * Prints transition wavelengths, for debugging.
     */
    private static void printTransitionWavelengths( double[] transitionWavelengths ) {
        System.out.print( "GunWavelengthControl transitionWavelengths=" );
        if ( transitionWavelengths == null ) {
            System.out.println( "null" );
        }
        else {
            for ( double wavelength : transitionWavelengths ) {
                System.out.print( " " + wavelength );
            }
            System.out.println();
        }
    }

    /**
     * Controls whether the thumb highlights when we drag "near" a transitions wavelength.
     *
     * @param b
     */
    public void setThumbHighlightingEnabled( boolean b ) {
        _highlightTransitionWavelengths = b;
        updateThumb();
    }

    /**
     * Controls whether we display vertical lines in the track to denote the transition wavelengths.
     *
     * @param b
     */
    public void setTransitionMarksVisible( boolean b ) {
        _transitionMarksVisible = b;
        if ( _transitionMarksNode != null ) {
            _transitionMarksNode.setVisible( b );
        }
    }

    //----------------------------------------------------------------------------
    // private
    //----------------------------------------------------------------------------

    /*
     * Called when we start dragging the slider thumb.
     */
    private void startDragging() {
        _dragging = true;
    }

    /*
     * Called when we stop dragging the slider thumb.
     * Snaps the thumb to the closest transition wavelength,
     * and removes the highlighted from the thumb.
     */
    private void stopDragging() {
        _dragging = false;
        if ( _bestMatch != NO_MATCH ) {
            setWavelength( _bestMatch );
        }
    }

    /*
     * Determines whether some wavelength is sufficiently 
     * close to some transition wavelength.
     */
    private boolean isClose( double wavelength, double transitionWavelength ) {
        double min = transitionWavelength - THUMB_HIGHLIGHT_THRESHOLD;
        double max = transitionWavelength + THUMB_HIGHLIGHT_THRESHOLD;
        return ( ( wavelength >= min ) && ( wavelength <= max ) );
    }

    /*
     * Called while the thumb is being dragged.
     * Highlights the thumb whenever the wavelength is sufficiently close to one of the atom's transition wavelengths.
     */
    private void updateThumb() {

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

        // Set the thumb's stroke properties
        if ( _bestMatch == NO_MATCH ) {
            setThumbStroke( THUMB_NORMAL_STROKE );
            setThumbStrokeColor( THUMB_NORMAL_COLOR );
        }
        else {
            setThumbStroke( THUMB_HIGHLIGHT_STROKE );
            setThumbStrokeColor( THUMB_HIGHLIGHT_COLOR );
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
