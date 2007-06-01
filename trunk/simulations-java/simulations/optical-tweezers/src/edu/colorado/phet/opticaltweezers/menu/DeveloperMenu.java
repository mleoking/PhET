/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;

/**
 * DeveloperMenu is the "Developer" menu that appears in the menu bar.
 * This menu contains global developer-only features for tuning and debugging.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperMenu extends JMenu implements ActionListener {
    
    public DeveloperMenu() {
        super( "Developer" );
    }

    public void actionPerformed( ActionEvent event ) {
        //XXX
    }
}
