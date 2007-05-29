/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LogarithmicValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.defaults.PhysicsDefaults;
import edu.colorado.phet.opticaltweezers.dialog.PhysicsDeveloperDialog;
import edu.colorado.phet.opticaltweezers.model.PhysicsModel;
import edu.colorado.phet.opticaltweezers.module.PhysicsModule;
import edu.colorado.phet.opticaltweezers.view.PhysicsCanvas;

/**
 * PhysicsControlPanel is the control panel for PhysicsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhysicsControlPanel extends AbstractControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhysicsModule _module;
    private PhysicsModel _model;
    private PhysicsCanvas _canvas;
    private PhysicsDeveloperDialog _developerControlsDialog;
    
    private JCheckBox _developerControlsCheckBox;
    
    private ClockStepControlPanel _clockStepControlPanel;
    
    private JCheckBox _electricFieldCheckBox;
    private JCheckBox _beadChargesCheckBox;
    private JRadioButton _allChargesRadioButton;
    private JRadioButton _excessChargesRadioButton;
   
    private ForcesControlPanel _forcesControlPanel;
    private ChartsControlPanel _chartsControlPanel;
    private AdvancedControlPanel _advancedControlPanel;

    private JCheckBox _rulerCheckBox;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param module
     */
    public PhysicsControlPanel( PhysicsModule module) {
        super( module );
        
        _module = module;
        _model = module.getPhysicsModel();
        _canvas = module.getPhysicsCanvas();

        // Set the control panel's minimum width.
        int minimumWidth = OTResources.getInt( "int.minControlPanelWidth", 215 );
        setMinumumWidth( minimumWidth );
        
        // Developer controls
        _developerControlsCheckBox = new JCheckBox( "Show Developer Controls" );
        
        // Clock speed
        _clockStepControlPanel = new ClockStepControlPanel( TITLE_FONT, CONTROL_FONT, _model.getClock() );
        
        // Fields and charges
        JPanel fieldAndChargesPanel = new JPanel();
        {
            JLabel titleLabel = new JLabel( OTResources.getString( "label.fieldsAndCharges" ) );
            titleLabel.setFont( TITLE_FONT );
            
            _electricFieldCheckBox = new JCheckBox( OTResources.getString( "label.showElectricField" ) );
            _beadChargesCheckBox = new JCheckBox( OTResources.getString( "label.showBeadCharges" ) );
            _allChargesRadioButton = new JRadioButton( OTResources.getString("label.allCharges" ) );
            _excessChargesRadioButton = new JRadioButton( OTResources.getString( "label.excessCharges" ) );
            ButtonGroup bg = new ButtonGroup();
            bg.add( _allChargesRadioButton );
            bg.add( _excessChargesRadioButton );
            
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setFill( GridBagConstraints.HORIZONTAL );
            layout.setMinimumWidth( 0, 20 );
            int row = 0;
            layout.addComponent( titleLabel, row++, 0, 2, 1 );
            layout.addComponent( _electricFieldCheckBox, row++, 0, 2, 1 );
            layout.addComponent( _beadChargesCheckBox, row++, 0, 2, 1 );
            layout.addComponent( _allChargesRadioButton, row++, 1 );
            layout.addComponent( _excessChargesRadioButton, row++, 1 );
            
            fieldAndChargesPanel.setLayout( new BorderLayout() );
            fieldAndChargesPanel.add( innerPanel, BorderLayout.WEST );
        }
        
        // Forces on bead 
        _forcesControlPanel = new ForcesControlPanel( TITLE_FONT, CONTROL_FONT, _canvas.getTrapForceNode(), _canvas.getDragForceNode(), _canvas.getBrownianForceNode() );
        
        // Charts
        _chartsControlPanel = new ChartsControlPanel( TITLE_FONT, CONTROL_FONT, _canvas.getPositionHistogramChartNode(), _canvas.getPotentialEnergyChartNode() );
        
        // Ruler
        _rulerCheckBox = new JCheckBox( OTResources.getString( "label.showRuler" ) );
        
        // Advanced features
        _advancedControlPanel = new AdvancedControlPanel( TITLE_FONT, CONTROL_FONT, _model.getFluid(), _module.getFrame() );
        
        // Fonts
        {
            _developerControlsCheckBox.setFont( CONTROL_FONT );
            
            _electricFieldCheckBox.setFont( CONTROL_FONT );
            _beadChargesCheckBox.setFont( CONTROL_FONT );
            _allChargesRadioButton.setFont( CONTROL_FONT );
            _excessChargesRadioButton.setFont( CONTROL_FONT );
            
            _rulerCheckBox.setFont( CONTROL_FONT );
        }
        
        // Layout
        {
            if ( System.getProperty( OTConstants.PROPERTY_PHET_DEVELOPER ) != null ) {
                addControlFullWidth( _developerControlsCheckBox );
                addSeparator();
            }
            addControlFullWidth( _clockStepControlPanel );
            addSeparator();
            addControlFullWidth( fieldAndChargesPanel );
            addSeparator();
            addControlFullWidth( _forcesControlPanel );
            addSeparator();
            addControlFullWidth( _chartsControlPanel );
            addSeparator();
            addControlFullWidth( _rulerCheckBox );
            addSeparator();
            addControlFullWidth( _advancedControlPanel );
            addSeparator();
            addResetButton();
        }
        
        // Listeners
        {
            EventListener listener = new EventListener();
            
            _developerControlsCheckBox.addActionListener( listener );
            
            _electricFieldCheckBox.addActionListener( listener );
            _beadChargesCheckBox.addActionListener( listener );
            _allChargesRadioButton.addActionListener( listener );
            _excessChargesRadioButton.addActionListener( listener );
            
            _rulerCheckBox.addActionListener( listener );
        }
        
        // Default state
        {
            _electricFieldCheckBox.setSelected( false );
            _beadChargesCheckBox.setSelected( false );
            _allChargesRadioButton.setSelected( true );
            _allChargesRadioButton.setEnabled( _beadChargesCheckBox.isSelected() );
            _excessChargesRadioButton.setEnabled( false );
            _excessChargesRadioButton.setEnabled( _beadChargesCheckBox.isSelected() );
            
            _rulerCheckBox.setSelected( false  );
            
            //XXX enable & disable controls based on clock speed
        }
        
        //XXX use red foreground for controls that aren't implemented
        {
            Color fg = Color.RED;
            _electricFieldCheckBox.setForeground( fg );
            _beadChargesCheckBox.setForeground( fg );
            _allChargesRadioButton.setForeground( fg );
            _excessChargesRadioButton.setForeground( fg );
        }
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    public ClockStepControlPanel getClockStepControlPanel() {
        return _clockStepControlPanel;
    }
    
    public ForcesControlPanel getForcesControlPanel() {
        return _forcesControlPanel;
    }
    
    public ChartsControlPanel getChartsControlPanel() {
        return _chartsControlPanel;
    }
    
    public AdvancedControlPanel getAdvancedControlPanel() {
        return _advancedControlPanel;
    }
    
    public void closeAllDialogs() {
        _advancedControlPanel.setFluidControlSelected( false );
        setDeveloperControlsSelected( false );
    }
    
    public void setDeveloperControlsSelected( boolean b ) {
        _developerControlsCheckBox.setSelected( b );
        handleDeveloperControlsCheckBox();
    }
    
    public void setElectricFieldSelected( boolean b ) {
        _electricFieldCheckBox.setSelected( b );
        handleElectricFieldCheckBox();
    }
    
    public boolean isElectricFieldSelected() {
        return _electricFieldCheckBox.isSelected();
    }
   
    public void setBeadChargesSelected( boolean b ) {
        _beadChargesCheckBox.setSelected( b );
        handleBeadChargesCheckBox();
    }
    
    public boolean isBeadChargesSelected() {
        return _beadChargesCheckBox.isSelected();
    }
    
    public void setAllChargesSelected( boolean b ) {
        _allChargesRadioButton.setSelected( b );
        handleAllChargesRadioButton();
    }
    
    public boolean isAllChargesSelected() {
        return _allChargesRadioButton.isSelected();
    }
    
    public void setExcessChargesSelected( boolean b ) {
        _excessChargesRadioButton.setSelected( b );
        handleExcessChargesRadioButton();
    }
    
    public boolean isExcessChargesSelected() {
        return _excessChargesRadioButton.isSelected();
    }
    
    public void setRulerSelected( boolean b ) {
        _rulerCheckBox.setSelected( b );
        handleRulerCheckBox();
    }
    
    public boolean isRulerSelected() {
        return _rulerCheckBox.isSelected();
    }
    
    //----------------------------------------------------------------------------
    // Event dispatching
    //----------------------------------------------------------------------------
    
    private class EventListener implements ActionListener {

        public void actionPerformed( ActionEvent e ) {
            Object source = e.getSource();
            if ( source == _electricFieldCheckBox ) {
                handleElectricFieldCheckBox();
            }
            else if ( source == _beadChargesCheckBox ) {
                handleBeadChargesCheckBox();
            }
            else if ( source == _allChargesRadioButton ) {
                handleAllChargesRadioButton();
            }
            else if ( source == _excessChargesRadioButton ) {
                handleExcessChargesRadioButton();
            }
            else if ( source == _rulerCheckBox ) {
                handleRulerCheckBox();
            }
            else if ( source == _developerControlsCheckBox ) {
                handleDeveloperControlsCheckBox();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleElectricFieldCheckBox() {
        final boolean selected = _electricFieldCheckBox.isSelected();
        //XXX
    }
    
    private void handleBeadChargesCheckBox() {
        final boolean selected = _beadChargesCheckBox.isSelected();
        _allChargesRadioButton.setEnabled( selected );
        _excessChargesRadioButton.setEnabled( selected );
    }
    
    private void handleAllChargesRadioButton() {
        final boolean selected = _allChargesRadioButton.isSelected();
        //XXX
    }
    
    private void handleExcessChargesRadioButton() {
        final boolean selected = _excessChargesRadioButton.isSelected();
        //XXX
    }
    
    private void handleRulerCheckBox() {
        final boolean selected = _rulerCheckBox.isSelected();
        _canvas.getRulerNode().setVisible( selected );
    }
    
    private void handleDeveloperControlsCheckBox() {
        
        final boolean selected = _developerControlsCheckBox.isSelected();
        
        if ( !selected ) {
            if ( _developerControlsDialog != null ) {
                _developerControlsDialog.dispose();
                _developerControlsDialog = null;
            }
        }
        else {
            JFrame parentFrame = _module.getFrame();
            _developerControlsDialog = new PhysicsDeveloperDialog( parentFrame, _module );
            _developerControlsDialog.addWindowListener( new WindowAdapter() {

                // called when the close button in the dialog's window dressing is clicked
                public void windowClosing( WindowEvent e ) {
                    _developerControlsDialog.dispose();
                }

                // called by JDialog.dispose
                public void windowClosed( WindowEvent e ) {
                    _developerControlsDialog = null;
                    _developerControlsCheckBox.setSelected( false );
                }
            } );
            _developerControlsDialog.show();
        }
    }
}
