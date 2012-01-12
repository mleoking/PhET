// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.dev;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import edu.colorado.phet.reactantsproductsandleftovers.module.game.NumberOfVariablesChallengeFactory;

/**
 * Developer menu item that runs tests on all the chemical reactions used in the sim,
 * checking them for compliance with various assumptions we've made.
 * Prints output to System.out.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DevTestReactionsMenuItem extends JMenuItem {

    public DevTestReactionsMenuItem() {
        super( "Test all chemical reactions (see System.out)" );
        addActionListener( new ActionListener() { 
            public void actionPerformed( ActionEvent event ) {
                NumberOfVariablesChallengeFactory.main( null );
            }
        }); 
    }
}
