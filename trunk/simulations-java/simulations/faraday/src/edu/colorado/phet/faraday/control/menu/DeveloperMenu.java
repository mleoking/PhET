/* Copyright 2005-2008, University of Colorado */

package edu.colorado.phet.faraday.control.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import edu.colorado.phet.faraday.view.ElectromagnetGraphic;
import edu.colorado.phet.faraday.view.PickupCoilGraphic;


/**
 * DeveloperMenu implements the Developer menu that appears in the Faraday menubar.
 * This menu is enabled by setting FaradayConfig.DEBUG_ENABLE_DEVELOPER_MENU, and
 * is intended for use in debugging.
 * <p>
 * Since this is a debugging menu, it is not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperMenu extends JMenu {
    
    public DeveloperMenu() {
        
        super( "Developer" );
        
        setMnemonic( 'v' );
        
        // Pickup Coil sample points
        final JCheckBoxMenuItem samplesMenuItem = new JCheckBoxMenuItem( "Show pickup coil sample points" );
        samplesMenuItem.setSelected( PickupCoilGraphic.DEBUG_DRAW_PICKUP_SAMPLE_POINTS );
        samplesMenuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PickupCoilGraphic.DEBUG_DRAW_PICKUP_SAMPLE_POINTS = samplesMenuItem.isSelected();
            }
        } );
        add( samplesMenuItem );
        
        // Pickup Coil flux display
        final JCheckBoxMenuItem fluxMenuItem = new JCheckBoxMenuItem( "Display pickup coil flux" );
        fluxMenuItem.setSelected( PickupCoilGraphic.DEBUG_DISPLAY_FLUX );
        fluxMenuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PickupCoilGraphic.DEBUG_DISPLAY_FLUX = fluxMenuItem.isSelected();
            }
        } );
        add( fluxMenuItem );
        
        // Electromagnet shape
        final JCheckBoxMenuItem electromagnetShapeItem = new JCheckBoxMenuItem( "Show electromagnet model shape" );
        electromagnetShapeItem.setSelected( ElectromagnetGraphic.DEBUG_DRAW_ELECTROMAGNET_MODEL_SHAPE );
        electromagnetShapeItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ElectromagnetGraphic.DEBUG_DRAW_ELECTROMAGNET_MODEL_SHAPE = electromagnetShapeItem.isSelected();
            }
        } );
        add( electromagnetShapeItem ); 
    }
}
