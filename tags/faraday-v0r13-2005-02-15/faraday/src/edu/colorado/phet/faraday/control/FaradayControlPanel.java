/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.control;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JSlider;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.ControlPanel;


/**
 * FaradayControlPanel is the base class for all control panels in the Faraday simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class FaradayControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    public static final Dimension SLIDER_SIZE = new Dimension( 100, 20 );
    public static final Dimension SPINNER_SIZE = new Dimension( 50, 20 );
    public static final String UNKNOWN_VALUE = "??????";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Font _titleFont;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param module the module
     */
    public FaradayControlPanel( Module module ) {
        super( module );
        Font defaultFont = super.getFont();
        _titleFont = new Font( defaultFont.getName(), defaultFont.getStyle(), defaultFont.getSize() + 4 );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the font to be used for titled borders.
     * 
     * @return the font
     */
    public Font getTitleFont() {
        return _titleFont; // Fonts are immutable
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    /*
     * Sets a slider to a fixed size.
     * 
     * @param slider the slider
     * @param size the size
     */
    protected static void setSliderSize( JSlider slider, Dimension size ) {
        assert( slider != null );
        slider.setPreferredSize( size );
        slider.setMaximumSize( size );
        slider.setMinimumSize( size );
    }
}
