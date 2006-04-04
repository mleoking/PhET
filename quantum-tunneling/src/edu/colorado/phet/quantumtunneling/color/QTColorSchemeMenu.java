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
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String BLACK = "black";
    public static final String WHITE = "white";
    public static final String CUSTOM = "custom";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private QTModule _module;
    private QTColorScheme _presetColorScheme;
    private QTColorScheme _customColorScheme;
    private JDialog _customDialog;
    private JRadioButtonMenuItem _blackItem, _whiteItem, _customItem;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public QTColorSchemeMenu( QTModule module ) {
        super( SimStrings.get( "menu.colorScheme" ) );
        setMnemonic( SimStrings.get( "menu.colorScheme.mnemonic" ).charAt( 0 ) );
        
        _module = module;

        // Black
        _blackItem = new JRadioButtonMenuItem( SimStrings.get( "menu.colorScheme.black" ) );
        _blackItem.setMnemonic( SimStrings.get( "menu.colorScheme.black.mnemonic" ).charAt( 0 ) );
        _blackItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleBlackSelection();
            }
        } );

        // White
        _whiteItem = new JRadioButtonMenuItem( SimStrings.get( "menu.colorScheme.white" ) );
        _whiteItem.setMnemonic( SimStrings.get( "menu.colorScheme.white.mnemonic" ).charAt( 0 ) );
        _whiteItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleWhiteSelection();
            }
        } );

        // Custom
        _customItem = new JRadioButtonMenuItem( SimStrings.get( "menu.colorScheme.custom" ) );
        _customItem.setMnemonic( SimStrings.get( "menu.colorScheme.custom.mnemonic" ).charAt( 0 ) );
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

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void selectBlack() {
        _blackItem.setSelected( true );
        handleBlackSelection();
    }

    public void selectWhite() {
        _whiteItem.setSelected( true );
        handleWhiteSelection();
    }

    public void setColorScheme( String name, QTColorScheme colorScheme ) {
        closeDialog();
        if ( name.equals( BLACK ) ) {
            selectBlack();
        }
        else if ( name.equals( WHITE ) ) {
            selectWhite();
        }
        else {
            _customItem.setSelected( true );
            _customColorScheme = new QTColorScheme( colorScheme );
            _module.setColorScheme( _customColorScheme );
        }
    }
    
    public QTColorScheme getColorScheme() {
        if ( _customItem.isSelected() ) {
            return _customColorScheme;
        }
        else {
            return _presetColorScheme;
        }
    }
    
    public String getColorSchemeName() {
        String name = null;
        if ( _blackItem.isSelected() ) {
            name = BLACK;
        }
        else if ( _whiteItem.isSelected() ) {
            name = WHITE;
        }
        else if ( _customItem.isSelected() ) {
            name = CUSTOM;
        }
        return name;
    }

    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
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
            _customColorScheme = new QTColorScheme( _presetColorScheme );
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
