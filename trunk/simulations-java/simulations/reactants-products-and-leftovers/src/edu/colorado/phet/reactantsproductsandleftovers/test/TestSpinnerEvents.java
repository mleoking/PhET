package edu.colorado.phet.reactantsproductsandleftovers.test;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.reactantsproductsandleftovers.controls.IntegerSpinner;


public class TestSpinnerEvents extends JFrame {
    
    private static final IntegerRange SPINNER_RANGE = new IntegerRange( 0, 10, 0 );
    
    private final IntegerSpinner spinner1, spinner2;

    public TestSpinnerEvents() {
        super( TestSpinnerEvents.class.getName() );
        spinner1 = new IntegerSpinner( SPINNER_RANGE );
        spinner2 = new IntegerSpinner( SPINNER_RANGE );
        JPanel panel = new JPanel();
        panel.add( spinner1 );
        panel.add( spinner2 );
        setContentPane( panel );
        setSize( new Dimension( 300, 200 ) );
    }
    
    public static void main( String[] args ) {
        JFrame frame = new TestSpinnerEvents();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
