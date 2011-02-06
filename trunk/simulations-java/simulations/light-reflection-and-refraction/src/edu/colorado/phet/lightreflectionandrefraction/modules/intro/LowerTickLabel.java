// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import java.awt.*;

import javax.swing.*;

/**
 * @author Sam Reid
 */
public class LowerTickLabel extends JComponent {
    public final JLabel label;

    public LowerTickLabel( String text ) {
        label = new JLabel( text );
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension( (int) label.getPreferredSize().getWidth(), (int) ( label.getPreferredSize().getHeight() + 15 ) );
    }

    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke( new BasicStroke( 1 ) );
        g2.setPaint( Color.black );
        g2.drawLine( getWidth() / 2, 0, getWidth() / 2, 15 );
        g2.setFont( label.getFont() );
        g2.drawString( label.getText(), label.getWidth() / 2, 15 + label.getHeight() + 10 );
    }
}
