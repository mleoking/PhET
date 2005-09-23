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

import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

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
    private static final Color BORDER_COLOR = new Color( 175, 175, 175 );
    
    public FourierTitledPanel( String title ) {
        TitledBorder titledBorder = new TitledBorder( title );
        Font font = titledBorder.getTitleFont();
        titledBorder.setTitleFont( TITLE_FONT );
        titledBorder.setBorder( BorderFactory.createLineBorder( BORDER_COLOR, 1 ) );
        setBorder( titledBorder );
    }
    
    /**
     * Windows doesn't anti-alias Swing components.
     * Since we're using an italic font, it looks particularly ugly.
     * So override paintComponent and enabled anti-aliasing.
     */
    public void paintComponent( Graphics g ) {
        if ( g instanceof Graphics2D ) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        }
        super.paintComponent( g );
    }
}
