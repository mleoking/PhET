package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.motion.graphs.MotionControlGraph;
import edu.colorado.phet.common.motion.model.ISimulationVariable;
import edu.colorado.phet.common.motion.model.ITimeSeries;
import edu.colorado.phet.common.motion.model.UpdateStrategy;
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
                          String label, String title, double min, double max, Color color, PImage thumb,
                          RotationModel motionModel, boolean editable, TimeSeriesModel timeSeriesModel,
                          UpdateStrategy updateStrategy, double maxDomainValue, RotationPlatform iPositionDriven ) {
        super( pSwingCanvas, simulationVariable, timeSeries,
               label, title, min, max, color, thumb,
               motionModel, editable, timeSeriesModel, updateStrategy, maxDomainValue, iPositionDriven );
        ValueAxis oldRangeAxis = getJFreeChartNode().getChart().getXYPlot().getRangeAxis();
//        NumberAxis numberAxis = (NumberAxis)getJFreeChartNode().getChart().getXYPlot().getRangeAxis();
//        numberAxis.setTickUnit( new NumberTickUnit( numberAxis.getRange().getLength() / 3 ) );
        RotationGraphNumberAxis verticalAxis = new RotationGraphNumberAxis( title );
        verticalAxis.setRange( oldRangeAxis.getRange() );
        getJFreeChartNode().getChart().getXYPlot().setRangeAxis( verticalAxis );
    }

    public static class RotationGraphNumberAxis extends NumberAxis {
        public RotationGraphNumberAxis( String title ) {
            super( title );
        }

        /**
         * Selects an appropriate tick value for the axis.  The strategy is to
         * display as many ticks as possible (selected from an array of 'standard'
         * tick units) without the labels overlapping.
         *
         * @param g2       the graphics device.
         * @param dataArea the area in which the plot should be drawn.
         * @param edge     the axis location.
         */
        protected void selectVerticalAutoTickUnit( Graphics2D g2,
                                                   Rectangle2D dataArea,
                                                   RectangleEdge edge ) {
            super.selectVerticalAutoTickUnit( g2, dataArea, edge );
            NumberTickUnit unit = getTickUnit();
            setTickUnit( new NumberTickUnit( unit.getSize() * 2 ), false, false );
//            double tickLabelHeight = estimateMaximumTickLabelHeight( g2 );
//
//            // start with the current tick unit...
//            TickUnitSource tickUnits = getStandardTickUnits();
//            TickUnit unit1 = tickUnits.getCeilingTickUnit( getTickUnit() );
//            double unitHeight = lengthToJava2D( unit1.getSize(), dataArea, edge );
//
//            // then extrapolate...
//            double guess = ( tickLabelHeight / unitHeight ) * unit1.getSize();
//
//            NumberTickUnit unit2
//                    = (NumberTickUnit)tickUnits.getCeilingTickUnit( guess );
//            double unit2Height = lengthToJava2D( unit2.getSize(), dataArea, edge );
//
//            tickLabelHeight = estimateMaximumTickLabelHeight( g2 );
//            if( tickLabelHeight > unit2Height ) {
//                unit2 = (NumberTickUnit)tickUnits.getLargerTickUnit( unit2 );
//            }
//
//            setTickUnit( unit2, false, false );

        }
    }
}
