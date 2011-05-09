// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.module.physics;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.opticaltweezers.control.*;
import edu.colorado.phet.opticaltweezers.control.developer.DeveloperControlPanel;
import edu.colorado.phet.opticaltweezers.defaults.PhysicsDefaults;
import edu.colorado.phet.opticaltweezers.model.OTClock;
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
    public PhysicsControlPanel( PhysicsModule module, Frame parentFrame ) {
        super( module );
        
        _canvas = module.getPhysicsCanvas();
        
        // Sub-panels
        PhysicsModel model = module.getPhysicsModel();
        _simulationSpeedControlPanel = new SimulationSpeedControlPanel( TITLE_FONT, CONTROL_FONT, model.getClock() );
        _laserDisplayControlPanel = new LaserDisplayControlPanel( TITLE_FONT, CONTROL_FONT, _canvas.getLaserNode() );
        _chargeControlPanel = new ChargeControlPanel( TITLE_FONT, CONTROL_FONT,
                _canvas.getChargeDistributionNode(), _canvas.getChargeExcessNode() );
        _forcesControlPanel = new ForcesControlPanel( TITLE_FONT, CONTROL_FONT, 
                model.getBead(), model.getFluid(), model.getLaser(), null /* laserPositionController */,
                _canvas.getTrapForceNode(), _canvas.getFluidDragForceNode(), null /* dnaForceNode */,
                _canvas.getBeadNode(), _canvas.getLaserNode() );
        _chartsControlPanel = new ChartsControlPanel( TITLE_FONT, CONTROL_FONT, 
                parentFrame, PhysicsDefaults.POSITION_HISTOGRAM_DIALOG_OFFSET,
                model.getClock(), model.getBead(), model.getLaser(),
                _canvas.getPotentialEnergyChartNode(), _canvas.getLaserNode() );
        _miscControlPanel = new MiscControlPanel( TITLE_FONT, CONTROL_FONT, 
                parentFrame, PhysicsDefaults.FLUID_CONTROLS_DIALOG_OFFSET, 
                _canvas.getRulerNode(), model.getFluid() );
        _developerControlPanel = new DeveloperControlPanel( TITLE_FONT, CONTROL_FONT, parentFrame,
                (OTClock)module.getClock(), model.getBead(), null /* invisibleBead */, model.getLaser(),
                null /* dnaStrandBead */, null /* dnaStrandNodeBeadNode */, 
                null /* dnaStrandFree */, null /* dnaStrandFreeNode */,
                _canvas.getTrapForceNode(), _canvas.getFluidDragForceNode(), null /* dnaForceNode */,
                _canvas.getLaserNode().getElectricFieldNode(), _canvas.getChargeDistributionNode(),
                true /* showVacuumControls */ );
        
        // Turn off some features
        _forcesControlPanel.setShowValuesCheckBoxVisible( false );
        _forcesControlPanel.setKeepTrapForceConstantCheckBoxVisible( false );
        
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
            if ( PiccoloPhetApplication.getInstance().isDeveloperControlsEnabled() ) {
                addControlFullWidth( _developerControlPanel );
                addSeparator();
            }
            addResetAllButton( module );
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
