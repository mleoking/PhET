// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.glaciers.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.view.*;

/**
 * ViewControlPanel is the "View" control panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ViewControlPanel extends AbstractSubPanel {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color BACKGROUND_COLOR = GlaciersConstants.SUBPANEL_BACKGROUND_COLOR;
    private static final String TITLE_STRING = GlaciersStrings.TITLE_VIEW_CONTROLS;
    private static final Color TITLE_COLOR = GlaciersConstants.SUBPANEL_TITLE_COLOR;
    private static final Font TITLE_FONT = GlaciersConstants.SUBPANEL_TITLE_FONT;
    private static final Color CONTROL_COLOR = GlaciersConstants.SUBPANEL_CONTROL_COLOR;
    private static final Font CONTROL_FONT = GlaciersConstants.SUBPANEL_CONTROL_FONT;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final GlaciersPlayArea _playArea;
    
    private final JRadioButton _englishUnitsButton, _metricUnitsButton;
    private final JCheckBox _equilibriumLineCheckBox;
    private final JCheckBox _iceFlowCheckBox;
    private final JCheckBox _coordinatesCheckBox;
    private final JCheckBox _snowfallCheckBox;
    private final JPanel _iceFlowPanel, _coordinatesPanel;
    
    private final ArrayList _unitsChangeListeners;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ViewControlPanel( GlaciersPlayArea playArea ) {
        super( TITLE_STRING, TITLE_COLOR, TITLE_FONT );
        
        _unitsChangeListeners = new ArrayList();
        
        _playArea = playArea;
        
        JPanel unitsPanel = new JPanel();
        {
            JLabel unitsLabel = new JLabel( GlaciersStrings.LABEL_UNITS );
            unitsLabel.setFont( CONTROL_FONT );
            unitsLabel.setForeground( CONTROL_COLOR );
            
            _englishUnitsButton = new JRadioButton( GlaciersStrings.RADIO_BUTTON_ENGLISH_UNITS );
            _englishUnitsButton.setFont( CONTROL_FONT );
            _englishUnitsButton.setForeground( CONTROL_COLOR );
            _englishUnitsButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    handleUnitsButton();
                }
            });
            
            _metricUnitsButton = new JRadioButton( GlaciersStrings.RADIO_BUTTON_METRIC_UNITS );
            _metricUnitsButton.setFont( CONTROL_FONT );
            _metricUnitsButton.setForeground( CONTROL_COLOR );
            _metricUnitsButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    handleUnitsButton();
                }
            });
            
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( _englishUnitsButton );
            buttonGroup.add( _metricUnitsButton );
            _englishUnitsButton.setSelected( true );
            
            unitsPanel.add( unitsLabel );
            unitsPanel.add( _englishUnitsButton );
            unitsPanel.add( _metricUnitsButton );
        }
        
        JPanel equilibriumLinePanel = new JPanel();
        {
            _equilibriumLineCheckBox = new JCheckBox( GlaciersStrings.CHECK_BOX_EQUILIBRIUM_LINE );
            _equilibriumLineCheckBox.setFont( CONTROL_FONT );
            _equilibriumLineCheckBox.setForeground( CONTROL_COLOR );
            _equilibriumLineCheckBox.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    handleEquilibriumLineCheckBox();
                }
            } );
            
            JLabel equilibriumLineIcon = new JLabel( EquilibriumLineNode.createIcon() );
            
            equilibriumLinePanel.add( _equilibriumLineCheckBox );
            equilibriumLinePanel.add( equilibriumLineIcon );
        }
        
        _iceFlowPanel = new JPanel();
        {
            _iceFlowCheckBox = new JCheckBox( GlaciersStrings.CHECK_BOX_ICE_FLOW );
            _iceFlowCheckBox.setFont( CONTROL_FONT );
            _iceFlowCheckBox.setForeground( CONTROL_COLOR );
            _iceFlowCheckBox.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    handleIceFlowCheckBox();
                }
            } );
            
            JLabel iceFlowIcon = new JLabel( IceFlowNode.createIcon() );
            
            _iceFlowPanel.add( _iceFlowCheckBox );
            _iceFlowPanel.add( iceFlowIcon );
        }
        
        _coordinatesPanel = new JPanel();
        {
            _coordinatesCheckBox = new JCheckBox( GlaciersStrings.CHECK_BOX_COORDINATES );
            _coordinatesCheckBox.setFont( CONTROL_FONT );
            _coordinatesCheckBox.setForeground( CONTROL_COLOR );
            _coordinatesCheckBox.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    handleCoordinatesCheckBox();
                }
            } );
            
            JLabel coordinatesIcon = new JLabel( CoordinatesNode.createIcon() );
            
            _coordinatesPanel.add( _coordinatesCheckBox );
            _coordinatesPanel.add( coordinatesIcon );
        }
        
        JPanel snowfallPanel = new JPanel();
        {
            _snowfallCheckBox = new JCheckBox( GlaciersStrings.CHECK_BOX_SNOWFALL );
            _snowfallCheckBox.setFont( CONTROL_FONT );
            _snowfallCheckBox.setForeground( CONTROL_COLOR );
            _snowfallCheckBox.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    handleSnowfallCheckBox();
                }
            } );
            
            JLabel snowfallIcon = new JLabel( SnowfallNode.createIcon() );
            
            snowfallPanel.add( _snowfallCheckBox );
            snowfallPanel.add( snowfallIcon );
        }
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        layout.setInsets( new Insets( 0, 0, 0, 0 ) );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.addComponent( unitsPanel, 0, 0, 2, 1 );
        layout.addComponent( equilibriumLinePanel, 1, 0 );
        layout.addComponent( snowfallPanel, 2, 0 );
        layout.addComponent( _iceFlowPanel, 1, 1 );
        layout.addComponent( _coordinatesPanel, 2, 1 );
        
        SwingUtils.setBackgroundDeep( this, BACKGROUND_COLOR, null /* excludedClasses */, false /* processContentsOfExcludedContainers */ );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setEnglishUnitsSelected( boolean selected ) {
        if ( selected != isEnglishUnitsSelected() ) {
            _englishUnitsButton.setSelected( selected );
            handleUnitsButton();
        }
    }
    
    public boolean isEnglishUnitsSelected() {
        return _englishUnitsButton.isSelected();
    }
    
    public void setMetricUnitsSelected( boolean selected ) {
        setEnglishUnitsSelected( !selected );
    }
    
    public boolean isMetricUnitsSelected() {
        return _metricUnitsButton.isSelected();
    }
    
    public void setEquilibriumLineSelected( boolean selected ) {
        if ( selected != isEquilibriumLineSelected() ) {
            _equilibriumLineCheckBox.setSelected( selected );
            handleEquilibriumLineCheckBox();
        }
    }
    
    public boolean isEquilibriumLineSelected() {
        return _equilibriumLineCheckBox.isSelected();
    }
    
    public void setIceFlowSelected( boolean selected ) {
        if ( selected != isIceFlowSelected() ) {
            _iceFlowCheckBox.setSelected( selected );
            handleIceFlowCheckBox();
        }
    }
    
    public boolean isIceFlowSelected() {
        return _iceFlowCheckBox.isSelected();
    }
    
    public void setCoordinatesSelected( boolean selected ) {
        if ( selected != isCoordinatesSelected() ) {
            _coordinatesCheckBox.setSelected( selected );
            handleCoordinatesCheckBox();
        }
    }
    
    public boolean isCoordinatesSelected() {
        return _coordinatesCheckBox.isSelected();
    }
    
    public void setSnowfallSelected( boolean selected ) {
        if ( selected != isSnowfallSelected() ) {
            _snowfallCheckBox.setSelected( selected );
            handleSnowfallCheckBox();
        }
    }
    
    public boolean isSnowfallSelected() {
        return _snowfallCheckBox.isSelected();
    }
    
    public void setCoordinatesCheckBoxVisible( boolean visible ) {
        _coordinatesPanel.setVisible( visible );
    }
    
    public void setIceFlowCheckBoxVisible( boolean visible ) {
        _iceFlowPanel.setVisible( visible );
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    public void addUnitsChangedListener( UnitsChangeListener listener ) {
        _unitsChangeListeners.add( listener );
    }
    
    public void removeUnitsChangedListener( UnitsChangeListener listener ) {
        _unitsChangeListeners.remove( listener );
    }
    
    private void notifyUnitsChanged() {
        final boolean englishUnits = _englishUnitsButton.isSelected();
        Iterator i = _unitsChangeListeners.iterator();
        while ( i.hasNext() ) {
            ((UnitsChangeListener)i.next()).unitsChanged( englishUnits );
        }
    }
    
    private void handleUnitsButton() {
        notifyUnitsChanged();
    }
    
    private void handleEquilibriumLineCheckBox() {
        _playArea.setEquilibriumLineVisible( _equilibriumLineCheckBox.isSelected() );
    }
    
    private void handleIceFlowCheckBox() {
        _playArea.setIceFlowVisible( _iceFlowCheckBox.isSelected() );
    }
    
    private void handleCoordinatesCheckBox() {
        _playArea.setAxesVisible( _coordinatesCheckBox.isSelected() );
    }
    
    private void handleSnowfallCheckBox() {
        _playArea.setSnowfallVisible( _snowfallCheckBox.isSelected() );
    }
}
