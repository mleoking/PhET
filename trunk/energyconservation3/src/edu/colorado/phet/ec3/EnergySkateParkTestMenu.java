package edu.colorado.phet.ec3;

import edu.colorado.phet.ec3.test.apptests.*;
import edu.colorado.phet.ec3.test.phys1d.Test2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        addSeparator();

        TestItem[] testItems = new TestItem[]{
                new TestItem( "esp/tests/a.esp", "Head Bounce Get Stuck" ),
                new TestItem( "esp/tests/double-fall.esp", "Double Well fall-through" ),
                new TestItem( "esp/tests/moon-upside.esp", "Upside-Down on moon" ),
                new TestItem( "esp/tests/angle.esp", "Slight angle/speed" ),
                new TestItem( "esp/tests/fall-through-ground.esp", "Shoulder through ground" ),
                new TestItem( "esp/tests/dw-rc.esp", "double well roller coaster" )
        };
        for( int i = 0; i < testItems.length; i++ ) {
            final TestItem testItem = testItems[i];
            add( new JMenuItem( new AbstractAction( testItem.getTitle() ) {
                public void actionPerformed( ActionEvent e ) {
                    parentApp.getModule().open( testItem.getLocation() );
                }
            } ) );
        }

        addSeparator();
        JMenuItem jMenuItem = new JMenuItem( "New Spline" );
        jMenuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Test2.main( new String[0] );
            }
        } );
        add( jMenuItem );
    }

    class TestItem {
        private String location;
        private String title;

        public TestItem( String location, String title ) {
            this.location = location;
            this.title = title;
        }

        public String getLocation() {
            return location;
        }

        public String getTitle() {
            return title;
        }
    }
}
