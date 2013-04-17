// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * Label to be shown for the sliders in the MediumControlPanel.
 *
 * @author Sam Reid
 */
public class TickLabel extends JLabel {
    public TickLabel( String text ) {
        super( text );
        setFont( new PhetFont( 14 ) );//Bigger font to improve readability.
    }

    //Draw the tick and the label
    @Override protected void paintComponent( Graphics g ) {
        //Draw the text
        super.paintComponent( g );
        Graphics2D g2 = (Graphics2D) g;

        //Store original settings
        Stroke origStroke = g2.getStroke();
        Paint origPaint = g2.getPaint();

        //Paint the tick
        g2.setStroke( new BasicStroke( 1 ) );
        g2.setPaint( Color.black );
        g2.drawLine( getWidth() / 2, 0, getWidth() / 2, 2 );

        //Restore original settings
        g2.setStroke( origStroke );
        g2.setPaint( origPaint );
    }
}
