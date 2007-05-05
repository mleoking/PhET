package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.energyskatepark.EnergySkateParkApplication;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * User: Sam Reid
 * Date: Oct 18, 2006
 * Time: 8:38:53 AM
 */

public class EnergySkateParkTrackMenu extends JMenu {
    private EnergySkateParkApplication parentApp;

    public EnergySkateParkTrackMenu( EnergySkateParkApplication app ) {
        this( "Tracks", app, getTests() );
    }

    public EnergySkateParkTrackMenu( String label, final EnergySkateParkApplication parentApp, EnergySkateParkTestMenu.TestItem[] testItems ) {
        super( label );
        this.parentApp = parentApp;

        for( int i = 0; i < testItems.length; i++ ) {
            final EnergySkateParkTestMenu.TestItem testItem = testItems[i];
            add( new JMenuItem( new AbstractAction( testItem.getTitle() ) {
                public void actionPerformed( ActionEvent e ) {
                    parentApp.getModule().open( testItem.getLocation() );
                }
            } ) );
        }
    }

    private static EnergySkateParkTestMenu.TestItem[] getTests() {
        EnergySkateParkTestMenu.TestItem[] testItems = new EnergySkateParkTestMenu.TestItem[]{
                new EnergySkateParkTestMenu.TestItem( "energy-skate-park/tests/loop.esp", "Loop" ),
                new EnergySkateParkTestMenu.TestItem( "energy-skate-park/tests/double-well.esp", "Double Well" ),
                new EnergySkateParkTestMenu.TestItem( "energy-skate-park/tests/double-well-rc.esp", "Double Well (Roller Coaster)" ),
                new EnergySkateParkTestMenu.TestItem( "energy-skate-park/tests/fallthrough_test.esp", "Friction Parabola" ),
                new EnergySkateParkTestMenu.TestItem( "energy-skate-park/tests/jump.esp", "Jump" ),
                new EnergySkateParkTestMenu.TestItem( "energy-skate-park/tests/strack4.esp", "S-Curve" ),
        };
        return testItems;
    }
}
