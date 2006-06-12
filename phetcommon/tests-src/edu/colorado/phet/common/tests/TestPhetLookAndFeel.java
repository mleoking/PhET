/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.tests;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.PhetLookAndFeel;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 6, 2006
 * Time: 7:32:47 PM
 * Copyright (c) Jun 6, 2006 by Sam Reid
 */

public class TestPhetLookAndFeel extends PhetApplication {
    public TestPhetLookAndFeel( String[] args ) {
        super( args, TestPhetLookAndFeel.class.getName(), "description", "version" );
        addModule( new TestPhetLookAndFeelModule( "Module A" ) );
        addModule( new TestPhetLookAndFeelModule( "Module 1" ) );
    }

    static class TestPhetLookAndFeelExample extends PhetLookAndFeel {
        public TestPhetLookAndFeelExample() {
            setBackgroundColor( Color.blue );
            setForegroundColor( Color.white );
            setFont( new Font( "Lucida Sans", Font.BOLD, 24 ) );
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
