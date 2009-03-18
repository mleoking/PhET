
package edu.colorado.phet.idealgas.test;

import java.awt.Color;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.idealgas.IdealGasConfig;

/**
 * This test isolates problem #1372, JSpinners are broken on Mac OS.
 * Typing Return/Enter in the spinner does not result in a call to stateChanged.
 */
public class TestJSpinnerApplication extends PhetApplication {
    
    private static final boolean TEST_IN_PHET_FRAMEWORK = true;

    public static class TestClock extends ConstantDtClock {
        public TestClock() {
            super( 100, 1 );
        }
    }
    
    public static class TestPlayArea extends JPanel {
        public TestPlayArea() {
            setBackground( Color.WHITE );
            setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
        }
    }
    
    public static class TestControlPanel extends JPanel {
        public TestControlPanel() {
            final JSpinner spinner = new JSpinner( new SpinnerNumberModel( 0, 0, 100, 1 ) );
            spinner.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    System.out.println( "spinner value = " + spinner.getValue() );
                }
            });
            add( spinner );
        }
    }
    
    public static class TestModule extends Module {
        public TestModule() {
            super( "TestModule", new TestClock() );
            setSimulationPanel( new TestPlayArea() );
            setControlPanel( new TestControlPanel() );
        }
    }
    
    public TestJSpinnerApplication( PhetApplicationConfig config) {
        super(config);
        addModule( new TestModule() );
    }

    public static void main( final String[] args ) {
        if ( TEST_IN_PHET_FRAMEWORK ) {
            // test in the PhET framework
            new PhetApplicationLauncher().launchSim( args, IdealGasConfig.PROJECT_NAME, TestJSpinnerApplication.class );
        }
        else {
            // test in a simple JFrame
            JFrame frame = new JFrame();
            frame.getContentPane().add( new TestControlPanel() );
            frame.pack();
            frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
            frame.setVisible( true );
        }
    }
}
