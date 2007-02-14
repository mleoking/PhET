/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.tests.graphics;

import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

import javax.swing.*;
import java.awt.*;

/**
 * TestPhetGraphics2D
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TestPhetGraphics2D {

    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Test PhetGraphics2D" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        SwingClock clock = new SwingClock( 1, 25 );
        ApparatusPanel ap2 = new ApparatusPanel2( clock );
        clock.start();
        ap2.setPreferredSize( new Dimension( 400, 300 ) );
        frame.setContentPane( ap2 );
        frame.pack();
        frame.setVisible( true );

        PhetGraphic rect = new TestGraphic( ap2 );
        ap2.addGraphic( rect );

        ap2.setBackground( Color.black );

        ap2.revalidate();
        ap2.paintImmediately( ap2.getBounds() );
    }

    static class TestGraphic extends PhetGraphic {
        private Rectangle rect = new Rectangle( 50, 100, 100, 50 );
        Rectangle rect2 = new Rectangle( 150, 200, 100, 50 );

        public TestGraphic( Component ap2 ) {
            super( ap2 );
//            PhetShapeGraphic psg = new PhetShapeGraphic( ap2, new Rectangle( 50, 100, 100, 50 ), Color.red );
//            rect.setLocation( 20, 20 );
//            ap2.addGraphic( psg );
//            PhetShapeGraphic rect2 = new PhetShapeGraphic( ap2, new Rectangle( 150, 200, 100, 50 ), Color.blue );
//            ap2.addGraphic( rect2 );

        }

        protected Rectangle determineBounds() {
            return getComponent().getBounds();
        }

        public void paint( Graphics2D g2 ) {
            saveGraphicsState( g2 );

            g2.setColor( Color.red );
            g2.draw( rect );
            g2.setColor( Color.blue );
            g2.draw( rect2 );

            restoreGraphicsState();
        }
    }
}
