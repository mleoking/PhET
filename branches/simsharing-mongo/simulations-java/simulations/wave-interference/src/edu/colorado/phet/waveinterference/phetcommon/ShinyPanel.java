// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.phetcommon;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;

/**
 * User: Sam Reid
 * Date: Jan 22, 2006
 * Time: 9:47:44 PM
 */

public class ShinyPanel extends JPanel {
    private Color lightGray;
    private Color shadedGray;

    public ShinyPanel( JComponent component ) {
        this( component, new Color( 80, 80, 80 ), new Color( 20, 20, 20 ) );
    }

    public ShinyPanel( JComponent component, Color lightGray, Color shadedGray ) {
        this.lightGray = lightGray;
        this.shadedGray = shadedGray;
        setLayout( new BorderLayout() );
        add( component, BorderLayout.CENTER );
        setOpaque( this, false );
        setBorder( new ShinyBorder() );
    }

    private void setOpaque( JComponent container, boolean b ) {

        if ( container instanceof JButton ) {
            container.setForeground( Color.black );
            container.setBackground( Color.white );
//            container.setOpaque( b);
        }
        else {
            container.setOpaque( b );
            container.setBackground( new Color( 255, 255, 255, 0 ) );
            container.setForeground( Color.white );
        }

        for ( int i = 0; i < container.getComponentCount(); i++ ) {
            Component child = container.getComponent( i );
            if ( child instanceof JComponent && !( child instanceof JTextComponent ) ) {
                setOpaque( (JComponent) child, b );
            }
        }
    }

    protected void paintComponent( Graphics g ) {
        GradientPaint gradientPaint = new GradientPaint( 0, 0, lightGray, getWidth(), getHeight(), shadedGray, true );
        Graphics2D g2 = (Graphics2D) g;
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
            Color[] gradient = new Color[8];
            for ( int i = 0; i < gradient.length; i++ ) {
                float value = ( (float) i ) / ( gradient.length - 1 );
                gradient[i] = new Color( 1 - value, 1 - value, 1 - value );
            }
            Border outsiteBorder = new BevelBorder( BevelBorder.RAISED, gradient[0], gradient[1], gradient[7], gradient[6] );
            Border insideBorder = new BevelBorder( BevelBorder.RAISED, gradient[2], gradient[3], gradient[5], gradient[4] );
            return new CompoundBorder( outsiteBorder, insideBorder );
        }
    }

}
