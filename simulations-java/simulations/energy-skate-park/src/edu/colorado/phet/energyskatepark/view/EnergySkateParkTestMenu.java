package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.energyskatepark.EnergySkateParkApplication;
import edu.colorado.phet.energyskatepark.model.physics.TestPhysics1D;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Oct 18, 2006
 * Time: 8:38:53 AM
 *
 */

public class EnergySkateParkTestMenu extends JMenu {
    private EnergySkateParkApplication parentApp;

    public EnergySkateParkTestMenu( final EnergySkateParkApplication parentApp, final String[] args ) {
        super( "Tests" );
        this.parentApp = parentApp;

        TestItem[] testItems = new TestItem[]{
                new TestItem( "energy-skate-park/tests/a.esp", "Head Bounce Get Stuck" ),
                new TestItem( "energy-skate-park/tests/double-fall.esp", "Double Well fall-through" ),
                new TestItem( "energy-skate-park/tests/moon-upside.esp", "Upside-Down on moon" ),
                new TestItem( "energy-skate-park/tests/angle.esp", "Slight angle/speed" ),
                new TestItem( "energy-skate-park/tests/fall-through-ground.esp", "Shoulder through ground" ),
                new TestItem( "energy-skate-park/tests/dw-rc.esp", "double well roller coaster" ),
                new TestItem( "energy-skate-park/tests/skater-jump.esp", "Skater Jump" ),
                new TestItem( "energy-skate-park/tests/droptofloor_test.esp", "Drop to floor" ),
                new TestItem( "energy-skate-park/tests/fallthrough_test.esp", "High Friction parabolic" ),
                new TestItem( "energy-skate-park/tests/loop_test.esp", "Loop Test" ),
                new TestItem( "energy-skate-park/tests/upside-down_test.esp", "Upside Down" )
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
                TestPhysics1D.main( new String[0] );
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
