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
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.view.*;
import edu.colorado.phet.molecularreactions.view.charts.*;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
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
    private PNode barChartNode;
    private MoleculePopulationsPieChartNode pieChart;


    /**
     *
     */
    public ComplexModule() {
        this( SimStrings.get( "Module.complexModuleTitle" ) );
    }


    protected ComplexModule( String title ) {
        super( title, MRConfig.CHART_PANE_SIZE );

        // Disable marking of the selected molecule and its nearest neighbor
        SimpleMoleculeGraphic.setMarkSelectedMolecule( true );

        // Add the pump
        MRModel model = getMRModel();
        pumpGraphic = new PumpGraphic( this );
        // 15 is the wall thickness of the box graphic
        pumpGraphic.setOffset( model.getBox().getMinX() + model.getBox().getWidth(),
                               model.getBox().getMinY() + model.getBox().getHeight() + 15 - pumpGraphic.getPumpBaseLocation().getY() );
        getSpatialView().addChild( pumpGraphic );

        setMRControlPanel( new ComplexMRControlPanel( this ) );

        // Don't show the total energy line on the energy view
        getEnergyView().setTotalEnergyLineVisible( false );
        getEnergyView().setProfileLegendVisible( false );
    }

    protected void setMRControlPanel( MRControlPanel controlPanel ) {
        if( this.controlPanel != null ) {
            getControlPanel().removeControl( this.controlPanel );
        }
        this.controlPanel = controlPanel;
        getControlPanel().addControl( controlPanel );
    }

    public void activate() {
        super.activate();
        // True marking of the selected molecule and its nearest neighbor
        SimpleMoleculeGraphic.setMarkSelectedMolecule( true );
    }

    public void reset() {
        super.reset();
        setInitialConditions();
        controlPanel.reset();
        pumpGraphic.reset();
//        setPieChartVisible( false );
//        setBarChartVisible( false );
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
     * Set visibility of the strip chart
     *
     * @param visible
     */
    public void setStripChartVisible( boolean visible ) {

        if( visible ) {
            if( stripChartDlg == null ) {
                stripChartDlg = new StripChartDialog( this );
            }
            stripChartDlg.setVisible( true );

//            PhetPCanvas ppc = new PhetPCanvas( stripChartDlg.getContentPane().getPreferredSize() );
//            ppc.addScreenChild( new PSwing( ppc, (JPanel)stripChartDlg.getContentPane() ));
//            getEnergyView().addToUpperPane( ppc.getPhetRootNode() );
            getEnergyView().addToUpperPane( new StripChartNode( this, MRConfig.CHART_PANE_SIZE ) );
        }
        else if( !visible && stripChartDlg != null ) {
            stripChartDlg.setVisible( false );
//            stripChartDlg = null;
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
        stripChartDlg.reset();        
    }
}

