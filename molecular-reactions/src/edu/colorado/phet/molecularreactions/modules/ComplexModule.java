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

import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.view.PumpGraphic;
import edu.colorado.phet.molecularreactions.view.AbstractSimpleMoleculeGraphic;
import edu.colorado.phet.molecularreactions.view.MoleculePopulationsStripChart;
import edu.colorado.phet.molecularreactions.util.StripChart;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ChartPanel;

/**
 * MRModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ComplexModule extends MRModule {
    private PSwing stripChartNode;
    private JDialog stripChartDlg;
    private ComplexMRControlPanel controlPanel;
    private PumpGraphic pumpGraphic;

    public ComplexModule() {
        super( "Experiments" );

        // Disable marking of the selected molecule and its nearest neighbor
        AbstractSimpleMoleculeGraphic.setMARK_SELECTED_MOLECULE( true );

        // Add the pump
        MRModel model = getMRModel();
        pumpGraphic = new PumpGraphic( this );
        // 15 is the wall thickness of the box graphic
        pumpGraphic.setOffset( model.getBox().getMinX() + model.getBox().getWidth(),
                               model.getBox().getMinY() + model.getBox().getHeight() + 15 - pumpGraphic.getPumpBaseLocation().getY() );
        getSpatialView().addChild( pumpGraphic );

        controlPanel = new ComplexMRControlPanel( this );
        getControlPanel().addControl( controlPanel );
    }

    public void reset() {
        super.reset();
        setInitialConditions();
        controlPanel.reset();
        pumpGraphic.reset();
    }

    private void setInitialConditions() {
        setStripChartVisible( false );

    }

    private void createControls() {
        JButton setupGoBtn = new JButton();
        setupGoBtn.addActionListener( new SetupGoAction( setupGoBtn, this ) );

        getControlPanel().addControl( setupGoBtn );
    }

    public void setCountersEditable( boolean editable ) {
        ComplexMRControlPanel controlPanel = (ComplexMRControlPanel)getMRControlPanel();
        controlPanel.getMoleculeInstanceControlPanel().setCountersEditable( editable );
    }

    public void setStripChartVisible( boolean visible ) {
        if( visible ) {
            StripChart stripChart = new MoleculePopulationsStripChart( getMRModel(), getClock(), 500, 0, 20, 1 );
            ChartPanel chartPanel = new ChartPanel( stripChart.getChart() );
            chartPanel.setPreferredSize( new java.awt.Dimension( 400, 200 ) );
            stripChartNode = new PSwing( (PhetPCanvas)getSimulationPanel(), chartPanel );

            stripChartDlg = new JDialog( PhetUtilities.getPhetFrame(), false );
            PhetPCanvas stripChartCanvas = new PhetPCanvas();
            stripChartCanvas.addScreenChild( stripChartNode );
            stripChartCanvas.setPreferredSize( chartPanel.getPreferredSize() );
            stripChartDlg.getContentPane().add( stripChartCanvas );
            stripChartDlg.pack();
            stripChartDlg.setVisible( true );
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

