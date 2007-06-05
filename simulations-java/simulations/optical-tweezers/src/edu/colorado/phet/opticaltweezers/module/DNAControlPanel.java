/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.module;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.control.AdvancedControlPanel;
import edu.colorado.phet.opticaltweezers.control.ClockStepControlPanel;
import edu.colorado.phet.opticaltweezers.control.ForcesControlPanel;
import edu.colorado.phet.opticaltweezers.control.developer.DeveloperControlPanel;
import edu.colorado.phet.opticaltweezers.model.DNAModel;

/**
 * DNAControlPanel is the control panel for DNAModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DNAControlPanel extends AbstractControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DNACanvas _canvas;
    
    private ClockStepControlPanel _clockStepControlPanel;
    private ForcesControlPanel _forcesControlPanel;
    private AdvancedControlPanel _advancedControlPanel;
    private DeveloperControlPanel _developerControlPanel;
    
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

        _canvas = module.getDNACanvas();

        // Set the control panel's minimum width.
        int minimumWidth = OTResources.getInt( "int.minControlPanelWidth", 215 );
        setMinumumWidth( minimumWidth );
        
        // Sub-panels
        DNAModel model = module.getDNAModel();
        _clockStepControlPanel = new ClockStepControlPanel( TITLE_FONT, CONTROL_FONT, model.getClock() );
        _forcesControlPanel = new ForcesControlPanel( TITLE_FONT, CONTROL_FONT, 
                _canvas.getTrapForceNode(), _canvas.getDragForceNode(), _canvas.getBrownianForceNode(), _canvas.getDNAForceNode() );
        _advancedControlPanel = new AdvancedControlPanel( TITLE_FONT, CONTROL_FONT, module.getFrame(), model.getFluid() );
        _developerControlPanel = new DeveloperControlPanel( TITLE_FONT, CONTROL_FONT, module.getFrame(), model.getBead(), model.getDNAStrand(), _canvas.getDNAStrandNode() );
        
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
            if ( System.getProperty( OTConstants.PROPERTY_PHET_DEVELOPER ) != null ) {
                addControlFullWidth( _developerControlPanel );
                addSeparator();
            }
            addResetButton();
        }
        
        // Default state
        _rulerCheckBox.setSelected( false );
        //XXX enable & disable controls based on clock speed
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
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
    // Access to subpanels
    //----------------------------------------------------------------------------
    
    public DeveloperControlPanel getDeveloperControlPanel() {
        return _developerControlPanel;
    }
    
    public ClockStepControlPanel getClockStepControlPanel() {
        return _clockStepControlPanel;
    }
    
    public ForcesControlPanel getForcesControlPanel() {
        return _forcesControlPanel;
    }
    
    public AdvancedControlPanel getAdvancedControlPanel() {
        return _advancedControlPanel;
    }

    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleRulerCheckBox() {
        final boolean selected = _rulerCheckBox.isSelected();
        _canvas.getRulerNode().setVisible( selected );
    }
}
