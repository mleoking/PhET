package edu.colorado.phet.circuitconstructionkit.tests;

import edu.colorado.phet.circuitconstructionkit.phetgraphics.PhetSlider;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Jun 8, 2004
 * Time: 4:59:31 PM
 */
public class TestPhetSlider {
    public static void main( String[] args ) {
        final PhetSlider phetslider = new PhetSlider( "Voltage", "Volts", 0, 9, 3 );
        JFrame jf = new JFrame( "Test ModelSlider" );
        jf.setContentPane( phetslider );
        jf.pack();
        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( jf );
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
