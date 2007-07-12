package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.motion.graphs.MotionControlGraph;
import edu.colorado.phet.common.motion.model.ISimulationVariable;
import edu.colorado.phet.common.motion.model.ITimeSeries;
import edu.colorado.phet.common.motion.model.UpdateStrategy;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.umd.cs.piccolo.nodes.PImage;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.ui.RectangleEdge;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Author: Sam Reid
 * Jul 12, 2007, 9:43:17 AM
 */
public class RotationGraph extends MotionControlGraph {
    public RotationGraph( PhetPCanvas pSwingCanvas, ISimulationVariable simulationVariable, ITimeSeries timeSeries,
                          String label, String title, String units, double min, double max, Color color, PImage thumb,
                          RotationModel motionModel, boolean editable, TimeSeriesModel timeSeriesModel,
                          UpdateStrategy updateStrategy, double maxDomainValue, RotationPlatform iPositionDriven ) {
        super( pSwingCanvas, simulationVariable, timeSeries,
               label, title, min, max, color, thumb,
               motionModel, editable, timeSeriesModel, updateStrategy, maxDomainValue, iPositionDriven );
        ValueAxis oldRangeAxis = getJFreeChartNode().getChart().getXYPlot().getRangeAxis();
        RotationGraphNumberAxis verticalAxis = new RotationGraphNumberAxis( title+" ("+units+")" );
        verticalAxis.setRange( oldRangeAxis.getRange() );
        getJFreeChartNode().getChart().getXYPlot().setRangeAxis( verticalAxis );
    }

    public static class RotationGraphNumberAxis extends NumberAxis {
        public RotationGraphNumberAxis( String title ) {
            super( title );
            setLabelFont( new PhetDefaultFont( 14, true ) );
        }

        protected void selectVerticalAutoTickUnit( Graphics2D g2,
                                                   Rectangle2D dataArea,
                                                   RectangleEdge edge ) {
            //modify the value chosen by the superclass
            super.selectVerticalAutoTickUnit( g2, dataArea, edge );
            setTickUnit( new NumberTickUnit( getTickUnit().getSize() * 2 ), false, false );
        }
    }
}
