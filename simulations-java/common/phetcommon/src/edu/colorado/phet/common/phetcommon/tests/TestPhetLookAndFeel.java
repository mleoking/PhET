/*  */
package edu.colorado.phet.common.phetcommon.tests;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.test.DeprecatedPhetApplicationLauncher;

/**
 * User: Sam Reid
 * Date: Jun 6, 2006
 * Time: 7:32:47 PM
 */

public class TestPhetLookAndFeel extends DeprecatedPhetApplicationLauncher {
    public TestPhetLookAndFeel( String[] args ) {
        super( args, TestPhetLookAndFeel.class.getName(), "description", "version" );
        addModule( new TestPhetLookAndFeelModule( "Module A" ) );
        addModule( new TestPhetLookAndFeelModule( "Module 1" ) );
    }

    static class TestPhetLookAndFeelExample extends PhetLookAndFeel {
        public TestPhetLookAndFeelExample() {
            setBackgroundColor( Color.blue );
            setForegroundColor( Color.white );
            setFont( new PhetFont( Font.BOLD, 24 ) );
        }
    }

    static class TestPhetLookAndFeelModule extends Module {
        public TestPhetLookAndFeelModule( String name ) {
            super( name, new SwingClock( 30, 1 ) );
            setSimulationPanel( new JLabel( "Hello" ) );
        }
    }

    public static void main( String[] args ) {
        new TestPhetLookAndFeelExample().initLookAndFeel();
        new TestPhetLookAndFeel( args ).startApplication();
    }
}
