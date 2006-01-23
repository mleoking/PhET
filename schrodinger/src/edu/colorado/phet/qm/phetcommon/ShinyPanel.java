/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.phetcommon;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jan 22, 2006
 * Time: 9:47:44 PM
 * Copyright (c) Jan 22, 2006 by Sam Reid
 */

public class ShinyPanel extends JPanel {
    public ShinyPanel( JComponent component ) {
        setLayout( new BorderLayout() );
        add( component, BorderLayout.CENTER );
        setOpaque( this, false );
        setBorder( new ShinyBorder() );
    }

    private void setOpaque( JComponent container, boolean b ) {
        container.setOpaque( b );
        for( int i = 0; i < container.getComponentCount(); i++ ) {
            Component child = container.getComponent( i );
            if( child instanceof JComponent && !( child instanceof JTextComponent ) ) {
                setOpaque( (JComponent)child, b );
            }
        }
    }

    protected void paintComponent( Graphics g ) {
        Color lightGray = new Color( 192, 192, 192 );
        Color shadedGray = new Color( 228, 228, 228 );
        GradientPaint gradientPaint = new GradientPaint( 0, 0, lightGray, getWidth() / 2, getHeight() / 2, shadedGray, true );
        Graphics2D g2 = (Graphics2D)g;
        g2.setPaint( gradientPaint );
        g2.fillRect( 0, 0, getWidth(), getHeight() );
        super.paintComponent( g );
    }


}
