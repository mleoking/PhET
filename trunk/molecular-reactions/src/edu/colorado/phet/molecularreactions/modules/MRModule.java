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
import edu.colorado.phet.common.model.clock.*;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.view.SpatialView;
import edu.colorado.phet.molecularreactions.view.energy.EnergyView;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import java.awt.*;

/**
 * MRModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MRModule extends Module {

    private Dimension size = new Dimension( 850, 575 );
    private ControlPanel mrControlPanel;
    private SpatialView spatialView;
    private EnergyView energyView;
    private Dimension spatialViewSize = new Dimension( 520, 500 );

    public MRModule( String name ) {
        super( name, new VariableConstantTickClock( new SwingClock( 40, MRConfig.RUNNING_DT ), MRConfig.RUNNING_DT ) );

        // Create the model
        MRModel model = new MRModel( getClock() );
        setModel( model );

        // create the control panel
        setControlPanel( new ControlPanel() );

        // Create the basic graphics
        PhetPCanvas canvas = new PhetPCanvas( size );
        setSimulationPanel( canvas );

        // Set up the sizes and locations of the views
        Insets insets = new Insets( 10, 10, 10, 10 );

        // Create spatial view
        spatialView = new SpatialView( this, spatialViewSize );
        spatialView.setOffset( insets.left, insets.top );
        canvas.addWorldChild( spatialView );

        // Create energy view
        energyView = new EnergyView( this );
        energyView.setOffset( insets.left + spatialView.getFullBounds().getWidth() + insets.left,
                              insets.top );
        canvas.addWorldChild( energyView );

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
    
    public void reset() {
        getModel().removeAllModelElements();
        ( (MRModel)getModel() ).setInitialConditions();
        energyView.reset();
    }
    
    protected JPanel getMRControlPanel() {
        return mrControlPanel;
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
}
