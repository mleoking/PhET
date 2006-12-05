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
import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.molecularreactions.util.StripChart;
import edu.colorado.phet.molecularreactions.util.Resetable;
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
 * <p>
 * A dialog that holds a MolecularPopulationsStripChart and a
 * couple of controls to manage it.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class StripChartDialog extends JDialog implements Resetable {
    private MoleculePopulationsStripChart stripChart;
    private IClock clock;

    public StripChartDialog( MRModule module ) {
        super(PhetUtilities.getPhetFrame(), false);
        this.setResizable( false );
        this.clock = module.getClock();

        Dimension chartSize = new Dimension( 400, 200 );
        final double xAxisRange = MRConfig.STRIP_CHART_VISIBLE_TIME_RANGE ;
        int numBufferedDataPoints = MRConfig.STRIP_CHART_BUFFER_SIZE;
        stripChart = new MoleculePopulationsStripChart( module.getMRModel(),
                                                        module.getClock(),
                                                        xAxisRange,
                                                        0,
                                                        MRConfig.STRIP_CHART_MIN_RANGE_Y,
                                                        1,
                                                        numBufferedDataPoints );
        ChartPanel chartPanel = new ChartPanel( stripChart.getChart() );
        chartPanel.setPreferredSize( chartSize );

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
        stripChartCanvas.setBackground( UIManager.getColor("Panel.background") );

        // Add a scrollbar
        final JScrollBar scrollBar = new JScrollBar( JScrollBar.HORIZONTAL, 0, (int)stripChart.getViewableRangeX(), 0, (int)xAxisRange );
        scrollBar.addAdjustmentListener( new StripChartAdjuster( stripChart ) );

        Insets scrollBarInsets = new Insets( 3, 50, 3, 10 );
        scrollBar.setPreferredSize( new Dimension( (int)( stripChartNode.getFullBounds().getWidth() - scrollBarInsets.left - scrollBarInsets.right ), 15 ) );
        PSwing scrollBarNode = new PSwing( stripChartCanvas, scrollBar );
        scrollBarNode.setPaint( new Color(0,0,0,0));
        Dimension dialogSize = new Dimension( (int)chartSize.getWidth(),
                                              (int)( chartSize.getHeight() + scrollBarNode.getFullBounds().getHeight() +
                                                     scrollBarInsets.top + scrollBarInsets.bottom ) );

        scrollBarNode.setOffset( scrollBarInsets.left,
                                 dialogSize.getHeight() - scrollBarNode.getFullBounds().getHeight() - scrollBarInsets.bottom );
        stripChartCanvas.addScreenChild( scrollBarNode );
        stripChart.addListener( new StripChart.Listener() {
            public void dataChanged() {
                scrollBar.setMaximum( (int)Math.max( stripChart.getMaxX(), xAxisRange ) );
                scrollBar.setMinimum( (int)stripChart.getMinX() );
                scrollBar.setVisibleAmount( (int)stripChart.getViewableRangeX() );
                scrollBar.setValue( (int)stripChart.getMaxX());
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
        });
        scrollBar.setEnabled( !module.getClock().isRunning() );

        stripChartCanvas.setPreferredSize( dialogSize );
        this.getContentPane().add( stripChartCanvas );
        this.pack();

        // Start the strip chart recording
        stripChart.startRecording( clock.getSimulationTime() );
    }

    /**
     * Rescale the strip chart
     */
    public void rescaleChart() {
        stripChart.rescale();
    }


    public void reset() {
        stripChart.reset();
        stripChart.startRecording( clock.getSimulationTime() );
    }
}
