/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.control.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import edu.colorado.phet.faraday.view.PickupCoilGraphic;


/**
 * DeveloperMenu implements the Developer menu that appears in the Faraday menubar.
 * This menu is enabled by setting FaradayConfig.DEBUG_ENABLE_DEVELOPER_MENU, and
 * is intended for use in debugging.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DeveloperMenu extends JMenu {
    
    public DeveloperMenu() {
        
        super( "Developer" );
        
        setMnemonic( 'v' );

        // Pickup Coil flux display
        {
            final JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem( "Display pickup coil flux" );
            menuItem.setSelected( PickupCoilGraphic.isDisplayFluxEnabled() );
            menuItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    PickupCoilGraphic.setDisplayFluxEnabled( menuItem.isSelected() );
                }
            } );
            add( menuItem );
        }
    }
}
