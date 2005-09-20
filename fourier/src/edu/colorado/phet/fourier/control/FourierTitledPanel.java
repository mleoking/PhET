/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.control;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.fourier.FourierConfig;


/**
 * FourierTitledPanel encapsualtes the "look" of all titled panels 
 * in a Fourier control panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FourierTitledPanel extends JPanel {

    private static final Font TITLE_FONT = new Font( FourierConfig.FONT_NAME, Font.ITALIC, 13 );
    
    public FourierTitledPanel( String title ) {
        TitledBorder titledBorder = new TitledBorder( title );
        Font font = titledBorder.getTitleFont();
        titledBorder.setTitleFont( TITLE_FONT );
        titledBorder.setBorder( BorderFactory.createLineBorder( Color.LIGHT_GRAY, 1 ) );
        setBorder( titledBorder );
    }
}
