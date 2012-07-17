// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.linegraphing.dev;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import edu.colorado.phet.linegraphing.linegame.view.RewardNode;

/**
 * Tests the game reward. This is a developer control, no i18n required.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DevTestGameRewardMenuItem extends JMenuItem {

    public DevTestGameRewardMenuItem() {
        super( "Test game reward..." );
        addActionListener( new ActionListener() { 
            public void actionPerformed( ActionEvent event ) {
                RewardNode.main( new String[] { } );
            }
        }); 
    }
}
