package edu.colorado.phet.ec3;

import edu.colorado.phet.ec3.test.apptests.*;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * User: Sam Reid
 * Date: Oct 18, 2006
 * Time: 8:38:53 AM
 * Copyright (c) Oct 18, 2006 by Sam Reid
 */

public class EnergySkateParkTestMenu extends JMenu {
    private EnergySkateParkApplication parentApp;

    public EnergySkateParkTestMenu( final EnergySkateParkApplication parentApp, final String[] args ) {
        super( "Tests" );
        this.parentApp = parentApp;
        add( new JMenuItem( new AbstractAction( "Fall Through" ) {
            public void actionPerformed( ActionEvent e ) {
                parentApp.closeApplication();
                TestFallThrough.main( args );
            }
        } ) );
        add( new JMenuItem( new AbstractAction( "Head Bounce" ) {
            public void actionPerformed( ActionEvent e ) {
                parentApp.closeApplication();
                TestHeadBounce.main( args );
            }
        } ) );
        add( new JMenuItem( new AbstractAction( "Drop To Floor" ) {
            public void actionPerformed( ActionEvent e ) {
                parentApp.closeApplication();
                TestLongDropToFloor.main( args );
            }
        } ) );
        add( new JMenuItem( new AbstractAction( "Loop" ) {
            public void actionPerformed( ActionEvent e ) {
                parentApp.closeApplication();
                TestLoop.main( args );
            }
        } ) );
        add( new JMenuItem( new AbstractAction( "Upside Down" ) {
            public void actionPerformed( ActionEvent e ) {
                parentApp.closeApplication();
                TestUpsideDown.main( args );
            }
        } ) );
    }
}
