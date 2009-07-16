/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import edu.colorado.phet.glaciers.GlaciersApplication;

/**
 * DeveloperMenu is the "Developer" menu that appears in the menu bar.
 * This menu contains global developer-only features for tuning and debugging.
 * This menu is not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperMenu extends JMenu {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DeveloperMenu( final GlaciersApplication app ) {
        super( "Developer" );

        final JCheckBoxMenuItem evolutionStateDialogItem = new JCheckBoxMenuItem( "Glacier Evolution State dialog..." );
        add( evolutionStateDialogItem );
        evolutionStateDialogItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                app.setEvolutionStateDialogVisible( evolutionStateDialogItem.isSelected() );
            }
        } );
    }
}
