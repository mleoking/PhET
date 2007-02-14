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


/**
 * FaradayPanel is the base class for all panels used for all sub-panels 
 * that are added to the control panel.
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
    public static final String UNKNOWN_VALUE = "??";
    
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
        _titleFont = new Font( defaultFont.getName(), Font.BOLD, defaultFont.getSize() + 2 );
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
}
