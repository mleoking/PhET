/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.phetcommon;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

/**
 * User: Sam Reid
 * Date: Jan 22, 2006
 * Time: 9:47:44 PM
 * Copyright (c) Jan 22, 2006 by Sam Reid
 */

public class ShinyPanel extends JPanel {
    private Color lightGray;
    private Color shadedGray;

    public ShinyPanel( JComponent component ) {
        this( component, new Color( 192, 192, 192 ), new Color( 228, 228, 228 ) );
    }

    public ShinyPanel( JComponent component, Color lightGray, Color shadedGray ) {
        this.lightGray = lightGray;
        this.shadedGray = shadedGray;
        add( component );
//        setLayout( new BorderLayout() );
//        add( component, BorderLayout.CENTER );
        init( this, false );
        setBorder( new ShinyBorder() );
        initListeners( component );
    }

    private void initListeners( JComponent component ) {
        component.addComponentListener( new ComponentListener() {
            public void componentHidden( ComponentEvent e ) {
            }

            public void componentMoved( ComponentEvent e ) {
                relayout();
            }

            public void componentResized( ComponentEvent e ) {
                relayout();
            }

            public void componentShown( ComponentEvent e ) {
                relayout();
            }
        } );
        component.addContainerListener( new ContainerListener() {
            public void componentAdded( ContainerEvent e ) {
                relayout();
            }

            public void componentRemoved( ContainerEvent e ) {
                relayout();
            }
        } );
    }

    private void relayout() {

        layoutAll();
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                layoutAll();
            }
        } );
    }

    private void layoutAll() {
        invalidate();
        validateTree();
        revalidate();
        doLayout();
        repaint();
    }

    private void init( JComponent container, boolean b ) {
        container.setOpaque( b );
        if( container instanceof JSlider ) {
            container.setBackground( new Color( 0, 0, 0, 0 ) );
        }
        initListeners( container );
        for( int i = 0; i < container.getComponentCount(); i++ ) {
            Component child = container.getComponent( i );
            if( child instanceof JComponent && !( child instanceof JTextComponent ) ) {
                init( (JComponent)child, b );
            }
        }
    }

    protected void paintComponent( Graphics g ) {
//        Color lightGray = new Color( 192, 192, 192 );
//        Color shadedGray = new Color( 228, 228, 228 );

//        Color lightGray = Color.gray;
//        Color shadedGray = Color.lightGray;

        GradientPaint gradientPaint = new GradientPaint( 0, 0, lightGray, getWidth() / 2, getHeight() / 2, shadedGray, true );
        Graphics2D g2 = (Graphics2D)g;
        g2.setPaint( gradientPaint );
        g2.fillRect( 0, 0, getWidth(), getHeight() );
        super.paintComponent( g );
    }


    public void update() {
        init( this, false );
    }
}
