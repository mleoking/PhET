/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.GlaciersStrings;

/**
 * AdvancedClimateControlPanel is the climate control panel for the "Advanced" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AdvancedClimateControlPanel extends JPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color BACKGROUND_COLOR = GlaciersConstants.INNER_PANEL_BACKGROUND_COLOR;
    private static final Color TITLE_COLOR = GlaciersConstants.INNER_PANEL_TITLE_COLOR;
    private static final Color CONTROL_COLOR = GlaciersConstants.INNER_PANEL_CONTROL_COLOR;
    private static final Font TITLE_FONT = GlaciersConstants.CONTROL_PANEL_TITLE_FONT;
    private static final Font CONTROL_FONT = GlaciersConstants.CONTROL_PANEL_CONTROL_FONT;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JRadioButton _snowfallAndTemperatureRadioButton;
    private JRadioButton _climatePresetsRadioButton;
    private JRadioButton _massBalanceRadioButton;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AdvancedClimateControlPanel() {
        super();
        
        _snowfallAndTemperatureRadioButton = new JRadioButton( GlaciersStrings.RADIO_BUTTON_SNOWFALL_AND_TEMPERATURE );
        _snowfallAndTemperatureRadioButton.setFont( CONTROL_FONT );
        _snowfallAndTemperatureRadioButton.setForeground( CONTROL_COLOR );
        
        _climatePresetsRadioButton = new JRadioButton( GlaciersStrings.RADIO_BUTTON_CLIMATE_PRESETS );
        _climatePresetsRadioButton.setFont( CONTROL_FONT );
        _climatePresetsRadioButton.setForeground( CONTROL_COLOR );
        
        _massBalanceRadioButton = new JRadioButton( GlaciersStrings.RADIO_BUTTON_MASS_BALANCE );
        _massBalanceRadioButton.setFont( CONTROL_FONT );
        _massBalanceRadioButton.setForeground( CONTROL_COLOR );
        
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( _snowfallAndTemperatureRadioButton );
        buttonGroup.add( _climatePresetsRadioButton );
        buttonGroup.add( _massBalanceRadioButton );
        
        Border emptyBorder = BorderFactory.createEmptyBorder( 3, 3, 3, 3 );
        TitledBorder titledBorder = new TitledBorder( GlaciersStrings.TITLE_CLIMATE_CONTROLS );
        titledBorder.setTitleFont( TITLE_FONT );
        titledBorder.setTitleColor( TITLE_COLOR );
        titledBorder.setBorder( BorderFactory.createLineBorder( TITLE_COLOR, 1 ) );
        Border compoundBorder = BorderFactory.createCompoundBorder( emptyBorder, titledBorder );
        setBorder( compoundBorder );
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.setAnchor( GridBagConstraints.WEST );
        layout.addComponent( _snowfallAndTemperatureRadioButton, row++, column );
        layout.addComponent( _climatePresetsRadioButton, row++, column );
        layout.addComponent( _massBalanceRadioButton, row++, column );
        row = 0;
        column++;
        layout.setAnchor( GridBagConstraints.CENTER );
        JSeparator separator = new JSeparator( SwingConstants.VERTICAL );
        separator.setForeground( CONTROL_COLOR );
        layout.addFilledComponent( separator, row++, column, 1, 3, GridBagConstraints.VERTICAL );
        row = 0;
        column++;
        layout.setAnchor( GridBagConstraints.CENTER );
        layout.addFilledComponent( Box.createHorizontalStrut( 180 ), row++, column, 1, 3, GridBagConstraints.VERTICAL );
        row = 0;
        column++;
        layout.setAnchor( GridBagConstraints.CENTER );
        JSeparator separator2 = new JSeparator( SwingConstants.VERTICAL );
        separator2.setForeground( CONTROL_COLOR );
        layout.addFilledComponent( separator2, row++, column, 1, 3, GridBagConstraints.VERTICAL );
        row = 0;
        column++;
        layout.setAnchor( GridBagConstraints.CENTER );
        layout.addFilledComponent( Box.createHorizontalStrut( 120 ), row++, column, 1, 3, GridBagConstraints.VERTICAL );
        row = 0;
        column++;
        
        Class[] excludedClasses = { JTextComponent.class, JSpinner.class };
        SwingUtils.setBackgroundDeep( this, BACKGROUND_COLOR, excludedClasses, false /* processContentsOfExcludedContainers */ );
        
        _snowfallAndTemperatureRadioButton.setSelected( true );//XXX
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Listeners
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Notification
    //----------------------------------------------------------------------------
}
