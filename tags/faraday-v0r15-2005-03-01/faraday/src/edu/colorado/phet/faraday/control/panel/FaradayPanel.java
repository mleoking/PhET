/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.control.panel;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JSlider;

import edu.colorado.phet.common.application.Module;


/**
 * FaradayPanel
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FaradayPanel extends JPanel {

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
     */
    public FaradayPanel() {
        super();
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
    public static void setSliderSize( JSlider slider, Dimension size ) {
        assert( slider != null );
        slider.setPreferredSize( size );
        slider.setMaximumSize( size );
        slider.setMinimumSize( size );
    }
}
