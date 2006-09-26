/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import edu.colorado.phet.common.view.util.SimStrings;

/**
 * OptionsMenu is the "Options" menu that appears in the menu bar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class OptionsMenu extends JMenu {

    private JRadioButtonMenuItem _brightness1MenuItem;
    private JRadioButtonMenuItem _brightness2MenuItem;
    private JRadioButtonMenuItem _radialDistanceMenuItem;
    private JRadioButtonMenuItem _perspectiveMenuItem;
    
    public OptionsMenu() {
        super( SimStrings.get( "menu.options" ) );
        setMnemonic( SimStrings.get( "menu.options.mnemonic" ).charAt( 0 ) );

        // "Wave View" menu
        {
            JMenu waveViewMenu = new JMenu( SimStrings.get( "menu.option.waveView" ) );
            waveViewMenu.setMnemonic( SimStrings.getChar( "menu.option.waveView.mnemonic" ) );
            add( waveViewMenu );
            
            _brightness1MenuItem = new JRadioButtonMenuItem( SimStrings.get( "menu.option.brightness1" ) );
            _brightness2MenuItem = new JRadioButtonMenuItem( SimStrings.get( "menu.option.brightness2" ) );
            _radialDistanceMenuItem = new JRadioButtonMenuItem( SimStrings.get( "menu.option.radialDistance" ) );
            _perspectiveMenuItem = new JRadioButtonMenuItem( SimStrings.get( "menu.option.perspective" ) );
            
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( _brightness1MenuItem );
            buttonGroup.add( _brightness2MenuItem );
            buttonGroup.add( _radialDistanceMenuItem );
            buttonGroup.add( _perspectiveMenuItem );
            
            waveViewMenu.add( _brightness1MenuItem );
            waveViewMenu.add( _brightness2MenuItem );
            waveViewMenu.add( _radialDistanceMenuItem );
            waveViewMenu.add( _perspectiveMenuItem );
            
            // Event handling
            ActionListener listener = new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    handleWaveViewChange();
                }
            };
            _brightness1MenuItem.addActionListener( listener );
            _brightness2MenuItem.addActionListener( listener );
            _radialDistanceMenuItem.addActionListener( listener );
            _perspectiveMenuItem.addActionListener( listener );
            
            // Default state
            _brightness1MenuItem.setSelected( true );
        }
    }
    
    private void handleWaveViewChange() {
        //XXX
    }
}
