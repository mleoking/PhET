// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactionsandrates.view.charts;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.reactionsandrates.MRConfig;
import edu.colorado.phet.reactionsandrates.modules.MRModule;
import edu.colorado.phet.reactionsandrates.util.Resetable;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import org.jfree.chart.ChartPanel;

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
public class StripChartNode extends AbstractRescaleableChartNode implements Resetable, Rescaleable {
    private MoleculePopulationsStripChart stripChart;
    private IClock clock;

    public StripChartNode( MRModule module, Dimension size ) {
        PhetPCanvas stripChartCanvas = new PhetPCanvas();

        this.clock = module.getClock();

        final double xAxisRange = MRConfig.STRIP_CHART_VISIBLE_TIME_RANGE;
        Insets scrollBarInsets = new Insets( 3, 80, 3, 10 );

        int numBufferedDataPoints = MRConfig.STRIP_CHART_BUFFER_SIZE;
        stripChart = new MoleculePopulationsStripChart( module.getMRModel(),
                                                        clock,
                                                        xAxisRange,
                                                        0,
                                                        MRConfig.STRIP_CHART_MIN_RANGE_Y,
                                                        10,
                                                        numBufferedDataPoints );
        final ChartPanel chartPanel = new ChartPanel( stripChart.getChart() );
        chartPanel.setBackground( MRConfig.MOLECULE_PANE_BACKGROUND );

        // Create a scrollbar
        final JScrollBar scrollBar = new JScrollBar( JScrollBar.HORIZONTAL, 0, (int)stripChart.getViewableRangeX(), 0, (int)xAxisRange );
        scrollBar.addAdjustmentListener( new StripChartAdjuster( stripChart ) );

        // Set sizes and locations of the chart and the scrollbar
        Dimension scrollBarDim = new Dimension( (int)( size.getWidth() - scrollBarInsets.left - scrollBarInsets.right ), 15 );

        scrollBar.setPreferredSize( scrollBarDim );
        final PSwing scrollBarNode = new PSwing( scrollBar );
        scrollBarNode.setPaint( new Color( 0, 0, 0, 0 ) );
        scrollBarNode.setOffset( scrollBarInsets.left,
                                 size.getHeight() - scrollBarNode.getFullBounds().getHeight() - scrollBarInsets.bottom );

        chartPanel.setPreferredSize( new Dimension( size.width,
                                                    size.height - (int)scrollBarNode.getFullBounds().getHeight() - scrollBarInsets.bottom - scrollBarInsets.top ) );

        final PNode stripChartNode = new PSwing( chartPanel );
        stripChartCanvas.addScreenChild( stripChartNode );
        scrollBar.addAdjustmentListener( new AdjustmentListener() {
            public void adjustmentValueChanged( AdjustmentEvent e ) {
                scrollBarNode.repaint();
                stripChartNode.repaint();
            }
        } );

        scrollBar.setVisible( true );
        scrollBar.setEnabled( true );

        addZoomControl( size, stripChartCanvas, stripChart );

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

        this.addChild( stripChartCanvas.getPhetRootNode() );
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

}
