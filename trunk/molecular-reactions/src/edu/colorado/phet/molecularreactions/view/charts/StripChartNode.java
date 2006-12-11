/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view.charts;

import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.molecularreactions.util.Resetable;
import edu.colorado.phet.molecularreactions.util.StripChart;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * StripChartDialog
 * <p/>
 * A dialog that holds a MolecularPopulationsStripChart and a
 * couple of controls to manage it.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class StripChartNode extends PNode implements Resetable {
    private MoleculePopulationsStripChart stripChart;
    private IClock clock;

    public StripChartNode( MRModule module, Dimension size ) {
        PhetPCanvas stripChartCanvas = new PhetPCanvas();

        this.clock = module.getClock();
        final double xAxisRange = MRConfig.STRIP_CHART_VISIBLE_TIME_RANGE;
        Insets scrollBarInsets = new Insets( 3, 50, 3, 10 );


        int numBufferedDataPoints = MRConfig.STRIP_CHART_BUFFER_SIZE;
        stripChart = new MoleculePopulationsStripChart( module.getMRModel(),
                                                        module.getClock(),
                                                        xAxisRange,
                                                        0,
                                                        MRConfig.STRIP_CHART_MIN_RANGE_Y,
                                                        1,
                                                        numBufferedDataPoints );
        ChartPanel chartPanel = new ChartPanel( stripChart.getChart() );
        chartPanel.setBackground( MRConfig.MOLECULE_PANE_BACKGROUND );

        // Create a scrollbar
        final JScrollBar scrollBar = new JScrollBar( JScrollBar.HORIZONTAL, 0, (int)stripChart.getViewableRangeX(), 0, (int)xAxisRange );
        scrollBar.addAdjustmentListener( new StripChartAdjuster( stripChart ) );

        // Set sizes and locations of the chart and the scrollbar
        scrollBar.setPreferredSize( new Dimension( (int)( size.getWidth() - scrollBarInsets.left - scrollBarInsets.right ), 15 ) );
        PSwing scrollBarNode = new PSwing( stripChartCanvas, scrollBar );
        scrollBarNode.setPaint( new Color( 0, 0, 0, 0 ) );
        scrollBarNode.setOffset( scrollBarInsets.left,
                                 size.getHeight() - scrollBarNode.getFullBounds().getHeight() - scrollBarInsets.bottom );

        chartPanel.setPreferredSize( new Dimension( size.width,
                                                    size.height - (int)scrollBarNode.getFullBounds().getHeight() - scrollBarInsets.bottom - scrollBarInsets.top ) );

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
        rescaleNode.setOffset( 10,
                               chartPanel.getPreferredSize().getHeight() - rescaleNode.getFullBounds().getHeight() );
        stripChartCanvas.addScreenChild( rescaleNode );
        stripChartCanvas.setOpaque( true );

        stripChartCanvas.addScreenChild( scrollBarNode );
        stripChart.addListener( new StripChart.Listener() {
            public void dataChanged() {
                scrollBar.setMaximum( (int)Math.max( stripChart.getMaxX(), xAxisRange ) );
                scrollBar.setMinimum( (int)stripChart.getMinX() );
                scrollBar.setVisibleAmount( (int)stripChart.getViewableRangeX() );
                scrollBar.setValue( (int)stripChart.getMaxX() );
            }
        } );

        // The scroll bar should only be enabled when the clock is paused
        module.getClock().addClockListener( new ClockAdapter() {
            public void clockStarted( ClockEvent clockEvent ) {
                scrollBar.setEnabled( false );
            }

            public void clockPaused( ClockEvent clockEvent ) {
                scrollBar.setEnabled( true );
            }
        } );
        scrollBar.setEnabled( !module.getClock().isRunning() );

        this.addChild( stripChartCanvas.getPhetRootNode() );
    }

    /**
     * Starts and stops recording
     */
    public void setRecording( boolean recording ) {
        if( recording ) {
            stripChart.startRecording( clock.getSimulationTime() );
            stripChart.rescale();
        }
        else {
            stripChart.stopRecording();
        }
    }

    public void reset() {
        stripChart.reset();
//        stripChart.startRecording( clock.getSimulationTime() );
    }
}
