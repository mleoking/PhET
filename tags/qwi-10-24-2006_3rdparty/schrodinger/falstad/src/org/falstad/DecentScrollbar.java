/* Copyright 2004, Sam Reid */
package org.falstad;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * User: Sam Reid
 * Date: Feb 9, 2006
 * Time: 9:13:28 PM
 * Copyright (c) Feb 9, 2006 by Sam Reid
 */
class DecentScrollbar extends Canvas implements MouseListener, MouseMotionListener {
    int value, lo, hi;
    DecentScrollbarListener listener;

    DecentScrollbar( DecentScrollbarListener parent, int start, int lo_, int hi_ ) {
        value = start;
        lo = lo_;
        hi = hi_;
        listener = parent;
        addMouseListener( this );
        addMouseMotionListener( this );
        gray1 = new Color( 104, 104, 104 );
        gray2 = new Color( 168, 168, 168 );
        gray3 = new Color( 192, 192, 192 );
        gray4 = new Color( 224, 224, 224 );
    }

    Color gray1, gray2, gray3, gray4;

    public Dimension getPreferredSize() {
        return new Dimension( 20, 20 );
    }

    static final int tw = 8;
    boolean dragging;
    int thumbpos, dragoffset;

    public void paint( Graphics g ) {
        Dimension size = getSize();
        int w = size.width;
        int h = size.height;
        int x = thumbpos = ( value - lo ) * ( w - 2 - tw ) / ( hi - lo ) + 1;
        g.setColor( gray2 );
        g.fillRect( 0, 0, w, h );
        g.setColor( gray3 );
        g.fillRect( x, 2, tw, h - 4 );
        g.setColor( gray4 );
        g.drawLine( 0, h - 1, w, h - 1 );
        g.drawLine( w - 1, 1, w - 1, h - 1 );
        g.drawLine( x, 1, x + tw - 1, 1 );
        g.drawLine( x, 1, x, h - 2 );
        g.setColor( gray1 );
        g.drawLine( 0, 0, w - 1, 0 );
        g.drawLine( 0, 0, 0, h - 1 );
        g.drawLine( x + tw - 1, 2, x + tw - 1, h - 2 );
        g.drawLine( x + 1, h - 2, x + tw - 1, h - 2 );
    }

    int getValue() {
        return value;
    }

    boolean setValue( int v ) {
        if( v < lo ) {
            v = lo;
        }
        if( v > hi ) {
            v = hi;
        }
        if( value == v ) {
            return false;
        }
        value = v;
        repaint();
        return true;
    }

    public void mousePressed( MouseEvent e ) {
        if( thumbpos <= e.getX() && thumbpos + tw >= e.getX() ) {
            dragging = true;
            dragoffset = e.getX() - thumbpos;
        }
    }

    public void mouseReleased( MouseEvent e ) {
        if( dragging ) {
            listener.scrollbarFinished( this );
        }
        dragging = false;
    }

    public void mouseClicked( MouseEvent e ) {
    }

    public void mouseEntered( MouseEvent e ) {
    }

    public void mouseExited( MouseEvent e ) {
    }

    public void mouseDragged( MouseEvent e ) {
        if( !dragging ) {
            return;
        }
        int x = e.getX() - dragoffset;
        Dimension size = getSize();
        int v = ( x - 1 ) * ( hi - lo ) / ( size.width - 2 - tw ) + lo;
        if( setValue( v ) ) {
            listener.scrollbarValueChanged( this );
        }
    }

    public void mouseMoved( MouseEvent e ) {
    }
}
