/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.control;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Shape;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.faraday.FaradayConfig;


/**
 * FaradaySlider is the graphic slider used throughout Faraday.
 * It has a knob, a knob hightlight, and a track, with no background.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FaradaySlider extends GraphicSlider {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int DEFAULT_TRACK_WIDTH = 2;
    private static final Color DEFAULT_TRACK_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Creates a slider with a specified track length.
     * Defaults are used for the track width and color.
     * 
     * @param component the parent component
     * @param trackLength the track length, in pixels
     */
    public FaradaySlider( Component component, int trackLength ) {
        this( component, trackLength, DEFAULT_TRACK_WIDTH, DEFAULT_TRACK_COLOR );
    }
    
    /**
     * Creates a slider with a specified track length, width and color.
     * 
     * @param component the parent component
     * @param trackLength the track length, in pixels
     * @param trackWidth the track width, in pixels
     * @param trackColor the track color
     */
    public FaradaySlider( Component component, int trackLength, int trackWidth, Color trackColor ) {
        super( component );
        
        assert( trackLength > 0 );
        
        // Background - none
        
        // Track
        Shape shape = new Rectangle( 0, 0, trackLength, trackWidth );
        PhetGraphic track = new PhetShapeGraphic( component, shape, trackColor );
        setTrack( track );
        
        // Knob
        PhetGraphic knob = new PhetImageGraphic( component, FaradayConfig.SLIDER_KNOB_IMAGE );
        knob.centerRegistrationPoint();
        setKnob( knob );
        
        // Knob Highlight
        PhetGraphic knobHighlight = new PhetImageGraphic( component, FaradayConfig.SLIDER_KNOB_RED_IMAGE );
        knobHighlight.centerRegistrationPoint();
        setKnobHighlight( knobHighlight );
    }
}
