/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.graphics.arrows;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Aug 20, 2003
 * Time: 9:24:17 PM
 * Copyright (c) Aug 20, 2003 by Sam Reid
 */
public class ArrowTest {
    private JFrame jf;
    private ApparatusPanel panel;

    public ArrowTest( double arrowTailWidth ) {
        jf = new JFrame( getClass().getName() );

        jf.setSize( Toolkit.getDefaultToolkit().getScreenSize() );
        jf.setExtendedState( JFrame.MAXIMIZED_BOTH );

        panel = new ApparatusPanel();

        TestArrowGraphic tag = new TestArrowGraphic( arrowTailWidth );
        panel.addGraphic( tag, 0 );
        jf.setContentPane( panel );
        jf.repaint();
        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    interface Dragger {
        void drag( Point point );
    }


    class TestArrowGraphic implements InteractiveGraphic {
        public TestArrowGraphic( double tailWidth ) {
            arrow = new Arrow( Color.black, tailWidth );
        }

        class HeadDragger implements Dragger {
            public void drag( Point point ) {
                x2 = point.x;
                y2 = point.y;
            }

        }

        class TailDragger implements Dragger {
            public void drag( Point point ) {
                x = point.x;
                y = point.y;
            }

        }

        Arrow arrow;
        int x = 300;
        int y = 300;
        int x2 = 450;
        int y2 = 150;
        Dragger dragger;

        public boolean canHandleMousePress( MouseEvent event ) {
            return arrow.contains( event.getPoint() );
        }

        public void mousePressed( MouseEvent event ) {

            if( arrow.headContains( event.getPoint() ) ) {
                dragger = new HeadDragger();
            }
            else if( arrow.tailContains( event.getPoint() ) ) {
                dragger = new TailDragger();
            }
        }

        public void mouseDragged( MouseEvent event ) {
            dragger.drag( event.getPoint() );
            panel.repaint();
        }

        public void mouseReleased( MouseEvent event ) {
            dragger = null;
        }

        public void mouseEntered( MouseEvent event ) {
            event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        }

        public void mouseExited( MouseEvent event ) {
            event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
        }

        public void paint( Graphics2D g ) {
            g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            arrow.drawLine( g, x, y, x2, y2 );
        }

    }

    private void start() {
        jf.setVisible( true );
    }
}
