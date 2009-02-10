package edu.colorado.phet.acidbasesolutions.control;

import java.text.DecimalFormat;

import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LogarithmicValueControl;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Control used to modify and display the strength the molecule introduced into the solution.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WeakStrengthControl extends LogarithmicValueControl {

    private static final String LABEL = "";
    private static final String UNITS = "";
    private static final String VALUE_FORMAT = "0.0E0";
    private static final String TICK_FORMAT = "0E0";
    
    public WeakStrengthControl() {
        super( ABSConstants.MIN_WEAK_STRENGTH, ABSConstants.MAX_WEAK_STRENGTH, LABEL, VALUE_FORMAT, UNITS, new HorizontalLayoutStrategy() );
        setValue( ABSConstants.DEFAULT_WEAK_STRENGTH );
        setTickPattern( TICK_FORMAT );
    }

    // test
    public static void main( String[] args ) {
        
        final DecimalFormat format = new DecimalFormat( "0.000E0" );
        final WeakStrengthControl control = new WeakStrengthControl();
        control.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                System.out.println( format.format( control.getValue() ) );
            }
        } );
        
        JFrame frame = new JFrame();
        frame.getContentPane().add( control );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }
}
