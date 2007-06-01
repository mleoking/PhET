/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
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
    
    private PhysicsCanvas _canvas;
    
    private ClockStepControlPanel _clockStepControlPanel;
    private LaserDisplayControlPanel _laserDisplayControlPanel;
    private BeadChargeControlPanel _beadChargeControlPanel;
    private ForcesControlPanel _forcesControlPanel;
    private ChartsControlPanel _chartsControlPanel;
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
    public PhysicsControlPanel( PhysicsModule module) {
        super( module );
        
        _canvas = module.getPhysicsCanvas();

        // Set the control panel's minimum width.
        int minimumWidth = OTResources.getInt( "int.minControlPanelWidth", 215 );
        setMinumumWidth( minimumWidth );
        
        // Sub-panels
        PhysicsModel model = module.getPhysicsModel();
        _clockStepControlPanel = new ClockStepControlPanel( TITLE_FONT, CONTROL_FONT, model.getClock() );
        _laserDisplayControlPanel = new LaserDisplayControlPanel( TITLE_FONT, CONTROL_FONT, _canvas.getLaserNode() );
        _beadChargeControlPanel = new BeadChargeControlPanel( TITLE_FONT, CONTROL_FONT );
        _forcesControlPanel = new ForcesControlPanel( TITLE_FONT, CONTROL_FONT,
                _canvas.getTrapForceNode(), _canvas.getDragForceNode(), _canvas.getBrownianForceNode(), null /* dnaForceNode */ );
        _chartsControlPanel = new ChartsControlPanel( TITLE_FONT, CONTROL_FONT, _canvas.getPositionHistogramChartNode(), _canvas.getPotentialEnergyChartNode() );
        _advancedControlPanel = new AdvancedControlPanel( TITLE_FONT, CONTROL_FONT, module.getFrame(), model.getFluid() );
        _developerControlPanel = new DeveloperControlPanel( TITLE_FONT, CONTROL_FONT, module.getFrame(), model.getBead(), null /* dnaStrand */ );
        
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
            addControlFullWidth( _laserDisplayControlPanel );
            addSeparator();
            addControlFullWidth( _beadChargeControlPanel );
            addSeparator();
            addControlFullWidth( _forcesControlPanel );
            addSeparator();
            addControlFullWidth( _chartsControlPanel );
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
    
    public LaserDisplayControlPanel getLaserDisplayControlPanel() {
        return _laserDisplayControlPanel;
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
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleRulerCheckBox() {
        final boolean selected = _rulerCheckBox.isSelected();
        _canvas.getRulerNode().setVisible( selected );
    }
}
