/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.phetcommon;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;

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
//        this( component, new Color( 192, 192, 192 ), new Color( 228, 228, 228 ) );
        this( component, new Color( 80, 80, 80 ), new Color( 20, 20, 20 ) );
    }

    public ShinyPanel( JComponent component, Color lightGray, Color shadedGray ) {
        this.lightGray = lightGray;
        this.shadedGray = shadedGray;
        setLayout( new BorderLayout() );
        add( component, BorderLayout.CENTER );
        setOpaque( this, false );
        setBorder( new ShinyBorder() );
//        addComponentListener( new ComponentListener() {
//            public void componentHidden( ComponentEvent e ) {
//            }
//
//            public void componentMoved( ComponentEvent e ) {
//            }
//
//            public void componentResized( ComponentEvent e ) {
//                setOpaque( ShinyPanel.this, false );
//            }
//
//            public void componentShown( ComponentEvent e ) {
//                setOpaque( ShinyPanel.this, false );
//            }
//        } );
//        addPropertyChangeListener( "background", new PropertyChangeListener() {
//            public void propertyChange( PropertyChangeEvent evt ) {
//                setOpaque( ShinyPanel.this, false );
//            }
//        } );
//        Timer timer=new Timer( 30,new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                setOpaque( ShinyPanel.this,false );
//            }
//        } );
//        timer.start();
    }

    private void setOpaque( JComponent container, boolean b ) {
        container.setOpaque( b );
        container.setBackground( new Color( 255, 255, 255, 0 ) );
        container.setForeground( Color.white );
//
//        container.addPropertyChangeListener( "background", new PropertyChangeListener() {
//            public void propertyChange( PropertyChangeEvent evt ) {
//                setOpaque( ShinyPanel.this, false );
//            }
//        } );

        for( int i = 0; i < container.getComponentCount(); i++ ) {
            Component child = container.getComponent( i );
            if( child instanceof JComponent && !( child instanceof JTextComponent ) ) {
                setOpaque( (JComponent)child, b );
            }
        }
    }

    protected void paintComponent( Graphics g ) {
//        Color lightGray = new Color( 192, 192, 192 );
//        Color shadedGray = new Color( 228, 228, 228 );

//        Color lightGray = Color.gray;
//        Color shadedGray = Color.lightGray;

        GradientPaint gradientPaint = new GradientPaint( 0, 0, lightGray, getWidth(), getHeight(), shadedGray, true );
        Graphics2D g2 = (Graphics2D)g;
        g2.setPaint( gradientPaint );
        g2.fillRect( 0, 0, getWidth(), getHeight() );
        super.paintComponent( g );
    }


    public void update() {
        setOpaque( this, false );
    }

    public static class ShinyBorder extends CompoundBorder {

        public ShinyBorder() {
            super( createCompoundBorder(), new EmptyBorder( 0, 0, 0, 0 ) );
        }

        protected static CompoundBorder createCompoundBorder() {
            Color[]gradient = new Color[8];
            for( int i = 0; i < gradient.length; i++ ) {
                float value = ( (float)i ) / ( gradient.length - 1 );
                gradient[i] = new Color( 1 - value, 1 - value, 1 - value );
            }
            Border outsiteBorder = new BevelBorder( BevelBorder.RAISED, gradient[0], gradient[1], gradient[7], gradient[6] );
            Border insideBorder = new BevelBorder( BevelBorder.RAISED, gradient[2], gradient[3], gradient[5], gradient[4] );
            return new CompoundBorder( outsiteBorder, insideBorder );
        }
    }

}
