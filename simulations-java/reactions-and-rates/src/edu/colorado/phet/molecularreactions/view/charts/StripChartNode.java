/* Copyright 2003-2007, University of Colorado */

package edu.colorado.phet.molecularreactions.view.charts;

import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.molecularreactions.util.Resetable;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.nodes.ZoomControlNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import org.jfree.chart.ChartPanel;
import org.jfree.data.Range;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

/**
 * StripChartDialog
 * <p/>
 * A PNode that holds a MolecularPopulationsStripChart and a
 * couple of controls to manage it.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class StripChartNode extends PNode implements Resetable, Rescaleable {
    private MoleculePopulationsStripChart stripChart;
    private IClock clock;
    private static final double ZOOM_SCALE_FACTOR = 2.0;
    private ZoomControlNode zoomControl;

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
        final ChartPanel chartPanel = new ChartPanel( stripChart.getChart() );
        chartPanel.setBackground( MRConfig.MOLECULE_PANE_BACKGROUND );

        // Create a scrollbar
        final JScrollBar scrollBar = new JScrollBar( JScrollBar.HORIZONTAL, 0, (int)stripChart.getViewableRangeX(), 0, (int)xAxisRange );
        scrollBar.addAdjustmentListener( new StripChartAdjuster( stripChart ) );

        // Set sizes and locations of the chart and the scrollbar
        Dimension scrollBarDim = new Dimension( (int)( size.getWidth() - scrollBarInsets.left - scrollBarInsets.right ), 15 );

        scrollBar.setPreferredSize( scrollBarDim );
        final PSwing scrollBarNode = new PSwing(scrollBar );
        scrollBarNode.setPaint( new Color( 0, 0, 0, 0 ) );
        scrollBarNode.setOffset( scrollBarInsets.left,
                                 size.getHeight() - scrollBarNode.getFullBounds().getHeight() - scrollBarInsets.bottom );

        chartPanel.setPreferredSize( new Dimension( size.width,
                                                    size.height - (int)scrollBarNode.getFullBounds().getHeight() - scrollBarInsets.bottom - scrollBarInsets.top ) );

        final PNode stripChartNode = new PSwing(chartPanel );
        stripChartCanvas.addScreenChild( stripChartNode );
        scrollBar.addAdjustmentListener( new AdjustmentListener() {
            public void adjustmentValueChanged( AdjustmentEvent e ) {
                scrollBarNode.repaint();
                stripChartNode.repaint();
            }
        } );

        scrollBar.setVisible( true );
        scrollBar.setEnabled( true );

        // Add a rescale button
        addZoomControl( module, stripChartCanvas, chartPanel );

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
                //scrollBar.setEnabled( false );
                scrollBarNode.repaint();
            }

            public void clockPaused( ClockEvent clockEvent ) {
                scrollBar.setEnabled( true );
                scrollBarNode.repaint();
            }
        } );
        //scrollBar.setEnabled( !module.getClock().isRunning() );

        this.addChild( stripChartCanvas.getPhetRootNode() );
    }

    private void addZoomControl( MRModule module, PhetPCanvas stripChartCanvas, ChartPanel chartPanel ) {
        zoomControl = new ZoomControlNode( ZoomControlNode.VERTICAL );

        zoomControl.setOffset( 10, chartPanel.getPreferredSize().getHeight() - zoomControl.getFullBounds().getHeight() );

        stripChartCanvas.addScreenChild( zoomControl );

        zoomControl.addZoomListener( new ChartRescalingZoomListener() );
    }

    /*
     * Starts and stops recording
     */
    public void setRecording( boolean recording ) {

        // Only do the following if the chart isn't already recording.
        if( stripChart.isRecording() != recording ) {
            if( recording ) {
                stripChart.startRecording( clock.getSimulationTime() );
            }
            else {
                stripChart.stopRecording();
            }
        }
    }

    public void reset() {
        stripChart.reset();
        repaint();
    }

    public void rescale() {
        stripChart.rescale();
    }

    private class ChartRescalingZoomListener implements ZoomControlNode.ZoomListener {
        private static final int MIN_NUM_MOLECULES = 2;

        public void zoomedOut() {
            handleZoom( true );
        }

        public void zoomedIn() {
            handleZoom( false );
        }

        private void handleZoom( boolean zoomOut ) {
            Range plotRange = stripChart.getYRange();

            double newMax = plotRange.getLength();

            if ( zoomOut ) {
                newMax *= ZOOM_SCALE_FACTOR;
            }
            else {
                newMax /= ZOOM_SCALE_FACTOR;

                if (newMax < MIN_NUM_MOLECULES ) {
                    newMax = MIN_NUM_MOLECULES;
                }
            }

            stripChart.setYRange( 0, (int)newMax );
            repaint();
        }
    }
}
