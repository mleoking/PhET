/** Sam Reid*/
package edu.colorado.phet.cck3.tests;

import edu.colorado.phet.cck3.common.PhetSlider;
import edu.colorado.phet.common.view.util.GraphicsUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Jun 8, 2004
 * Time: 4:59:31 PM
 * Copyright (c) Jun 8, 2004 by Sam Reid
 */
public class TestPhetSlider {
    public static void main( String[] args ) {
        final PhetSlider phetslider = new PhetSlider( "Voltage", "Volts", 0, 9, 3 );
        JFrame jf = new JFrame( "Test ModelSlider" );
        jf.setContentPane( phetslider );
        jf.pack();
        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        GraphicsUtil.centerWindowOnScreen( jf );
//        phetslider.setPaintLabels( false );
//        phetslider.setPaintTicks( false );
        jf.setVisible( true );
        ChangeListener cl = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double value = phetslider.getValue();
                System.out.println( "value = " + value );
            }
        };
        phetslider.addChangeListener( cl );
        phetslider.setNumMajorTicks( 5 );
        phetslider.setNumMinorTicksPerMajorTick( 4 );
    }
}
