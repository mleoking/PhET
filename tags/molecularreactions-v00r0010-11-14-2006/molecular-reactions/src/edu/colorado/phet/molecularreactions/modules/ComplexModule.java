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

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.view.*;
import edu.colorado.phet.molecularreactions.view.charts.MoleculePopulationsBarChartNode;
import edu.colorado.phet.molecularreactions.view.charts.MoleculePopulationsStripChart;
import edu.colorado.phet.molecularreactions.view.charts.MoleculePopulationsPieChartNode;
import edu.colorado.phet.molecularreactions.view.charts.StripChartAdjuster;
import edu.colorado.phet.molecularreactions.util.StripChart;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolo.PNode;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * ComplexModule
 * <p/>
 * This module has controls for putting lots of molecules in the box and
 * charting their properties is different ways, and for running experiments
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ComplexModule extends MRModule {
    private JDialog stripChartDlg;
    private ComplexMRControlPanel controlPanel;
    private PumpGraphic pumpGraphic;
    private PNode barChartNode;
    private MoleculePopulationsPieChartNode pieChart;

    /**
     *
     */
    public ComplexModule() {
        super( SimStrings.get( "Module.complexModuleTitle" ) );

        // Disable marking of the selected molecule and its nearest neighbor
        AbstractSimpleMoleculeGraphic.setMarkSelectedMolecule( true );

        // Add the pump
        MRModel model = getMRModel();
        pumpGraphic = new PumpGraphic( this );
        // 15 is the wall thickness of the box graphic
        pumpGraphic.setOffset( model.getBox().getMinX() + model.getBox().getWidth(),
                               model.getBox().getMinY() + model.getBox().getHeight() + 15 - pumpGraphic.getPumpBaseLocation().getY() );
        getSpatialView().addChild( pumpGraphic );

        controlPanel = new ComplexMRControlPanel( this );
        getControlPanel().addControl( controlPanel );

        // Don't show the total energy line on the energy view
        getEnergyView().setTotalEnergyLineVisible( false );
        getEnergyView().setProfileLegendVisible( false );
    }

    public void activate() {
        super.activate();
        // True marking of the selected molecule and its nearest neighbor
        AbstractSimpleMoleculeGraphic.setMarkSelectedMolecule( true );
    }

    public void reset() {
        super.reset();
        setInitialConditions();
        controlPanel.reset();
        pumpGraphic.reset();
    }

    private void setInitialConditions() {
        setStripChartVisible( false, null );
    }

    public void setCountersEditable( boolean editable ) {
        ComplexMRControlPanel controlPanel = (ComplexMRControlPanel)getMRControlPanel();
        controlPanel.getMoleculeInstanceControlPanel().setCountersEditable( editable );
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

    /**
     * Set visibility of the strip chart
     *
     * @param visible
     */
    public void setStripChartVisible( boolean visible ) {

        if( visible ) {
            if( stripChartDlg == null ) {
//                stripChartDlg = new StripChartDialog( this );
                stripChartDlg = createStripChartDialog();
            }
            stripChartDlg.setVisible( true );
        }
        else if( !visible && stripChartDlg != null ) {
            stripChartDlg.setVisible( false );
            stripChartDlg = null;
        }
    }

    /**
     * Variant that hooks a ComponentListener to the dialog, so it will knwo if the
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

    private JDialog createStripChartDialog() {
        Dimension chartSize = new Dimension( 400, 200 );
        final double xAxisRange = MRConfig.STRIP_CHART_VISIBLE_TIME_RANGE ;
        int numBufferedDataPoints = MRConfig.STRIP_CHART_BUFFER_SIZE;
        final MoleculePopulationsStripChart stripChart = new MoleculePopulationsStripChart( getMRModel(),
                                                                                            getClock(),
                                                                                            xAxisRange,
                                                                                            0,
                                                                                            20,
                                                                                            1,
                                                                                            numBufferedDataPoints );
        ChartPanel chartPanel = new ChartPanel( stripChart.getChart() );
        chartPanel.setPreferredSize( chartSize );

        // Dialog
        JDialog stripChartDlg = new JDialog( PhetUtilities.getPhetFrame(), false );
        stripChartDlg.setResizable( false );
        PhetPCanvas stripChartCanvas = new PhetPCanvas();
        PNode stripChartNode = new PSwing( stripChartCanvas, chartPanel );
        stripChartCanvas.addScreenChild( stripChartNode );

        // Add a rescale button
        JButton rescaleBtn = new JButton( SimStrings.get( "StripChart.rescale" ) );
        rescaleBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                stripChart.rescale();
            }
        } );
        PSwing rescaleNode = new PSwing( stripChartCanvas, rescaleBtn );
        rescaleNode.setOffset( chartPanel.getPreferredSize().getWidth() - rescaleNode.getFullBounds().getWidth() - 10,
                               chartPanel.getPreferredSize().getHeight() - rescaleNode.getFullBounds().getHeight() - 10 );
        stripChartCanvas.addScreenChild( rescaleNode );

        // Add a scrollbar
        final JScrollBar scrollBar = new JScrollBar( JScrollBar.HORIZONTAL, 0, (int)stripChart.getViewableRangeX(), 0, (int)xAxisRange );
        scrollBar.addAdjustmentListener( new StripChartAdjuster( stripChart ) );

        Insets scrollBarInsets = new Insets( 3, 50, 3, 10 );
        scrollBar.setPreferredSize( new Dimension( (int)( stripChartNode.getFullBounds().getWidth() - scrollBarInsets.left - scrollBarInsets.right ), 15 ) );
        PSwing scrollBarNode = new PSwing( stripChartCanvas, scrollBar );
        Dimension dialogSize = new Dimension( (int)chartSize.getWidth(),
                                              (int)( chartSize.getHeight() + scrollBarNode.getFullBounds().getHeight() +
                                                     scrollBarInsets.top + scrollBarInsets.bottom ) );

        scrollBarNode.setOffset( scrollBarInsets.left,
                                 dialogSize.getHeight() - scrollBarNode.getFullBounds().getHeight() - scrollBarInsets.bottom );
        stripChartCanvas.addScreenChild( scrollBarNode );
        stripChart.addListener( new StripChart.Listener() {
            public void dataChanged() {
                scrollBar.setMaximum( (int)stripChart.getMaxX() );
                scrollBar.setMinimum( (int)stripChart.getMinX() );
                scrollBar.setVisibleAmount( (int)stripChart.getViewableRangeX() );
                scrollBar.setValue( (int)stripChart.getMaxX());
            }
        } );

        // The scroll bar should only be enabled when the clock is paused
        getClock().addClockListener( new ClockAdapter() {
            public void clockStarted( ClockEvent clockEvent ) {
                scrollBar.setEnabled( false );
            }

            public void clockPaused( ClockEvent clockEvent ) {
                scrollBar.setEnabled( true );
            }
        });
        scrollBar.setEnabled( !getClock().isRunning() );

        stripChartCanvas.setPreferredSize( dialogSize );
        stripChartDlg.getContentPane().add( stripChartCanvas );
        stripChartDlg.pack();

        // Start the strip chart recording
        stripChart.startRecording( getClock().getSimulationTime() );

        return stripChartDlg;
    }

    /**
     * Cycles through the labels on the button, and sets the state
     * of the model.
     */
    private static class SetupGoAction extends AbstractAction {
        private int SETUP_MODE = 1, RUN_MODE = 2, STOP_MODE = 3;
        private int mode;
        JButton btn;
        String setupText = "<html><center>Set Initial<br>Conditions";
        String runText = "<html><center>Run<br>Experiment";
        String stopText = "<html><center>Stop<br>Experiment";
        private IClock clock;
        private MRModel model;
        private ComplexModule module;

        public SetupGoAction( JButton btn, ComplexModule module ) {
            this.module = module;
            this.clock = module.getClock();
            this.model = (MRModel)module.getModel();
            this.btn = btn;
            // Begin in STOP mode
            setMode( STOP_MODE );
        }

        /**
         * Sets the label on the button to indicate the mode the simulation
         * will go into when the button is clicked
         */
        private void setText() {
            if( mode == SETUP_MODE ) {
                btn.setText( runText );
            }
            else if( mode == RUN_MODE ) {
                btn.setText( stopText );
            }
            else if( mode == STOP_MODE ) {
                btn.setText( setupText );
            }
            else {
                throw new RuntimeException( "internal error" );
            }
        }

        public void actionPerformed( ActionEvent e ) {
            if( mode == SETUP_MODE ) {
                setMode( RUN_MODE );
            }
            else if( mode == RUN_MODE ) {
                setMode( STOP_MODE );
            }
            else if( mode == STOP_MODE ) {
                setMode( SETUP_MODE );
            }
            else {
                throw new RuntimeException( "internal error" );
            }
        }

        private void setMode( int mode ) {
            this.mode = mode;
            if( mode == SETUP_MODE ) {
                clock.pause();

                // Remove all the molecules from the model
                List modelElements = model.getModelElements();
                for( int i = modelElements.size() - 1; i >= 0; i-- ) {
                    Object o = modelElements.get( i );
                    if( o instanceof AbstractMolecule
                        || o instanceof ProvisionalBond ) {
                        ModelElement modelElement = (ModelElement)o;
                        model.removeModelElement( modelElement );
                    }
                }
                module.setCountersEditable( true );
            }
            else if( mode == RUN_MODE ) {
                clock.start();
                module.setCountersEditable( false );
            }
            else if( mode == STOP_MODE ) {
                clock.pause();
                module.setCountersEditable( false );
            }
            else {
                throw new RuntimeException( "internal error" );
            }
            setText();
        }
    }
}

