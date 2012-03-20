// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.dev;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import edu.colorado.phet.reactantsproductsandleftovers.module.game.NumberOfVariablesChallengeFactory;
import edu.colorado.phet.reactantsproductsandleftovers.view.game.GameRewardNode;

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
                GameRewardNode.main( new String[] {} );
            }
        }); 
    }
}
