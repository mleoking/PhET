/**
 * Class: TestPhetImageGraphic
 * Package: edu.colorado.phet.common.examples
 * Original Author: Ron LeMaster
 * Creation Date: Nov 19, 2004
 * Creation Time: 10:11:06 AM
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.common.examples;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

public class TestPhetImageGraphic {

    static class TestClass extends PhetImageGraphic {

        public TestClass( Component component ) {
            super( component );
        }

        public Rectangle determineBounds() {
            return super.determineBounds();
        }
    }

    public static void main( String[] args ) {


        ApparatusPanel ap = new ApparatusPanel();
        ap.setBackground( Color.white );
        ap.setPreferredSize( new Dimension( 500, 500 ) );

        TestClass testGraphic1 = new TestClass( ap );

        if( testGraphic1.determineBounds() != null ) {
            throw new RuntimeException( "test failed" );
        }
        ;

        final PhetImageGraphic pig = new PhetImageGraphic( ap, "images/Phet-Flatirons-logo-3-small.gif" );
        pig.setTransform( AffineTransform.getScaleInstance( 1, 1 ) );
        pig.setPosition( 100, 50 );
        ap.addGraphic( pig );

        Timer timer = new Timer( 100, new ActionListener() {
            double xScale = 1;
            double yScale = 1;
            double xLoc = 100;
            double yLoc = 50;

            public void actionPerformed( ActionEvent e ) {
                System.out.println( "xScale:" + xScale );
                xScale = ( xScale + 0.1 ) % 2;
                yScale = ( yScale + 0.05 ) % 2;
                xLoc = ( xLoc + 1 ) % 100;
                yLoc = ( yLoc + 1 ) % 80;
                pig.setPosition( (int)xLoc, (int)yLoc );
                AffineTransform atx = AffineTransform.getTranslateInstance( xLoc, yLoc );
                atx.scale( xScale, yScale );
                pig.setTransform( atx );
            }
        } );
        timer.start();

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( ap );
        frame.pack();
        frame.setVisible( true );
    }
}
