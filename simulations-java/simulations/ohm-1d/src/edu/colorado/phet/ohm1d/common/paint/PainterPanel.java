package edu.colorado.phet.ohm1d.common.paint;

import java.awt.*;

import javax.swing.*;

public class PainterPanel extends JPanel {
    Painter p;

    public PainterPanel( Painter p ) {
        this.p = p;
    }

    public void paintComponent( Graphics g ) {
        super.paintComponent( g );
        if ( p != null ) {
            Graphics2D g2 = (Graphics2D) g;
            p.paint( g2 );
        }
    }
}
