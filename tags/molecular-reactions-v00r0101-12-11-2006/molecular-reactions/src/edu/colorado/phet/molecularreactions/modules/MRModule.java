/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.modules;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.model.clock.VariableConstantTickClock;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.view.SpatialView;
import edu.colorado.phet.molecularreactions.view.energy.EnergyView;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Dimension2D;

/**
 * MRModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MRModule extends Module {

    private Dimension canvasSize = MRConfig.SIMULATION_PANEL_SIZE;
    private SpatialView spatialView;
    private EnergyView energyView;
    private Dimension spatialViewSize = MRConfig.SPATIAL_VIEW_SIZE;
    private PhetPCanvas canvas;
    private Insets simulationPaneInsets;
    private int chartPaneHeight;


    /**
     *
     * @param name          The title of the module
     * @param chartPaneSize The size of the upper pane of the energy view
     */
    public MRModule( String name, Dimension chartPaneSize ) {
        super( name, new VariableConstantTickClock( new SwingClock( 1000 / MRConfig.CLOCK_FPS, 
                                                                    MRConfig.RUNNING_DT ),
                                                    MRConfig.RUNNING_DT ) );
        chartPaneHeight = chartPaneSize.height;

        // Create the model
        MRModel model = new MRModel( getClock() );
        setModel( model );

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

        // Create energy view
        createEnergyView( chartPaneSize.width, chartPaneSize );

        // Create a listener that will adjust the layout when the frame changes size
        canvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateCanvasLayout();
            }
        } );
        updateCanvasLayout();

        // Add an agent that will make the clock's time step smaller when it's
        // being single stepped.
        getClock().addClockListener( new ClockAdapter() {
            public void clockStarted( ClockEvent clockEvent ) {
                ((VariableConstantTickClock)getClock()).setDt( MRConfig.RUNNING_DT );
            }

            public void clockPaused( ClockEvent clockEvent ) {
                ((VariableConstantTickClock)getClock()).setDt( MRConfig.STEPPING_DT );
            }
        });
    }

    private void createEnergyView( int width, Dimension chartPaneSize ) {
        if( energyView != null ) {
            canvas.removeWorldChild( energyView );
        }
        energyView = new EnergyView( this, width, chartPaneSize );
        energyView.setOffset( simulationPaneInsets.left + spatialView.getFullBounds().getWidth() + simulationPaneInsets.left,
                              simulationPaneInsets.top );
        canvas.addWorldChild( energyView );
    }

    public void reset() {
        getModel().removeAllModelElements();
        ( (MRModel)getModel() ).setInitialConditions();
        energyView.reset();
        getClock().start();
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

    public void setManualControl( boolean manualControl ) {
        getClock().pause();
        energyView.setManualControl( true );
    }

    public void setGraphicTypeVisible( boolean visible ) {
        spatialView.setGraphicTypeVisible( visible );
    }


    /**
     * Updates the canvas layout.
     */
    protected void updateCanvasLayout() {

        Dimension worldSize = getWorldSize( canvas );
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, skip layout
            return;
        }

        // Layout nodes on the canvas ...
        double energyViewWidth = worldSize.getWidth() - spatialView.getFullBounds().getWidth() - simulationPaneInsets.right * 2 - simulationPaneInsets.left;
        createEnergyView( (int)energyViewWidth, new Dimension( (int)energyViewWidth, chartPaneHeight ) );
    }

    /**
     * Determines the visible bounds of the canvas in world coordinates.
     */
    public static Dimension getWorldSize( PhetPCanvas canvas ) {
        Dimension2D dim = new PDimension( canvas.getWidth(), canvas.getHeight() );
        canvas.getPhetRootNode().screenToWorld( dim ); // this modifies dim!
        Dimension worldSize = new Dimension( (int) dim.getWidth(), (int) dim.getHeight() );
        return worldSize;
    }}
