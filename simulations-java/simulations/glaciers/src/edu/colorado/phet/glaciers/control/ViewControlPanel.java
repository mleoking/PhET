/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.dialog.GlacierPictureDialog;
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
    private final Frame _dialogOwner;
    
    private final JRadioButton _englishUnitsButton, _metricUnitsButton;
    private final JCheckBox _equilibriumLineCheckBox;
    private final JCheckBox _iceFlowCheckBox;
    private final JCheckBox _coordinatesCheckBox;
    private final JCheckBox _snowfallCheckBox;
    private final JCheckBox _glacierPictureCheckBox;
    private JDialog _glacierPictureDialog;
    
    private boolean _glacierPictureDialogWasOpen;
    private Point _glacierPictureDialogLocation;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ViewControlPanel( GlaciersPlayArea playArea, Frame dialogOwner ) {
        super( TITLE_STRING, TITLE_COLOR, TITLE_FONT );
        
        _playArea = playArea;
        _dialogOwner = dialogOwner;
        
        _glacierPictureDialogWasOpen = false;
        
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
            _englishUnitsButton.addActionListener( new ActionListener() {
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
        
        JPanel iceFlowPanel = new JPanel();
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
            
            iceFlowPanel.add( _iceFlowCheckBox );
            iceFlowPanel.add( iceFlowIcon );
        }
        
        JPanel coordinatesPanel = new JPanel();
        {
            _coordinatesCheckBox = new JCheckBox( GlaciersStrings.CHECK_BOX_COORDINATES );
            _coordinatesCheckBox.setFont( CONTROL_FONT );
            _coordinatesCheckBox.setForeground( CONTROL_COLOR );
            _coordinatesCheckBox.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    handleCoordinatesCheckBox();
                }
            } );
            
            JLabel coordinatesIcon = new JLabel( ElevationAxisNode.createIcon() );
            
            coordinatesPanel.add( _coordinatesCheckBox );
            coordinatesPanel.add( coordinatesIcon );
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
        
        JPanel glacierPicturePanel = new JPanel();
        {
            _glacierPictureCheckBox = new JCheckBox( GlaciersStrings.CHECK_BOX_GLACIER_PICTURE );
            _glacierPictureCheckBox.setFont( CONTROL_FONT );
            _glacierPictureCheckBox.setForeground( CONTROL_COLOR );
            _glacierPictureCheckBox.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    handleGlacierPictureCheckBox();
                }
            } );
            
            glacierPicturePanel.add( _glacierPictureCheckBox );
        }
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        layout.setInsets( new Insets( 0, 0, 0, 0 ) );
        layout.setAnchor( GridBagConstraints.WEST );
//        layout.addComponent( unitsPanel, 0, 0, 2, 1 ); //XXX hide for 7/25/08 deadline
        layout.addComponent( equilibriumLinePanel, 1, 0 );
        layout.addComponent( snowfallPanel, 2, 0 );
        layout.addComponent( glacierPicturePanel, 3, 0 );
        layout.addComponent( iceFlowPanel, 2, 1 );
        layout.addComponent( coordinatesPanel, 3, 1 );
        
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
    
    public void setGlacierPictureSelected( boolean selected ) {
        if ( selected != isGlacierPictureSelected() ) {
            _glacierPictureCheckBox.setSelected( selected );
            handleGlacierPictureCheckBox();
        }
    }
    
    public boolean isGlacierPictureSelected() {
        return _glacierPictureCheckBox.isSelected();
    }
    
    public void setCoordinatesCheckBoxVisible( boolean visible ) {
        _coordinatesCheckBox.setVisible( visible );
    }
    
    public void setIceFlowCheckBoxVisible( boolean visible ) {
        _iceFlowCheckBox.setVisible( visible );
    }
    
    public void activate() {
        setGlacierPictureSelected( _glacierPictureDialogWasOpen );
    }
    
    public void deactivate() {
        _glacierPictureDialogWasOpen = isGlacierPictureSelected();
        setGlacierPictureSelected( false );
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleUnitsButton() {
        //XXX
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
    
    private void handleGlacierPictureCheckBox() {
        if ( _glacierPictureCheckBox.isSelected() ) {
            _glacierPictureDialog = new GlacierPictureDialog( _dialogOwner );
            if ( _glacierPictureDialogLocation != null ) {
                _glacierPictureDialog.setLocation( _glacierPictureDialogLocation );
            }
            else {
                SwingUtils.centerDialogInParent( _glacierPictureDialog );
            }
            _glacierPictureDialog.addWindowListener( new WindowAdapter() {
                // called when the close button in the dialog's window dressing is clicked
                public void windowClosing( WindowEvent e ) {
                    setGlacierPictureSelected( false );
                }
            } );
            _glacierPictureDialog.setVisible( true );
        }
        else {
            _glacierPictureDialogLocation = _glacierPictureDialog.getLocation();
            _glacierPictureDialog.dispose();
            _glacierPictureDialog = null;
        }
    }
}
