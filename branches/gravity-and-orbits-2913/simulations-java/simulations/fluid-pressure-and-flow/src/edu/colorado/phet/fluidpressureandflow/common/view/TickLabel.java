// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.awt.*;

import javax.swing.*;

/**
 * @author Sam Reid
 */
public class TickLabel extends JLabel {
    public TickLabel( String label ) {
        super( label );
    }

    @Override
    protected void paintComponent( Graphics g ) {
        Graphics2D g2 = (Graphics2D) g;
        super.paintComponent( g );
        Paint color = g2.getPaint();
        Stroke stroke = g2.getStroke();
        g2.setPaint( Color.black );
        g2.setStroke( new BasicStroke( 1 ) );
        g2.drawLine( getWidth() / 2, 0, getWidth() / 2, 3 );
        g2.setPaint( color );
        g2.setStroke( stroke );
    }
}
