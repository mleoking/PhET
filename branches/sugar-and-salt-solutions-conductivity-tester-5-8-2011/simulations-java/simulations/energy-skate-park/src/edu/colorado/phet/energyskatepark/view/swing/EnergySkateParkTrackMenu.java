// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.swing;

import edu.colorado.phet.energyskatepark.EnergySkateParkApplication;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.serialization.EnergySkateParkIO;

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
        this( EnergySkateParkStrings.getString("tracks-menu.title"), app, getTests() );
    }

    public EnergySkateParkTrackMenu( String label, final EnergySkateParkApplication parentApp, EnergySkateParkTestMenu.TestItem[] testItems ) {
        super( label );
        this.parentApp = parentApp;

        for( int i = 0; i < testItems.length; i++ ) {
            final EnergySkateParkTestMenu.TestItem testItem = testItems[i];
            add( new JMenuItem( new AbstractAction( testItem.getTitle() ) {
                public void actionPerformed( ActionEvent e ) {
                    EnergySkateParkIO.open( testItem.getLocation(), parentApp.getModule() );
                }
            } ) );
        }
    }

    private static EnergySkateParkTestMenu.TestItem[] getTests() {
        return new EnergySkateParkTestMenu.TestItem[]{
                EnergySkateParkTestMenu.TestItem.getTestItemForKey( "energy-skate-park/tests/loop.esp", "tracks-menu.item.loop" ),
                EnergySkateParkTestMenu.TestItem.getTestItemForKey( "energy-skate-park/tests/double-well.esp", "tracks-menu.item.double-well" ),
                EnergySkateParkTestMenu.TestItem.getTestItemForKey( "energy-skate-park/tests/double-well-rc.esp", "tracks-menu.item.double-well-roller-coaster" ),
                EnergySkateParkTestMenu.TestItem.getTestItemForKey( "energy-skate-park/tests/fallthrough_test.esp", "tracks-menu.item.friction-parabola" ),
                EnergySkateParkTestMenu.TestItem.getTestItemForKey( "energy-skate-park/tests/jump.esp", "tracks-menu.item.jump" ),
                EnergySkateParkTestMenu.TestItem.getTestItemForKey( "energy-skate-park/tests/strack4.esp", "tracks-menu.item.s-curve" ),
        };
    }
}
