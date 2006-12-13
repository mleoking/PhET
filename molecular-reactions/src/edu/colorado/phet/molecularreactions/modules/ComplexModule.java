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

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.model.PublishingModel;
import edu.colorado.phet.molecularreactions.model.AbstractMolecule;
import edu.colorado.phet.molecularreactions.view.PumpGraphic;
import edu.colorado.phet.molecularreactions.view.SimpleMoleculeGraphic;
import edu.colorado.phet.molecularreactions.view.charts.MoleculePopulationsBarChartNode;
import edu.colorado.phet.molecularreactions.view.charts.MoleculePopulationsPieChartNode;
import edu.colorado.phet.molecularreactions.view.charts.StripChartDialog;
import edu.colorado.phet.molecularreactions.view.charts.StripChartNode;
import edu.colorado.phet.piccolo.PhetPCanvas;

import java.awt.event.ComponentListener;
import java.awt.geom.Rectangle2D;

/**
 * ComplexModule
 * <p/>
 * This module has controls for putting lots of molecules in the box and
 * charting their properties is different ways.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ComplexModule extends MRModule {
    private StripChartDialog stripChartDlg;
    private MRControlPanel controlPanel;
    private PumpGraphic pumpGraphic;
    private MoleculePopulationsBarChartNode barChartNode;
    private MoleculePopulationsPieChartNode pieChart;
    public StripChartNode stripChartNode;
    private boolean firstTimeStripChartVisible = true;


    /**
     *
     */
    public ComplexModule() {
        this( SimStrings.get( "Module.complexModuleTitle" ) );
    }

    /**
     *
     * @param title
     */
    protected ComplexModule( String title ) {
        super( title, MRConfig.CHART_PANE_SIZE );

        // Create the strip chart
        stripChartNode = new StripChartNode( this, MRConfig.CHART_PANE_SIZE );
//        setStripChartRecording( true );

        // Disable marking of the selected molecule and its nearest neighbor
        SimpleMoleculeGraphic.setMarkSelectedMolecule( true );

        // Add the pump
        MRModel model = getMRModel();
        pumpGraphic = new PumpGraphic( this );
        // 15 is the wall thickness of the box graphic
        pumpGraphic.setOffset( model.getBox().getMinX() + model.getBox().getWidth(),
                               model.getBox().getMinY() + model.getBox().getHeight() + 15 - pumpGraphic.getPumpBaseLocation().getY() );
        getSpatialView().addChild( pumpGraphic );

        // Create the control panel
        controlPanel = createControlPanel();
        getControlPanel().addControl( controlPanel );

        // Don't show the total energy line on the energy view
        getEnergyView().setTotalEnergyLineVisible( false );

        // Create an agent that will start the strip chart when the first molecule is added to the model
        getMRModel().addListener( new PublishingModel.ModelListenerAdapter(){

            public void modelElementAdded( ModelElement element ) {
                if( element instanceof AbstractMolecule ) {
                    setStripChartRecording( true );
                }
            }
        });
    }

    protected MRControlPanel createControlPanel() {
        return new ComplexMRControlPanel( this );
    }

    public void activate() {
        super.activate();
        // True marking of the selected molecule and its nearest neighbor
//        SimpleMoleculeGraphic.setMarkSelectedMolecule( true );
    }

    public void reset() {
        super.reset();
        setInitialConditions();
        controlPanel.reset();
        pumpGraphic.reset();
        resetStripChart();
//        setStripChartRecording( true );
    }

    private void setInitialConditions() {
        setStripChartVisible( false, null );
    }

    public void setPieChartVisible( boolean visible ) {
        if( visible ) {
            Rectangle2D.Double bounds = new Rectangle2D.Double( 0, 0,
                                                                getEnergyView().getUpperPaneSize().getWidth(),
                                                                getEnergyView().getUpperPaneSize().getHeight() );
            pieChart = new MoleculePopulationsPieChartNode( this, bounds );
            getEnergyView().addToUpperPane( pieChart );
        }
        else if( pieChart != null ) {
            getEnergyView().removeFromUpperPane( pieChart );
        }
    }

    public void setBarChartVisible( boolean visible ) {
        if( visible ) {
            barChartNode = new MoleculePopulationsBarChartNode( this,
                                                                getEnergyView().getUpperPaneSize(),
                                                                (PhetPCanvas)getSimulationPanel() );
            barChartNode.rescale();
            getEnergyView().addToUpperPane( barChartNode );
        }
        else if( barChartNode != null ) {
            getEnergyView().removeFromUpperPane( barChartNode );
            barChartNode = null;
        }
    }

    public void rescaleStripChart() {
        if( stripChartDlg != null ) {
            stripChartDlg.rescaleChart();
        }
    }

    /**
     * Sets the flag that determines whether the strip chart will be rescaled the next time
     * it's made visible.
     *
     * @param firstTimeStripChartVisible
     */
    protected void setFirstTimeStripChartVisible( boolean firstTimeStripChartVisible ) {
        this.firstTimeStripChartVisible = firstTimeStripChartVisible;
    }

    /**
     * Set visibility of the strip chart
     *
     * @param visible
     */
    public void setStripChartVisible( boolean visible ) {
        if( visible ) {
            if( firstTimeStripChartVisible ) {
                stripChartNode.rescale();
                firstTimeStripChartVisible = false;
            }
            getEnergyView().addToUpperPane( stripChartNode );
        }
        else if( stripChartNode != null ) {
            getEnergyView().removeFromUpperPane( stripChartNode );
//            stripChartNode = null;
        }
    }

    /**
     * Variant that hooks a ComponentListener to the dialog, so it will know if the
     * user has closed the dialog by clicking on the X in its frame.
     *
     * @param visible
     * @param showStripChartBtn
     */
    public void setStripChartVisible( boolean visible, ComponentListener showStripChartBtn ) {
        setStripChartVisible( visible );
        if( visible && showStripChartBtn != null ) {
            stripChartDlg.addComponentListener( showStripChartBtn );
        }
    }

    public void resetStripChart() {
        stripChartNode.reset();
    }

    public void setStripChartRecording( boolean recording ) {
            stripChartNode.setRecording( recording);
    }

    protected void updateCanvasLayout() {
        super.updateCanvasLayout();

        // Don't show the total energy line on the energy view
        getEnergyView().setTotalEnergyLineVisible( false );
        getEnergyView().setProfileLegendVisible( false );
    }
}

