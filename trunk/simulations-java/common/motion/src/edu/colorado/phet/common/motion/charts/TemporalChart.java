package edu.colorado.phet.common.motion.charts;

import edu.colorado.phet.common.motion.model.TimeData;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PClip;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;

/**
 * Working directly with JFreeChart has been problematic for the motion-series sim since we need more flexibility + performance than it provides.
 * We need the following features:
 * 1. Ability to show labels on each chart when the horizontal axis is shared
 * 2. Ability to easily position controls within or next to the chart, and to have their axes synchronized
 * 3. Ability to draw custom curves by dragging directly on the chart.
 * 4. Ability to line up charts with each other and minimize to put "expand" buttons between charts
 * 4. Improved performance.
 */
public class TemporalChart extends PNode {
    private MutableRectangle dataModelBounds;
    private MutableDimension viewDimension;
    private PNode chartContents;//layer for chart pnodes, for minimize/maximize support
    private ModelViewTransform2D modelViewTransform2D;
    private TickMarkAndGridLineNode tickMarksAndGridLines;
    //This string is a hack to allow sims to pass in the string translation instead of requiring it to appear in phetcommon
    public static String SEC_TEXT = "sec";
    public static String TIME_LABEL_PATTERN = "{0} {1}";

    public TemporalChart(Rectangle2D.Double dataModelBounds, ChartCursor cursor) {
        this(dataModelBounds, 100, 100, cursor);//useful for layout code that updates size later instead of at construction and later
    }

    public TemporalChart(final Rectangle2D.Double dataModelBounds,
                         final double dataAreaWidth,//Width of the chart area
                         final double dataAreaHeight,
                         final ChartCursor cursor //Must be provided by the client so that charts cursor locations can be synchronized
    ) {
        this.dataModelBounds = new MutableRectangle(dataModelBounds);
        this.viewDimension = new MutableDimension(dataAreaWidth, dataAreaHeight);
        chartContents = new PNode();
        addChild(chartContents);

        final PhetPPath background = new PhetPPath(Color.white, new BasicStroke(1), Color.black);
        SimpleObserver backgroundUpdate = new SimpleObserver() {
            public void update() {
                background.setPathTo(new Rectangle2D.Double(0, 0, viewDimension.getWidth(), viewDimension.getHeight()));
            }
        };
        backgroundUpdate.update();
        viewDimension.addObserver(backgroundUpdate);
        addChartChild(background);

        tickMarksAndGridLines = new TickMarkAndGridLineNode();
        tickMarksAndGridLines.setPickable(false);
        tickMarksAndGridLines.setChildrenPickable(false);
        addChartChild(tickMarksAndGridLines);

        modelViewTransform2D = new ModelViewTransform2D(dataModelBounds, new Rectangle2D.Double(0, 0, dataAreaWidth, dataAreaHeight));//todo: attach listeners to the mvt2d
        SimpleObserver updateBasedOnViewBoundsChange = new SimpleObserver() {
            public void update() {
                if (viewDimension.getWidth() > 0 && viewDimension.getHeight() > 0)
                    modelViewTransform2D.setViewBounds(new Rectangle2D.Double(0, 0, viewDimension.getWidth(), viewDimension.getHeight()));
            }
        };
        updateBasedOnViewBoundsChange.update();
        viewDimension.addObserver(updateBasedOnViewBoundsChange);

        SimpleObserver updateBasedOnModelViewChange = new SimpleObserver() {
            public void update() {
                modelViewTransform2D.setModelBounds(TemporalChart.this.dataModelBounds.toRectangle2D());
            }
        };
        updateBasedOnModelViewChange.update();
        this.dataModelBounds.addObserver(updateBasedOnModelViewChange);

        modelViewTransform2D.addTransformListener(new TransformListener() {
            public void transformChanged(ModelViewTransform2D mvt) {
                updateTickMarksAndGridLines();
            }
        });
        updateTickMarksAndGridLines();

        addChartChild(new CursorNode(cursor, this));
    }

