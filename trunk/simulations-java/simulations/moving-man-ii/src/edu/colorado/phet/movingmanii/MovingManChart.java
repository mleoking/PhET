package edu.colorado.phet.movingmanii;

import edu.colorado.phet.common.motion.model.TimeData;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PClip;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Working directly with JFreeChart has been problematic for the motion-series sim since we need more flexibility + performance than it provides.
 * We need the following features:
 * 1. Ability to show labels on each chart when the horizontal axis is shared
 * 2. Ability to easily position controls within or next to the chart, and to have their axes synchronized
 * 3. Ability to draw custom curves by dragging directly on the chart.
 * 4. Ability to line up charts with each other and minimize to put "expand" buttons between charts
 * 4. Improved performance.
 */
public class MovingManChart extends PNode {
    private Rectangle2D.Double dataModelBounds;
    public double dataAreaWidth;
    public double dataAreaHeight;

    public MovingManChart(Rectangle2D.Double dataModelBounds,
                          double dataAreaWidth,//Width of the chart area
                          double dataAreaHeight) {
        this.dataModelBounds = dataModelBounds;
        this.dataAreaWidth = dataAreaWidth;
        this.dataAreaHeight = dataAreaHeight;
        PhetPPath background = new PhetPPath(new Rectangle2D.Double(0, 0, dataAreaWidth, dataAreaHeight), Color.white, new BasicStroke(1), Color.black);
        addChild(background);

        modelViewTransform2D = new ModelViewTransform2D(dataModelBounds, new Rectangle2D.Double(0, 0, dataAreaWidth, dataAreaHeight));//todo: update when dependencies change

        int numDomainMarks = 10;
        Function.LinearFunction domainFunction = new Function.LinearFunction(0, numDomainMarks, dataModelBounds.getX(), dataModelBounds.getMaxX());
        ArrayList<DomainTickMark> domainTickMarks = new ArrayList<DomainTickMark>();
        for (int i = 0; i < numDomainMarks + 1; i++) {
            double x = domainFunction.evaluate(i);
            DomainTickMark tickMark = new DomainTickMark(x);
            Point2D location = modelToView(new TimeData(0, x));
            tickMark.setOffset(location.getX(), dataAreaHeight);
            domainTickMarks.add(tickMark);
            addChild(tickMark);

            DomainGridLine gridLine = new DomainGridLine(x, this);
            addChild(gridLine);
        }
        DomainTickMark last = domainTickMarks.get(domainTickMarks.size()-1);
        last.setTickText(last.getTickText()+" sec");

        int numRangeMarks = 4;
        Function.LinearFunction rangeFunction = new Function.LinearFunction(0, numRangeMarks, dataModelBounds.getY(), dataModelBounds.getMaxY());
        for (int i = 0; i < numRangeMarks + 1; i++) {
            double y = rangeFunction.evaluate(i);
            RangeTickMark tickMark = new RangeTickMark(y);
            Point2D location = modelToView(new TimeData(y, 0));
            tickMark.setOffset(0, location.getY());
            addChild(tickMark);

            RangeGridLine gridLine = new RangeGridLine(y, this);
            addChild(gridLine);
        }
    }

    public double viewToModelDeltaX(double dx) {
        return modelViewTransform2D.viewToModelDifferentialX(dx);
    }

    public double getMaxRangeValue() {
        return dataModelBounds.getMaxY();
    }

    public double getMinRangeValue() {
        return dataModelBounds.getMinY();
    }

    public Point2D viewToModel(Point2D.Double pt) {
        return modelViewTransform2D.modelToViewDouble(pt);
    }

    public double viewToModelDY(double dy) {
        return modelViewTransform2D.viewToModelDifferentialY(dy);
    }

    private static class DomainTickMark extends PNode {
        private PText text;

        private DomainTickMark(double x) {
            double tickWidth = 1.0;
            PhetPPath tick = new PhetPPath(new Rectangle2D.Double(-tickWidth / 2, 0, tickWidth, 4), Color.black);
            addChild(tick);
            text = new PText(new DecimalFormat("0.0").format(x));
            text.setFont(new PhetFont(14, true));
            addChild(text);
            text.setOffset(tick.getFullBounds().getCenterX() - text.getFullBounds().getWidth() / 2, tick.getFullBounds().getHeight());
        }

        public String getTickText() {
            return text.getText();
        }

        public void setTickText(String s) {
            text.setText(s);//todo: need a better way of doing this that respects layout
        }
    }

    private static class RangeTickMark extends PNode {
        private RangeTickMark(double y) {
            double tickHeight = 1.0;
            PhetPPath tick = new PhetPPath(new Rectangle2D.Double(0, -tickHeight / 2, 4, tickHeight), Color.black);
            addChild(tick);
            PText text = new PText(new DecimalFormat("0.0").format(y));
            text.setFont(new PhetFont(14, true));
            addChild(text);
            double insetDX = 7;//spacing between the text and tick
            text.setOffset(tick.getFullBounds().getMinX() - text.getFullBounds().getWidth() - insetDX, tick.getFullBounds().getCenterY() - text.getFullBounds().getHeight() / 2);
        }
    }

    private static class DomainGridLine extends PNode {
        private DomainGridLine(double x, MovingManChart chart) {
            double chartX = chart.modelToView(new TimeData(0, x)).getX();
            PhetPPath tick = new PhetPPath(new Line2D.Double(chartX, chart.dataAreaHeight, chartX, 0), new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[]{10, 3}, 0), Color.lightGray);
            addChild(tick);
        }
    }

    private static class RangeGridLine extends PNode {
        private RangeGridLine(double y, MovingManChart chart) {
            double chartY = chart.modelToView(new TimeData(y, 0)).getY();
            PhetPPath tick = new PhetPPath(new Line2D.Double(0, chartY, chart.dataAreaWidth, chartY), new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[]{10, 3}, 0), Color.lightGray);
            addChild(tick);
        }
    }

    public void addDataSeries(MovingManDataSeries dataSeries, Color color) {
        addChild(new LineSeriesNode(dataSeries, color));
//        serieses.add(dataSeries);
    }

    private ModelViewTransform2D modelViewTransform2D;

    public Point2D modelToView(TimeData point) {
        return modelViewTransform2D.modelToViewDouble(point.getTime(), point.getValue());
    }

    private class LineSeriesNode extends PNode {

        public LineSeriesNode(final MovingManDataSeries dataSeries, Color color) {
            PClip clip = new PClip();
            clip.setPathTo(new Rectangle2D.Double(0, 0, dataAreaWidth, dataAreaHeight));
            final PhetPPath path = new PhetPPath(new BasicStroke(2), color);
            clip.addChild(path);
            addChild(clip);
            dataSeries.addListener(new MovingManDataSeries.Listener() {
                public void changed() {
                    TimeData[] points = dataSeries.getData();

                    DoubleGeneralPath generalPath = new DoubleGeneralPath();
                    for (int i = 0; i < points.length; i++) {
                        Point2D mapped = modelToView(points[i]);
                        if (i == 0) {
                            generalPath.moveTo(mapped);
                        } else {
                            generalPath.lineTo(mapped);
                        }
                    }

                    path.setPathTo(generalPath.getGeneralPath());
                }
            });
        }
    }
}