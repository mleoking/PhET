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
        setFont( new PhetFont( 14 ) );
    }

    @Override
    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke( new BasicStroke( 1 ) );
        g2.setPaint( Color.black );
        g2.drawLine( getWidth() / 2, 0, getWidth() / 2, 2 );
    }
}