    private void updateTickMarksAndGridLines() {
        tickMarksAndGridLines.removeAllChildren();
        int numDomainMarks = 10;
        Function.LinearFunction domainFunction = new Function.LinearFunction(0, numDomainMarks, dataModelBounds.getX(), dataModelBounds.getMaxX());
        PNode domainTickMarks = new PNode();
        for (int i = 0; i < numDomainMarks + 1; i++) {
            final double x = domainFunction.evaluate(i);
            final DomainTickMark tickMark = new DomainTickMark(x);
            domainTickMarks.addChild(tickMark);

            DomainGridLine gridLine = new DomainGridLine(x, this);
            tickMarksAndGridLines.addChild(gridLine);

            SimpleObserver domainTickMarkUpdate = new SimpleObserver() {
                public void update() {
                    Point2D location = modelToView(new TimeData(0, x));
                    tickMark.setOffset(location.getX(), viewDimension.getHeight());
                }
            };
            domainTickMarkUpdate.update();
            viewDimension.addObserver(domainTickMarkUpdate);
        }
        tickMarksAndGridLines.setDomainTickMarks(domainTickMarks);
        DomainTickMark last = (DomainTickMark) domainTickMarks.getChild(domainTickMarks.getChildrenCount() - 1);
        last.setTickText(MessageFormat.format(TIME_LABEL_PATTERN, last.getTickText(), SEC_TEXT));

        int numRangeMarks = 4;
        Function.LinearFunction rangeFunction = new Function.LinearFunction(0, numRangeMarks, dataModelBounds.getY(), dataModelBounds.getMaxY());
        for (int i = 0; i < numRangeMarks + 1; i++) {
            final double y = rangeFunction.evaluate(i);
            final RangeTickMark tickMark = new RangeTickMark(y);
            tickMarksAndGridLines.addChild(tickMark);

            RangeGridLine gridLine = new RangeGridLine(y, this);
            tickMarksAndGridLines.addChild(gridLine);

            SimpleObserver rangeTickMarkUpdate = new SimpleObserver() {
                public void update() {
                    Point2D location = modelToView(new TimeData(y, 0));
                    tickMark.setOffset(0, location.getY());
                }
            };
            rangeTickMarkUpdate.update();
            viewDimension.addObserver(rangeTickMarkUpdate);
            this.dataModelBounds.addObserver(rangeTickMarkUpdate);
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

    public void setViewDimension(double dataAreaWidth, double dataAreaHeight) {
        viewDimension.setDimension(dataAreaWidth, dataAreaHeight);
        //todo: update everything that needs updating
    }

    public MutableDimension getViewDimension() {
        return viewDimension;
    }

    public double viewToModel(double x) {
        return modelViewTransform2D.viewToModelDifferentialX(x);
    }

    //Sets visibility based on whether the chart is visible, this is not clipped to the chart

    public void addChartChild(PNode child) {
        chartContents.addChild(child);
    }

    /**
     * Determines the extent to which the labels and tick marks extend beyond the top and bottom of the chart area.
     *
     * @return
     */
    public double getDomainLabelHeight() {
        return 20.0;//todo: don't hard code this.
    }

    //TODO: rewrite tickMarksAndGridLines so that no lookup is necessary here

    public void setDomainAxisLabelsVisible(boolean b) {
        tickMarksAndGridLines.setDomainTickMarksVisible(b);
    }

    public static class DomainTickMark extends PNode {
        private PText text;

        public DomainTickMark(double x) {
            double tickWidth = 1.0;
            PhetPPath tick = new PhetPPath(new Rectangle2D.Double(-tickWidth / 2, 0, tickWidth, 4), Color.black);
            addChild(tick);
            String text = new DecimalFormat("0.0").format(x);
            //hide the decimal point where possible, but don't trim for series like 4.0, 4.5, etc.
            if (text.endsWith(".0")) text = text.substring(0, text.indexOf(".0"));//TODO handle comma for il8n
            this.text = new PText(text);
            this.text.setFont(new PhetFont(14, true));
            addChild(this.text);
            this.text.setOffset(tick.getFullBounds().getCenterX() - this.text.getFullBounds().getWidth() / 2, tick.getFullBounds().getHeight());
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
        private DomainGridLine(final double x, final TemporalChart chart) {
            final PhetPPath tick = new PhetPPath(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[]{10, 3}, 0), Color.lightGray);
            final SimpleObserver update = new SimpleObserver() {
                public void update() {
                    final double chartX = chart.modelToView(new TimeData(0, x)).getX();
                    tick.setPathTo(new Line2D.Double(chartX, chart.viewDimension.getHeight(), chartX, 0));
                }
            };
            update.update();
            addChild(tick);
        }
    }

    private static class RangeGridLine extends PNode {
        private RangeGridLine(final double y, final TemporalChart chart) {
            final PhetPPath tick = new PhetPPath(new BasicStroke(y == 0 ? 1.25f : 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[]{10, 3}, 0), y == 0 ? Color.black : Color.lightGray);
            addChild(tick);
            final SimpleObserver pathUpdate = new SimpleObserver() {
                public void update() {
                    final double chartY = chart.modelToView(new TimeData(y, 0)).getY();
                    tick.setPathTo(new Line2D.Double(0, chartY, chart.viewDimension.getWidth(), chartY));
                }
            };
            pathUpdate.update();
        }
    }

    public void addDataSeries(TemporalDataSeries dataSeries, Color color) {
        chartContents.addChild(new LineSeriesNode(dataSeries, color));
    }

    public Point2D modelToView(TimeData point) {
        return modelViewTransform2D.modelToViewDouble(point.getTime(), point.getValue());
    }

    private class LineSeriesNode extends PNode {
        public LineSeriesNode(final TemporalDataSeries dataSeries, Color color) {
            final PClip clip = new PClip();
            final SimpleObserver so = new SimpleObserver() {
                public void update() {
                    clip.setPathTo(new Rectangle2D.Double(0, 0, viewDimension.getWidth(), viewDimension.getHeight()));
                }
            };
            viewDimension.addObserver(so);
            so.update();

            final PhetPPath path = new PhetPPath(new GeneralPath(), new BasicStroke(3), color) {//todo: is performance dependent on stroke width here?

                //Stroke.createStrokedPath was by far the highest allocation in JProfiler
                //And severe lag during GC suggested this workaround
                //I'm not sure whether/how much this helps, perhaps GC's are less frequent or less severe?
                //Fixing this really changes the distribution of memory allocation as seen by JProfiler
                public Rectangle2D getPathBoundsWithStroke() {
                    return new Rectangle2D.Double(0, 0, viewDimension.getWidth(), viewDimension.getHeight()); //always return max bounds
                }
            };
            clip.addChild(path);
            addChild(clip);
            final TemporalDataSeries.Listener seriesListener = new TemporalDataSeries.Listener() {
                public void entireSeriesChanged() {
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

                public void dataPointAdded(TimeData point) {
                    GeneralPath ref = path.getPathReference();
                    final Point2D mapped = modelToView(point);
                    float x = (float) mapped.getX();
                    float y = (float) mapped.getY();
                    if (ref.getCurrentPoint() == null) {
                        ref.moveTo(x, y);
                    } else {
                        ref.lineTo(x, y);
                    }
                    //TODO: signify a change, see path.setPathTo
//                    path.firePropertyChange(PPath.PROPERTY_CODE_PATH, PPath.PROPERTY_PATH, null, path);
                    path.updateBoundsFromPath();
                    path.invalidatePaint();
                }
            };
            dataSeries.addListener(seriesListener);

            //Redraw entire path when transform changes
            TransformListener listener = new TransformListener() {
                public void transformChanged(ModelViewTransform2D mvt) {
                    seriesListener.entireSeriesChanged();
                }
            };
            modelViewTransform2D.addTransformListener(listener);
            listener.transformChanged(null);

        }
    }

    public MutableRectangle getDataModelBounds() {
        return dataModelBounds;
    }

    public void zoomInHorizontal() {
        dataModelBounds.setHorizontalRange(dataModelBounds.getMinX(), dataModelBounds.getMaxX() - 2);
    }

    public void zoomOutHorizontal() {
        dataModelBounds.setHorizontalRange(dataModelBounds.getMinX(), dataModelBounds.getMaxX() + 2);
    }

    double verticalScaleFactor = 1.2;

    public void zoomOutVertical() {
        dataModelBounds.setVerticalRange(dataModelBounds.getMinY() * verticalScaleFactor, dataModelBounds.getMaxY() * verticalScaleFactor);
    }

    public void zoomInVertical() {
        //assumes centered on y=0 axis
        dataModelBounds.setVerticalRange(dataModelBounds.getMinY() / verticalScaleFactor, dataModelBounds.getMaxY() / verticalScaleFactor);
    }

    private class TickMarkAndGridLineNode extends PNode {
        private PNode domainTickMarks;
        private boolean domainTickMarksVisible;

        public void setDomainTickMarks(PNode domainTickMarks) {
            if (domainTickMarks != null) {
                removeChild(domainTickMarks);
            }
            this.domainTickMarks = domainTickMarks;
            addChild(domainTickMarks);
            this.domainTickMarks.setVisible(domainTickMarksVisible);
        }

        public void setDomainTickMarksVisible(boolean b) {
            this.domainTickMarksVisible = b;
            if (domainTickMarks != null) {
                domainTickMarks.setVisible(b);
            }
        }
    }
}