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
   
    private BeadChargeControlPanel _beadChargeControlPanel;
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
            
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setFill( GridBagConstraints.HORIZONTAL );
            layout.setMinimumWidth( 0, 20 );
            int row = 0;
            layout.addComponent( titleLabel, row++, 0, 2, 1 );
            layout.addComponent( _electricFieldCheckBox, row++, 0, 2, 1 );
            
            fieldAndChargesPanel.setLayout( new BorderLayout() );
            fieldAndChargesPanel.add( innerPanel, BorderLayout.WEST );
        }
        
        _beadChargeControlPanel = new BeadChargeControlPanel( TITLE_FONT, CONTROL_FONT );
        _forcesControlPanel = new ForcesControlPanel( TITLE_FONT, CONTROL_FONT, _canvas.getTrapForceNode(), _canvas.getDragForceNode(), _canvas.getBrownianForceNode() );
        _chartsControlPanel = new ChartsControlPanel( TITLE_FONT, CONTROL_FONT, _canvas.getPositionHistogramChartNode(), _canvas.getPotentialEnergyChartNode() );
        
        // Ruler
        _rulerCheckBox = new JCheckBox( OTResources.getString( "label.showRuler" ) );
        
        _advancedControlPanel = new AdvancedControlPanel( TITLE_FONT, CONTROL_FONT, _model.getFluid(), _module.getFrame() );
        
        // Fonts
        _developerControlsCheckBox.setFont( CONTROL_FONT );
        _rulerCheckBox.setFont( CONTROL_FONT );
        
        // Layout
        {
            if ( System.getProperty( OTConstants.PROPERTY_PHET_DEVELOPER ) != null ) {
                addControlFullWidth( _developerControlsCheckBox );
                addSeparator();
            }
            addControlFullWidth( _clockStepControlPanel );
            addSeparator();
            addControlFullWidth( _beadChargeControlPanel );
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
        EventListener listener = new EventListener();
        _developerControlsCheckBox.addActionListener( listener );
        _electricFieldCheckBox.addActionListener( listener );
        _rulerCheckBox.addActionListener( listener );
        
        // Default state
        _electricFieldCheckBox.setSelected( false );
        _rulerCheckBox.setSelected( false );
        //XXX enable & disable controls based on clock speed
        
        //XXX not implemented
        _electricFieldCheckBox.setForeground( Color.RED );
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    public ClockStepControlPanel getClockStepControlPanel() {
        return _clockStepControlPanel;
    }
    
    public BeadChargeControlPanel getBeadChargeControlPanel() {
        return _beadChargeControlPanel;
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
    
    public boolean isDeveloperControlsSelected() {
        return _developerControlsCheckBox.isSelected();
    }
    
    public void setElectricFieldSelected( boolean b ) {
        _electricFieldCheckBox.setSelected( b );
        handleElectricFieldCheckBox();
    }
    
    public boolean isElectricFieldSelected() {
        return _electricFieldCheckBox.isSelected();
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
