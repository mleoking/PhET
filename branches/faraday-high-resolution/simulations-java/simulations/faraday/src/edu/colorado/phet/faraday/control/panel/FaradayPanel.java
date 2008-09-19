/* Copyright 2005-2008, University of Colorado */

package edu.colorado.phet.faraday.control.panel;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;


/**
 * FaradayPanel is the base class for all panels used for all sub-panels 
 * that are added to the control panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FaradayPanel extends JPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    public static final Dimension SLIDER_SIZE = new Dimension( 100, 20 );
    public static final Dimension SPINNER_SIZE = new Dimension( 60, 40);
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
        _titleFont = new PhetFont( Font.BOLD, PhetFont.getDefaultFontSize() + 2 );
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
