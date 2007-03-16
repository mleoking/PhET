/** Sam Reid*/
package edu.colorado.phet.common_cck.tests.uitest;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalSliderUI;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Apr 3, 2004
 * Time: 11:47:46 PM
 * Copyright (c) Apr 3, 2004 by Sam Reid
 */
public class MySliderUI extends MetalSliderUI {
    private JSlider js;
    MouseHand mh = new MouseHand();

    public MySliderUI( JSlider js ) {
        this.js = js;
    }

    public void paint( Graphics g, JComponent c ) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        super.paint( g, c );
    }

    public static ComponentUI createUI( JComponent c ) {
        return new MySliderUI( (JSlider)c );
    }

    class MouseHand extends MouseInputAdapter {
        public void mouseMoved( MouseEvent e ) {
            //                System.out.println( "thumbRect = " + thumbRect );
            if( thumbRect.contains( e.getX(), e.getY() ) ) {
                e.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
            }
            else {
                e.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
            }
        }
    }

    public void installUI( JComponent c ) {
        super.installUI( c );
        JSlider js = (JSlider)c;
        js.addMouseMotionListener( mh );
    }

    public void uninstallUI( JComponent c ) {
        super.uninstallUI( c );
        JSlider js = (JSlider)c;
        js.removeMouseMotionListener( mh );
    }
}
