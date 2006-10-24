/* Copyright 2004, Sam Reid */
package org.falstad;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jan 17, 2006
 * Time: 5:49:25 PM
 * Copyright (c) Jan 17, 2006 by Sam Reid
 */
public class QuantumCircLayout implements LayoutManager {
    public QuantumCircLayout() {
    }

    public void addLayoutComponent( String name, Component c ) {
    }

    public void removeLayoutComponent( Component c ) {
    }

    public Dimension preferredLayoutSize( Container target ) {
        return new Dimension( 500, 500 );
    }

    public Dimension minimumLayoutSize( Container target ) {
        return new Dimension( 100, 100 );
    }

    public void layoutContainer( Container target ) {
        Insets insets = target.insets();
        int targetw = target.size().width - insets.left - insets.right;
        int cw = targetw * 7 / 10;
        int targeth = target.size().height - ( insets.top + insets.bottom );
        target.getComponent( 0 ).move( insets.left, insets.top );
        target.getComponent( 0 ).resize( cw, targeth );
        int barwidth = targetw - cw;
        cw += insets.left;
        int i;
        int h = insets.top;
        for( i = 1; i < target.getComponentCount(); i++ ) {
            Component m = target.getComponent( i );
            if( m.isVisible() ) {
                Dimension d = m.getPreferredSize();
                if( m instanceof Scrollbar ) {
                    d.width = barwidth;
                }
                if( m instanceof Choice ) {
                    d.width = barwidth;
                }
                if( m instanceof Label ) {
                    h += d.height / 5;
                    d.width = barwidth;
                }
                m.move( cw, h );
                m.resize( d.width, d.height );
                h += d.height;
            }
        }
    }
}
