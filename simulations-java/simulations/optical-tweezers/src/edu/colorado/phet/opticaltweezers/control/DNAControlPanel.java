/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBox;
import javax.swing.JFrame;

import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.dialog.PhysicsDeveloperDialog;
import edu.colorado.phet.opticaltweezers.model.DNAModel;
import edu.colorado.phet.opticaltweezers.module.DNAModule;
import edu.colorado.phet.opticaltweezers.view.DNACanvas;

/**
 * DNAControlPanel is the control panel for DNAModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DNAControlPanel extends AbstractControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DNAModule _module;
    private DNAModel _model;
    private DNACanvas _canvas;
    
    private ClockStepControlPanel _clockStepControlPanel;
    private ForcesControlPanel _forcesControlPanel;
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
    public DNAControlPanel( DNAModule module) {
        super( module );
        
        _module = module;
        _model = module.getPhysicsModel();
        _canvas = module.getPhysicsCanvas();

        // Set the control panel's minimum width.
        int minimumWidth = OTResources.getInt( "int.minControlPanelWidth", 215 );
        setMinumumWidth( minimumWidth );
        
        // Sub-panels
        _clockStepControlPanel = new ClockStepControlPanel( TITLE_FONT, CONTROL_FONT, _model.getClock() );
        _forcesControlPanel = new ForcesControlPanel( TITLE_FONT, CONTROL_FONT, 
                _canvas.getTrapForceNode(), _canvas.getDragForceNode(), _canvas.getBrownianForceNode(), _canvas.getDNAForceNode() );
        _advancedControlPanel = new AdvancedControlPanel( TITLE_FONT, CONTROL_FONT, _model.getFluid(), _module.getFrame() );
        
        _rulerCheckBox = new JCheckBox( OTResources.getString( "label.showRuler" ) );
        _rulerCheckBox.setFont( CONTROL_FONT );
        _rulerCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleRulerCheckBox();
            }
        });
        
        // Layout
        {
            addControlFullWidth( _clockStepControlPanel );
            addSeparator();
            addControlFullWidth( _forcesControlPanel );
            addSeparator();
            addControlFullWidth( _rulerCheckBox );
            addSeparator();
            addControlFullWidth( _advancedControlPanel );
            addSeparator();
            addResetButton();
        }
        
        // Default state
        _rulerCheckBox.setSelected( false );
        //XXX enable & disable controls based on clock speed
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
    
    public AdvancedControlPanel getAdvancedControlPanel() {
        return _advancedControlPanel;
    }
    
    public void setRulerSelected( boolean b ) {
        _rulerCheckBox.setSelected( b );
        handleRulerCheckBox();
    }
    
    public boolean isRulerSelected() {
        return _rulerCheckBox.isSelected();
    }
    
    public void closeAllDialogs() {
        _advancedControlPanel.setFluidControlSelected( false );
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleRulerCheckBox() {
        final boolean selected = _rulerCheckBox.isSelected();
        _canvas.getRulerNode().setVisible( selected );
    }
}
