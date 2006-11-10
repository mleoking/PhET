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
import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.util.DialogCheckBox;
import edu.colorado.phet.molecularreactions.view.*;
import edu.colorado.phet.molecularreactions.view.charts.MoleculePopulationsBarChartNode;
import edu.colorado.phet.molecularreactions.view.charts.MoleculePopulationsStripChart;
import edu.colorado.phet.molecularreactions.view.charts.MoleculePopulationsPieChartNode;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolo.PNode;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * ComplexModule
 * <p>
 * This module has controls for putting lots of molecules in the box and
 * charting their properties is different ways
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ComplexModule extends MRModule {
    private PSwing stripChartNode;
    private JDialog stripChartDlg;
    private ComplexMRControlPanel controlPanel;
    private PumpGraphic pumpGraphic;
    private PNode barChartNode;
    private MoleculePopulationsPieChartNode pieChart;

    /**
     *
     */
    public ComplexModule() {
        super( SimStrings.get( "Module.complexModuleTitle" ));

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

    public void setStripChartVisible( boolean visible, DialogCheckBox showStripChartBtn ) {
        if( visible ) {
            final MoleculePopulationsStripChart stripChart = new MoleculePopulationsStripChart( getMRModel(), getClock(), 500, 0, 20, 1 );
            ChartPanel chartPanel = new ChartPanel( stripChart.getChart() );
            chartPanel.setPreferredSize( new java.awt.Dimension( 400, 200 ) );
            stripChartNode = new PSwing( (PhetPCanvas)getSimulationPanel(), chartPanel );

            // Dialog
            stripChartDlg = new JDialog( PhetUtilities.getPhetFrame(), false );
            stripChartDlg.setResizable( false );
            PhetPCanvas stripChartCanvas = new PhetPCanvas();
            stripChartCanvas.addScreenChild( stripChartNode );
            stripChartCanvas.setPreferredSize( new Dimension( chartPanel.getPreferredSize() ) );

            // Add a rescale button
            JButton rescaleBtn = new JButton( SimStrings.get( "StripChart.rescale"));
            rescaleBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    stripChart.rescale();
                }
            } );
            PSwing rescaleNode = new PSwing( stripChartCanvas, rescaleBtn );
            rescaleNode.setOffset( chartPanel.getPreferredSize().getWidth() - rescaleNode.getFullBounds().getWidth() - 10,
                                   chartPanel.getPreferredSize().getHeight() - rescaleNode.getFullBounds().getHeight() - 10);
            stripChartCanvas.addScreenChild( rescaleNode );

            stripChartDlg.getContentPane().add( stripChartCanvas );
            stripChartDlg.pack();
            stripChartDlg.setVisible( true );

            if( showStripChartBtn != null ) {
                stripChartDlg.addComponentListener( showStripChartBtn );
            }
        }
        else if( stripChartNode != null ) {
            stripChartNode = null;
            stripChartDlg.setVisible( false );
        }
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

