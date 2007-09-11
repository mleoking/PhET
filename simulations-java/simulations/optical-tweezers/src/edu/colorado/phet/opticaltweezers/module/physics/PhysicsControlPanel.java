/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.module.physics;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.OpticalTweezersApplication;
import edu.colorado.phet.opticaltweezers.control.*;
import edu.colorado.phet.opticaltweezers.control.developer.DeveloperControlPanel;
import edu.colorado.phet.opticaltweezers.defaults.PhysicsDefaults;
import edu.colorado.phet.opticaltweezers.model.OTClock;
import edu.colorado.phet.opticaltweezers.model.PhysicsModel;
import edu.colorado.phet.opticaltweezers.module.OTAbstractControlPanel;

/**
 * PhysicsControlPanel is the control panel for PhysicsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhysicsControlPanel extends OTAbstractControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhysicsCanvas _canvas;
    
    private SimulationSpeedControlPanel _simulationSpeedControlPanel;
    private LaserDisplayControlPanel _laserDisplayControlPanel;
    private ChargeControlPanel _chargeControlPanel;
    private ForcesControlPanel _forcesControlPanel;
    private ChartsControlPanel _chartsControlPanel;
    private MiscControlPanel _miscControlPanel;
    private DeveloperControlPanel _developerControlPanel;

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
        _simulationSpeedControlPanel = new SimulationSpeedControlPanel( TITLE_FONT, CONTROL_FONT, model.getClock() );
        _laserDisplayControlPanel = new LaserDisplayControlPanel( TITLE_FONT, CONTROL_FONT, _canvas.getLaserNode() );
        _chargeControlPanel = new ChargeControlPanel( TITLE_FONT, CONTROL_FONT,
                _canvas.getChargeDistributionNode(), _canvas.getChargeExcessNode() );
        _forcesControlPanel = new ForcesControlPanel( TITLE_FONT, CONTROL_FONT, 
                model.getBead(), model.getFluid(),
                _canvas.getTrapForceNode(), _canvas.getFluidDragForceNode(), null /* dnaForceNode */ );
        _chartsControlPanel = new ChartsControlPanel( TITLE_FONT, CONTROL_FONT, 
                module.getFrame(), PhysicsDefaults.POSITION_HISTOGRAM_DIALOG_OFFSET,
                model.getClock(), model.getBead(), model.getLaser(),
                _canvas.getPotentialEnergyChartNode(), _canvas.getLaserNode() );
        _miscControlPanel = new MiscControlPanel( TITLE_FONT, CONTROL_FONT, 
                module.getFrame(), PhysicsDefaults.FLUID_CONTROLS_DIALOG_OFFSET, 
                _canvas.getRulerNode(), model.getFluid() );
        List forceVectorNodes = new ArrayList();
        forceVectorNodes.add( _canvas.getTrapForceNode() );
        forceVectorNodes.add( _canvas.getFluidDragForceNode() );
        _developerControlPanel = new DeveloperControlPanel( TITLE_FONT, CONTROL_FONT, module.getFrame(),
                (OTClock)module.getClock(), model.getBead(), model.getLaser(),
                null /* dnaStrand */, null /* dnaStrandNode */, 
                _canvas.getTrapForceNode(), _canvas.getFluidDragForceNode(), null /* dnaForceNode */,
                _canvas.getLaserNode().getElectricFieldNode(), _canvas.getChargeDistributionNode() );
        
        // Turn off some features
        _forcesControlPanel.setShowValuesCheckBoxVisible( false );
        _forcesControlPanel.setConstantTrapForceCheckBoxVisible( false );
        
        // Layout
        {
            addControlFullWidth( _simulationSpeedControlPanel );
            addSeparator();
            addControlFullWidth( _laserDisplayControlPanel );
            addSeparator();
            addControlFullWidth( _chargeControlPanel );
            addSeparator();
            addControlFullWidth( _forcesControlPanel );
            addSeparator();
            addControlFullWidth( _chartsControlPanel );
            addSeparator();
            addControlFullWidth( _miscControlPanel );
            addSeparator();
            if ( OpticalTweezersApplication.isDeveloperControlsEnabled() ) {
                addControlFullWidth( _developerControlPanel );
                addSeparator();
            }
            addResetButton();
        }
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void closeAllDialogs() {
        _chartsControlPanel.setPositionHistogramSelected( false );
        _miscControlPanel.setFluidControlsSelected( false );
    }
    
    //----------------------------------------------------------------------------
    // Access to subpanels
    //----------------------------------------------------------------------------

    public DeveloperControlPanel getDeveloperControlPanel() {
        return _developerControlPanel;
    }
    
    public SimulationSpeedControlPanel getSimulationSpeedControlPanel() {
        return _simulationSpeedControlPanel;
    }
    
    public LaserDisplayControlPanel getLaserDisplayControlPanel() {
        return _laserDisplayControlPanel;
    }
    
    public ChargeControlPanel getChargeControlPanel() {
        return _chargeControlPanel;
    }
    
    public ForcesControlPanel getForcesControlPanel() {
        return _forcesControlPanel;
    }
    
    public ChartsControlPanel getChartsControlPanel() {
        return _chartsControlPanel;
    }
    
    public MiscControlPanel getMiscControlPanel() {
        return _miscControlPanel;
    }
}
