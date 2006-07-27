/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.color;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import edu.colorado.phet.boundstates.BSAbstractApplication;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSColorsMenu is the "Colors" menu that appears in the menu bar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSColorsMenu extends JMenu {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String BLACK = "black";
    public static final String WHITE = "white";
    public static final String CUSTOM = "custom";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSAbstractApplication _app;
    private BSColorScheme _presetColorScheme;
    private BSColorScheme _customColorScheme;
    private JDialog _customDialog;
    private JRadioButtonMenuItem _blackItem, _whiteItem, _customItem;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSColorsMenu( BSAbstractApplication app ) {
        super( SimStrings.get( "menu.colors" ) );
        setMnemonic( SimStrings.get( "menu.colors.mnemonic" ).charAt( 0 ) );
        
        _app = app;

        // Black
        _blackItem = new JRadioButtonMenuItem( SimStrings.get( "menu.colors.black" ) );
        _blackItem.setMnemonic( SimStrings.get( "menu.colors.black.mnemonic" ).charAt( 0 ) );
        _blackItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleBlackSelection();
            }
        } );

        // White
        _whiteItem = new JRadioButtonMenuItem( SimStrings.get( "menu.colors.white" ) );
        _whiteItem.setMnemonic( SimStrings.get( "menu.colors.white.mnemonic" ).charAt( 0 ) );
        _whiteItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleWhiteSelection();
            }
        } );

        // Custom
        _customItem = new JRadioButtonMenuItem( SimStrings.get( "menu.colors.custom" ) );
        _customItem.setMnemonic( SimStrings.get( "menu.colors.custom.mnemonic" ).charAt( 0 ) );
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

    public void setColorScheme( String name, BSColorScheme colorScheme ) {
        closeDialog();
        if ( name.equals( BLACK ) ) {
            selectBlack();
        }
        else if ( name.equals( WHITE ) ) {
            selectWhite();
        }
        else {
            _customItem.setSelected( true );
            _customColorScheme = new BSColorScheme( colorScheme );
            _app.setColorScheme( _customColorScheme );
        }
    }
    
    public BSColorScheme getColorScheme() {
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
        _presetColorScheme = new BSBlackColorScheme();
        _app.setColorScheme( _presetColorScheme );
    }

    private void handleWhiteSelection() {
        closeDialog();
        _presetColorScheme = new BSWhiteColorScheme();
        _app.setColorScheme( _presetColorScheme );
    }

    private void handleCustomSelection() {
        closeDialog();
        if ( _customColorScheme == null ) {
            // Use the most-recently selected preset to initialize the custom color scheme.
            _customColorScheme = new BSColorScheme( _presetColorScheme );
        }
        _app.setColorScheme( _customColorScheme );
        // open a non-modal dialog for editing colors...
        _customDialog = new BSColorSchemeDialog( _app, _customColorScheme );
        _customDialog.show();
    }

    private void closeDialog() {
        if ( _customDialog != null ) {
            _customDialog.dispose();
            _customDialog = null;
        }
    }
}
