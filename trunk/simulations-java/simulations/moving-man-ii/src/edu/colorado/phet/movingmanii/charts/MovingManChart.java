package edu.colorado.phet.movingmanii.charts;

import edu.colorado.phet.common.motion.model.TimeData;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.movingmanii.model.MovingManDataSeries;
import edu.colorado.phet.movingmanii.model.MutableBoolean;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PClip;

import java.awt.*;
import java.awt.geom.GeneralPath;
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
    private MutableDimension viewDimension;
    private PNode chartContents;//layer for chart pnodes, for minimize/maximize support
    private PImage minimizeButton;
    private PImage maximizeButton;
    private MutableBoolean maximized = new MutableBoolean(true);
    private ModelViewTransform2D modelViewTransform2D;

    public MovingManChart(Rectangle2D.Double dataModelBounds) {
        this(dataModelBounds, 100, 100);//useful for layout code that updates size later instead of at construction and later
    }

    public MovingManChart(Rectangle2D.Double dataModelBounds,
                          final double dataAreaWidth,//Width of the chart area
                          final double dataAreaHeight) {
        this.dataModelBounds = dataModelBounds;
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
        chartContents.addChild(background);

        modelViewTransform2D = new ModelViewTransform2D(dataModelBounds, new Rectangle2D.Double(0, 0, dataAreaWidth, dataAreaHeight));//todo: attach listeners to the mvt2d
        SimpleObserver mvtUpdate = new SimpleObserver() {
            public void update() {
                if (viewDimension.getWidth() > 0 && viewDimension.getHeight() > 0)
                    modelViewTransform2D.setViewBounds(new Rectangle2D.Double(0, 0, viewDimension.getWidth(), viewDimension.getHeight()));
            }
        };
        mvtUpdate.update();
        viewDimension.addObserver(mvtUpdate);

        int numDomainMarks = 10;
        Function.LinearFunction domainFunction = new Function.LinearFunction(0, numDomainMarks, dataModelBounds.getX(), dataModelBounds.getMaxX());
        ArrayList<DomainTickMark> domainTickMarks = new ArrayList<DomainTickMark>();
        for (int i = 0; i < numDomainMarks + 1; i++) {
            final double x = domainFunction.evaluate(i);
            final DomainTickMark tickMark = new DomainTickMark(x);
            domainTickMarks.add(tickMark);
            chartContents.addChild(tickMark);

            DomainGridLine gridLine = new DomainGridLine(x, this);
            chartContents.addChild(gridLine);

            SimpleObserver domainTickMarkUpdate = new SimpleObserver() {
                public void update() {
                    Point2D location = modelToView(new TimeData(0, x));
                    tickMark.setOffset(location.getX(), viewDimension.getHeight());
                }
            };
            domainTickMarkUpdate.update();
            viewDimension.addObserver(domainTickMarkUpdate);
        }
        DomainTickMark last = domainTickMarks.get(domainTickMarks.size() - 1);
        last.setTickText(last.getTickText() + " sec");

        int numRangeMarks = 4;
        Function.LinearFunction rangeFunction = new Function.LinearFunction(0, numRangeMarks, dataModelBounds.getY(), dataModelBounds.getMaxY());
        for (int i = 0; i < numRangeMarks + 1; i++) {
            final double y = rangeFunction.evaluate(i);
            final RangeTickMark tickMark = new RangeTickMark(y);
            chartContents.addChild(tickMark);

            RangeGridLine gridLine = new RangeGridLine(y, this);
            chartContents.addChild(gridLine);

            SimpleObserver rangeTickMarkUpdate = new SimpleObserver() {
                public void update() {
                    Point2D location = modelToView(new TimeData(y, 0));
                    tickMark.setOffset(0, location.getY());
                }
            };
            rangeTickMarkUpdate.update();
            viewDimension.addObserver(rangeTickMarkUpdate);
        }

        final int iconButtonInset = 2;
        {
            minimizeButton = new PImage(PhetCommonResources.getImage(PhetCommonResources.IMAGE_MINIMIZE_BUTTON)) {
                public void setVisible(boolean isVisible) {
                    super.setVisible(isVisible);
                    setPickable(isVisible);
                }
            };
            addChild(minimizeButton);
            minimizeButton.addInputEventListener(new CursorHandler());
            minimizeButton.addInputEventListener(new PBasicInputEventHandler() {
                public void mouseReleased(PInputEvent event) {
                    maximized.setValue(false);
                }
            });
            SimpleObserver locationUpdate = new SimpleObserver() {
                public void update() {
                    minimizeButton.setOffset(viewDimension.getWidth() - minimizeButton.getFullBounds().getWidth() - iconButtonInset, iconButtonInset);
                }
            };
            locationUpdate.update();
            viewDimension.addObserver(locationUpdate);
        }
        {
            maximizeButton = new PImage(PhetCommonResources.getImage(PhetCommonResources.IMAGE_MAXIMIZE_BUTTON)) {
                public void setVisible(boolean isVisible) {
                    super.setVisible(isVisible);
                    setPickable(isVisible);
                }
            };
            addChild(maximizeButton);
            maximizeButton.addInputEventListener(new CursorHandler());
            maximizeButton.addInputEventListener(new PBasicInputEventHandler() {
                public void mouseReleased(PInputEvent event) {
                    maximized.setValue(true);
                }
            });
            SimpleObserver locationUpdate = new SimpleObserver() {
                public void update() {
                    maximizeButton.setOffset(viewDimension.getWidth() - maximizeButton.getFullBounds().getWidth() - iconButtonInset, iconButtonInset);
                }
            };
            locationUpdate.update();
            viewDimension.addObserver(locationUpdate);
        }
        final SimpleObserver observer = new SimpleObserver() {
            public void update() {
                minimizeButton.setVisible(maximized.getValue());
                chartContents.setVisible(maximized.getValue());
                maximizeButton.setVisible(!maximized.getValue());
            }
        };
        maximized.addObserver(observer);
        observer.update();
    }

    public MutableBoolean getMaximized() {
        return maximized;
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

    public void addChartChild(PNode child) {
        chartContents.addChild(child);
    }

    public static class DomainTickMark extends PNode {
        private PText text;

        public DomainTickMark(double x) {
            double tickWidth = 1.0;
            PhetPPath tick = new PhetPPath(new Rectangle2D.Double(-tickWidth / 2, 0, tickWidth, 4), Color.black);
            addChild(tick);
            text = new PText(new DecimalFormat("0").format(x));
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
        private DomainGridLine(final double x, final MovingManChart chart) {
            final PhetPPath tick = new PhetPPath(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[]{10, 3}, 0), Color.lightGray);
            final SimpleObserver so = new SimpleObserver() {
                public void update() {
                    final double chartX = chart.modelToView(new TimeData(0, x)).getX();
                    tick.setPathTo(new Line2D.Double(chartX, chart.viewDimension.getHeight(), chartX, 0));
                }
            };
            so.update();
            chart.viewDimension.addObserver(so);
            addChild(tick);
        }
    }

    private static class RangeGridLine extends PNode {
        private RangeGridLine(final double y, final MovingManChart chart) {
            final PhetPPath tick = new PhetPPath(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[]{10, 3}, 0), Color.lightGray);
            addChild(tick);
            final SimpleObserver pathUpdate = new SimpleObserver() {
                public void update() {
                    final double chartY = chart.modelToView(new TimeData(y, 0)).getY();
                    tick.setPathTo(new Line2D.Double(0, chartY, chart.viewDimension.getWidth(), chartY));
                }
            };
            chart.viewDimension.addObserver(pathUpdate);
            pathUpdate.update();
        }
    }

    public void addDataSeries(MovingManDataSeries dataSeries, Color color, int numPointsToSkip) {
        chartContents.addChild(new LineSeriesNode(dataSeries, color, numPointsToSkip));
    }

    public Point2D modelToView(TimeData point) {
        return modelViewTransform2D.modelToViewDouble(point.getTime(), point.getValue());
    }

    private class LineSeriesNode extends PNode {
        public LineSeriesNode(final MovingManDataSeries dataSeries, Color color, final int numPointsToSkip) {
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
            dataSeries.addListener(new MovingManDataSeries.Listener() {
                public void entireSeriesChanged() {
                    System.out.println("MovingManChart$LineSeriesNode.entireSeriesChanged");
                    TimeData[] points = dataSeries.getData();

                    DoubleGeneralPath generalPath = new DoubleGeneralPath();
                    for (int i = 0; i < points.length - numPointsToSkip; i++) {
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
            });
        }
    }
}