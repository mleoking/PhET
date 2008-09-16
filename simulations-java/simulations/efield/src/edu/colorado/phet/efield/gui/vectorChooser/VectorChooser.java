// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.gui.vectorChooser;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import javax.swing.*;

import edu.colorado.phet.efield.phys2d_efield.DoublePoint;

// Referenced classes of package edu.colorado.phet.efield.gui.vectorChooser:
//            VectorListener, VectorPainter

public class VectorChooser extends JPanel
        implements MouseListener, MouseMotionListener {

    public VectorChooser( int i, int j, double d, double d1, VectorPainter vectorpainter ) {
        vp = vectorpainter;
        scaleX = d;
        scaleY = d1;
        width = i;
        height = j;
        vectorListeners = new Vector();
        setSize( new Dimension( i, j ) );
        setPreferredSize( new Dimension( i, j ) );
        addMouseListener( this );
        addMouseMotionListener( this );
    }

    public void addListener( VectorListener vectorlistener ) {
        vectorListeners.add( vectorlistener );
    }

    public void mouseClicked( MouseEvent mouseevent ) {
    }

    public void mousePressed( MouseEvent mouseevent ) {
        fireChange( mouseevent.getX(), mouseevent.getY() );
    }

    public void mouseReleased( MouseEvent mouseevent ) {
        fireChange( mouseevent.getX(), mouseevent.getY() );
    }

    private void fireChange( int i, int j ) {
        int k = width / 2;
        int l = height / 2;
        dx = i - k;
        dy = j - l;
        vec = new DoublePoint( (double) dx * scaleX, (double) dy * scaleY );
        repaint();
        informListeners( vec );
    }

    private void informListeners( DoublePoint doublepoint ) {
        for ( int i = 0; i < vectorListeners.size(); i++ ) {
            ( (VectorListener) vectorListeners.get( i ) ).vectorChanged( doublepoint );
        }

    }

    public void mouseEntered( MouseEvent mouseevent ) {
    }

    public void mouseExited( MouseEvent mouseevent ) {
    }

    public void mouseDragged( MouseEvent mouseevent ) {
        fireChange( mouseevent.getX(), mouseevent.getY() );
    }

    public void mouseMoved( MouseEvent mouseevent ) {
    }

    public void paintComponent( Graphics g ) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        super.paintComponent( g );

        vp.paint( (Graphics2D) g, width / 2, height / 2, dx, dy );
    }

    int width;
    int height;
    Vector vectorListeners;
    double scaleX;
    double scaleY;
    int dx;
    int dy;
    DoublePoint vec;
    VectorPainter vp;
}
