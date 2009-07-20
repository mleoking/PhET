/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.modules;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.model.clock.VariableConstantTickClock;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.reactionsandrates.MRConfig;
import edu.colorado.phet.reactionsandrates.model.MRModel;
import edu.colorado.phet.reactionsandrates.view.SpatialView;
import edu.colorado.phet.reactionsandrates.view.energy.EnergyView;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.util.Timer;
import java.util.TimerTask;

/**
 * MRModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public abstract class MRModule extends Module {

    private final Dimension canvasSize = MRConfig.SIMULATION_PANEL_SIZE;
    private final SpatialView spatialView;
    private final EnergyView energyView = new EnergyView();
    private final Dimension spatialViewSize = MRConfig.SPATIAL_VIEW_SIZE;
    private final MRModel mrModel;

    public PhetPCanvas getCanvas() {
        return canvas;
    }

    private final PhetPCanvas canvas;
    private final Insets simulationPaneInsets;
    private final int chartPaneHeight;

    private volatile boolean resetInProgress;


    /**
     * @param name          The title of the module
     * @param chartPaneSize The size of the upper pane of the energy view
     */
    public MRModule( String name, Dimension chartPaneSize ) {
        super( name, new VariableConstantTickClock( new SwingClock( 1000 / MRConfig.CLOCK_FPS,
                                                                    MRConfig.RUNNING_DT ),
                                                    MRConfig.RUNNING_DT ) );

        initiateReset();

        chartPaneHeight = chartPaneSize.height;

        // Create the model
        mrModel = new MRModel( getClock() );
        setModel( mrModel );

        // create the control panel
        ControlPanel controlPanel = new ControlPanel();
        controlPanel.getContentPanel().setFillNone();
        setControlPanel( controlPanel );

        // Create the basic graphics
        canvas = new PhetPCanvas( canvasSize );
        setSimulationPanel( canvas );

        // Set up the sizes and locations of the views
        simulationPaneInsets = new Insets( 10, 10, 10, 10 );

        // Create spatial view
        spatialView = new SpatialView( this, spatialViewSize );
        spatialView.setOffset( simulationPaneInsets.left, simulationPaneInsets.top );
        canvas.addWorldChild( spatialView );

        // Add an agent that will make the clock's time step smaller when it's
        // being single stepped.
        getClock().addClockListener( new ClockAdapter() {
            public void clockStarted( ClockEvent clockEvent ) {
                ( (VariableConstantTickClock)getClock() ).setDt( MRConfig.RUNNING_DT );
            }

            public void clockPaused( ClockEvent clockEvent ) {
                ( (VariableConstantTickClock)getClock() ).setDt( MRConfig.STEPPING_DT );
            }
        } );

        // Create energy view
        createEnergyView( chartPaneSize );

        completeReset();
    }

    public boolean isTemperatureBeingAdjusted() {
        return spatialView.isTemperatureBeingAdjusted();
    }
    
    public boolean isResetInProgress() {
        return resetInProgress;
    }

    public PNode getGraphicsForModelElement( ModelElement element ) {
        return spatialView.getGraphicsForModelElement( element );
    }

    private void createEnergyView( Dimension chartPaneSize ) {
        energyView.initialize( this, chartPaneSize );

        energyView.setOffset( simulationPaneInsets.left + spatialView.getFullBounds().getWidth() + simulationPaneInsets.left,
                              simulationPaneInsets.top );

        canvas.addWorldChild( energyView );
    }

    // Added this to address #1729.
    public void clearExperiment() {
        getMRModel().removeAllMolecules();
    }
    
    public void reset() {
        initiateReset();

        clearExperiment();
        ( (MRModel)getModel() ).setInitialConditions();
        energyView.reset();
        getClock().start();

        completeReset();
    }

    protected SpatialView getSpatialView() {
        return spatialView;
    }

    public EnergyView getEnergyView() {
        return energyView;
    }

    protected PSwingCanvas getPCanvas() {
        return (PSwingCanvas)getSimulationPanel();
    }

    public MRModel getMRModel() {
        return (MRModel)getModel();
    }

    public void setGraphicTypeVisible( boolean visible ) {
        spatialView.setGraphicTypeVisible( visible );
    }

    /*
     * Determines the visible bounds of the canvas in world coordinates.
     */
    public static Dimension getWorldSize( PhetPCanvas canvas ) {
        Dimension2D dim = new PDimension( canvas.getWidth(), canvas.getHeight() );
        canvas.getPhetRootNode().screenToWorld( dim ); // this modifies dim!
        return new Dimension( (int)dim.getWidth(), (int)dim.getHeight() );
    }

    protected void initiateReset() {
        resetInProgress = true;
    }

    protected void completeReset() {
        Timer t = new Timer();

        t.schedule( new TimerTask() {
            public void run() {
                resetInProgress = false;
            }
        }, 1000 );
    }
}
