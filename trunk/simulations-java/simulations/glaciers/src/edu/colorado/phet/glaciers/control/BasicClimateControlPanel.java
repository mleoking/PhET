/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.GlaciersStrings;

/**
 * BasicClimateControlPanel is the climate control panel for the "Basic" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BasicClimateControlPanel extends JPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color BACKGROUND_COLOR = GlaciersConstants.INNER_PANEL_BACKGROUND_COLOR;
    private static final Color TITLE_COLOR = GlaciersConstants.INNER_PANEL_TITLE_COLOR;
    private static final Font TITLE_FONT = GlaciersConstants.CONTROL_PANEL_TITLE_FONT;
    private static final Color CONTROL_COLOR = GlaciersConstants.INNER_PANEL_CONTROL_COLOR;
    private static final Font CONTROL_FONT = GlaciersConstants.CONTROL_PANEL_CONTROL_FONT;
    
    private static final String CARD_SNOWFALL_AND_TEMPERATURE = "Snowfall and Temperature card";
    private static final String CARD_MASS_BALANCE = "Mass Balance card";

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JRadioButton _snowfallAndTemperatureRadioButton;
    private JRadioButton _massBalanceRadioButton;
    
    private SnowfallAndTemperatureControlPanel _snowfallAndTemperatureControlPanel;
    private MassBalanceControlPanel _massBalanceControlPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BasicClimateControlPanel( 
            DoubleRange snowfallRange, 
            DoubleRange temperatureRange,
            DoubleRange equilibriumLineAltitudeRange,
            DoubleRange massBalanceSlopeRange,
            DoubleRange maximumMassBalanceRange ) {
        super();
        
        _snowfallAndTemperatureRadioButton = new JRadioButton( GlaciersStrings.RADIO_BUTTON_SNOWFALL_AND_TEMPERATURE );
        _snowfallAndTemperatureRadioButton.setFont( CONTROL_FONT );
        _snowfallAndTemperatureRadioButton.setForeground( CONTROL_COLOR );
        
        _massBalanceRadioButton = new JRadioButton( GlaciersStrings.RADIO_BUTTON_MASS_BALANCE );
        _massBalanceRadioButton.setFont( CONTROL_FONT );
        _massBalanceRadioButton.setForeground( CONTROL_COLOR );
        
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( _snowfallAndTemperatureRadioButton );
        buttonGroup.add( _massBalanceRadioButton );
        
        JPanel buttonPanel = new JPanel();
        EasyGridBagLayout buttonPanelLayout = new EasyGridBagLayout( buttonPanel );
        buttonPanel.setLayout( buttonPanelLayout );
        int row = 0;
        int column = 0;
        buttonPanelLayout.addComponent( _snowfallAndTemperatureRadioButton, row++, column );
        buttonPanelLayout.addComponent( _massBalanceRadioButton, row++, column );
        
        _snowfallAndTemperatureControlPanel = new SnowfallAndTemperatureControlPanel( snowfallRange, temperatureRange );
        
        _massBalanceControlPanel = new MassBalanceControlPanel( equilibriumLineAltitudeRange, massBalanceSlopeRange, maximumMassBalanceRange );
        
        final JPanel cardPanel = new JPanel( new CardLayout() );
        cardPanel.add( _snowfallAndTemperatureControlPanel, CARD_SNOWFALL_AND_TEMPERATURE );
        cardPanel.add( _massBalanceControlPanel, CARD_MASS_BALANCE );
        
        _snowfallAndTemperatureRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( _snowfallAndTemperatureRadioButton.isSelected() ) {
                    CardLayout cl = (CardLayout) ( cardPanel.getLayout() );
                    cl.show( cardPanel, CARD_SNOWFALL_AND_TEMPERATURE );
                }
            }
        } );
        
        _massBalanceRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( _massBalanceRadioButton.isSelected() ) {
                    CardLayout cl = (CardLayout) ( cardPanel.getLayout() );
                    cl.show( cardPanel, CARD_MASS_BALANCE );
                }
            }
        } );
        
        Border emptyBorder = BorderFactory.createEmptyBorder( 3, 3, 3, 3 );
        TitledBorder titledBorder = new TitledBorder( GlaciersStrings.TITLE_CLIMATE_CONTROLS );
        titledBorder.setTitleFont( TITLE_FONT );
        titledBorder.setTitleColor( TITLE_COLOR );
        titledBorder.setBorder( BorderFactory.createLineBorder( TITLE_COLOR, 1 ) );
        Border compoundBorder = BorderFactory.createCompoundBorder( emptyBorder, titledBorder );
        setBorder( compoundBorder );
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        row = 0;
        column = 0;
        layout.addComponent( buttonPanel, row, column++ );
//        layout.setAnchor( GridBagConstraints.CENTER );
        JSeparator separator = new JSeparator( SwingConstants.VERTICAL );
        separator.setForeground( CONTROL_COLOR );
        layout.addFilledComponent( separator, row, column++, 1, 3, GridBagConstraints.VERTICAL );
        layout.addComponent( cardPanel, row, column++ );
        
        Class[] excludedClasses = { JSpinner.class };
        SwingUtils.setBackgroundDeep( this, BACKGROUND_COLOR, excludedClasses, false /* processContentsOfExcludedContainers */ );
        
        // default state
        _snowfallAndTemperatureRadioButton.setSelected( true );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public SnowfallAndTemperatureControlPanel getSnowfallAndTemperatureControlPanel() {
        return _snowfallAndTemperatureControlPanel;
    }
    
    public MassBalanceControlPanel getMassBalanceControlPanel() {
        return _massBalanceControlPanel;
    }
}
