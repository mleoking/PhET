/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.dischargelamps.control;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.model.DischargeLampModel;

import java.awt.*;


/**
 * FaradaySlider is the graphic slider used throughout Faraday.
 * It has a knob, a knob hightlight, and a track, with no background.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BatterySliderExp extends GraphicSlider {

    public static final String SLIDER_KNOB_IMAGE = DischargeLampsConfig.IMAGE_FILE_DIRECTORY + "sliderKnob.png";
    public static final String SLIDER_KNOB_HIGHLIGHT_IMAGE = DischargeLampsConfig.IMAGE_FILE_DIRECTORY + "sliderKnobHighlight.png";

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int DEFAULT_TRACK_WIDTH = 2;
    private static final Color DEFAULT_TRACK_COLOR = Color.BLACK;
    private DischargeLampModel model;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Creates a slider with a specified track length.
     * Defaults are used for the track width and color.
     *
     * @param component   the parent component
     * @param trackLength the track length, in pixels
     */
    public BatterySliderExp( Component component, int trackLength, DischargeLampModel model ) {
        this( component, trackLength, DEFAULT_TRACK_WIDTH, DEFAULT_TRACK_COLOR, model );
    }

    /**
     * Creates a slider with a specified track length, width and color.
     *
     * @param component   the parent component
     * @param trackLength the track length, in pixels
     * @param trackWidth  the track width, in pixels
     * @param trackColor  the track color
     */
    public BatterySliderExp( Component component, int trackLength, int trackWidth, Color trackColor, DischargeLampModel model ) {
        super( component );
        this.model = model;

        // Background - none
        
        // Track
        Shape shape = new Rectangle( 0, 0, trackLength, trackWidth );
        PhetGraphic track = new PhetShapeGraphic( component, shape, trackColor, new BasicStroke( 1 ), Color.black );
        setTrack( track );
        
        // Knob
        PhetGraphic knob = new PhetImageGraphic( component, SLIDER_KNOB_IMAGE );
        knob.centerRegistrationPoint();
        setKnob( knob );
        
        // Knob Highlight
        PhetGraphic knobHighlight = new PhetImageGraphic( component, SLIDER_KNOB_HIGHLIGHT_IMAGE );
        knobHighlight.centerRegistrationPoint();
        setKnobHighlight( knobHighlight );
    }
}
