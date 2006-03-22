/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.color;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.quantumtunneling.module.QTModule;


/**
 * QTColorSchemeMenu
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTColorSchemeMenu extends JMenu {

    private QTModule _module;
    private IColorScheme _presetColorScheme;
    private CustomColorScheme _customColorScheme;
    private JDialog _customDialog;
    private JRadioButtonMenuItem _blackItem, _whiteItem, _customItem;

    public QTColorSchemeMenu( QTModule module ) {
        super( SimStrings.get( "menu.options.colorScheme" ) );
        setMnemonic( SimStrings.get( "menu.options.colorScheme.mnemonic" ).charAt( 0 ) );
        
        _module = module;

        // Black
        _blackItem = new JRadioButtonMenuItem( SimStrings.get( "menu.options.colorScheme.black" ) );
        _blackItem.setMnemonic( SimStrings.get( "menu.options.colorScheme.black.mnemonic" ).charAt( 0 ) );
        _blackItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleBlackSelection();
            }
        } );

        // White
        _whiteItem = new JRadioButtonMenuItem( SimStrings.get( "menu.options.colorScheme.white" ) );
        _whiteItem.setMnemonic( SimStrings.get( "menu.options.colorScheme.white.mnemonic" ).charAt( 0 ) );
        _whiteItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleWhiteSelection();
            }
        } );

        // Custom
        _customItem = new JRadioButtonMenuItem( SimStrings.get( "menu.options.colorScheme.custom" ) );
        _customItem.setMnemonic( SimStrings.get( "menu.options.colorScheme.custom.mnemonic" ).charAt( 0 ) );
        _customItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleCustomSelection();
            }
        } );

        // Set up radio button group...
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( _blackItem );
        buttonGroup.add( _whiteItem );
        buttonGroup.add( _customItem );
        
        add( _blackItem );
        add( _whiteItem );
        add( _customItem );
        
        // Default selection
        selectBlack();
    }

    public void selectBlack() {
        _blackItem.setSelected( true );
        handleBlackSelection();
    }

    public void selectWhite() {
        _whiteItem.setSelected( true );
        handleWhiteSelection();
    }

    public void setCustomColorScheme( CustomColorScheme colorScheme ) {
        closeDialog();
        _customItem.setSelected( true );
        _customColorScheme = new CustomColorScheme( colorScheme );
        _module.setColorScheme( _customColorScheme );
    }

    private void handleBlackSelection() {
        closeDialog();
        _presetColorScheme = new BlackColorScheme();
        _module.setColorScheme( _presetColorScheme );
    }

    private void handleWhiteSelection() {
        closeDialog();
        _presetColorScheme = new WhiteColorScheme();
        _module.setColorScheme( _presetColorScheme );
    }

    private void handleCustomSelection() {
        closeDialog();
        if ( _customColorScheme == null ) {
            // Use the most-recently selected preset to initialize the custom color scheme.
            _customColorScheme = new CustomColorScheme( _presetColorScheme );
        }
        _module.setColorScheme( _customColorScheme );
        // open a non-modal dialog for editing colors...
        _customDialog = new ColorSchemeDialog( _module, _customColorScheme );
        _customDialog.show();
    }

    private void closeDialog() {
        if ( _customDialog != null ) {
            _customDialog.dispose();
            _customDialog = null;
        }
    }
}
