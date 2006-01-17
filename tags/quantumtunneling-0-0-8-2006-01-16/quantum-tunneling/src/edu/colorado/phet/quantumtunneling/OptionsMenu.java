/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.quantumtunneling.module.QTModule;


/**
 * OptionsMenu implements the Options menu that appears in the menubar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class OptionsMenu extends JMenu {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private QTModule _module;
    private JCheckBoxMenuItem _showValuesMenuItem;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param application
     */
    public OptionsMenu( QTModule module ) {
        
        super( SimStrings.get( "menu.options" ) );
        
        _module = module;
        
        setMnemonic( SimStrings.get( "menu.options.mnemonic" ).charAt( 0 ) );

        // Background Color menu item
        _showValuesMenuItem = new JCheckBoxMenuItem( SimStrings.get( "menu.options.showValues" ) );
        _showValuesMenuItem.setSelected( _module.isValuesVisible() );
        _showValuesMenuItem.setMnemonic( SimStrings.get( "menu.options.showValues.mnemonic" ).charAt( 0 ) );
        _showValuesMenuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleShowValues();
            }
        } );
        add( _showValuesMenuItem );
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setShowValuesSelected( boolean selected ) {
        _showValuesMenuItem.setSelected( selected );
    }
    
    public boolean getShowValuesSelected() {
        return _showValuesMenuItem.isSelected();
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    /**
     * Toggles the state of "Show Values".
     */
    public void handleShowValues() {
        boolean enabled = _showValuesMenuItem.isSelected();
        _module.setValuesVisible( enabled );
    }
}
