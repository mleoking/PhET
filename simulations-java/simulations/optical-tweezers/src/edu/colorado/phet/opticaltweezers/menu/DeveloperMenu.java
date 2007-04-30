/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.menu;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.dialog.PhysicsDeveloperDialog;

/**
 * DeveloperMenu is the "Developer" menu that appears in the menu bar.
 * This menu contains developer-only features for tuning and debugging.
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
