package org.falstad;// QuantumStates.java (C) 2002 by Paul Falstad, www.falstad.com import java.awt.*;

import java.awt.*;

public class QuantumStatesLayout implements LayoutManager {
    public QuantumStatesLayout() {
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
        int barwidth = 0;
        int i;
        for( i = 1; i < target.getComponentCount(); i++ ) {
            Component m = target.getComponent( i );
            if( m.isVisible() ) {
                Dimension d = m.getPreferredSize();
                if( d.width > barwidth ) {
                    barwidth = d.width;
                }
            }
        }
        Insets insets = target.insets();
        int targetw = target.size().width - insets.left - insets.right;
        int cw = targetw - barwidth;
        int targeth = target.size().height - ( insets.top + insets.bottom );
        target.getComponent( 0 ).move( insets.left, insets.top );
        target.getComponent( 0 ).resize( cw, targeth );
        cw += insets.left;
        int h = insets.top;
        for( i = 1; i < target.getComponentCount(); i++ ) {
            Component m = target.getComponent( i );
            if( m.isVisible() ) {
                Dimension d = m.getPreferredSize();
                if( m instanceof Scrollbar || m instanceof DecentScrollbar ) {
                    d.width = barwidth;
                }
                if( m instanceof Choice && d.width > barwidth ) {
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
};

;

;

;