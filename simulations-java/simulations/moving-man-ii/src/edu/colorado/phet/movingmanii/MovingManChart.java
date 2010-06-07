package edu.colorado.phet.movingmanii;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Working directly with JFreeChart has been problematic for the motion-series sim since we need more flexibility + performance than it provides.
 * We need the following features:
 * 1. Ability to show labels on each chart when the horizontal axis is shared
 * 2. Ability to position controls within or next to the chart
 * 3. Ability to draw directly on the chart.
 * 4. Ability to line up charts with each other and minimize to put "expand" buttons between charts
 * 4. Improved performance.
 */
public class MovingManChart extends PNode {
    private Rectangle2D.Double dataModelBounds;
    private double dataAreaWidth;
    private double dataAreaHeight;

    public MovingManChart(Rectangle2D.Double dataModelBounds, double dataAreaWidth,//Width of the chart area
                          double dataAreaHeight) {
        this.dataModelBounds = dataModelBounds;
        this.dataAreaWidth = dataAreaWidth;
        this.dataAreaHeight = dataAreaHeight;
        PhetPPath background = new PhetPPath(new Rectangle2D.Double(0, 0, dataAreaWidth, dataAreaHeight), Color.yellow, new BasicStroke(1), Color.black);
        addChild(background);
    }

    public void addDataSeries(MovingManDataSeries dataSeries) {
        addChild(new LineSeriesNode(dataSeries));
//        serieses.add(dataSeries);
    }

    private class LineSeriesNode extends PNode {
        private PhetPPath path;

        public LineSeriesNode(final MovingManDataSeries dataSeries) {
            path = new PhetPPath(new BasicStroke(2), Color.black);
            addChild(path);
            dataSeries.addListener(new MovingManDataSeries.Listener() {
                public void changed() {
                    Point2D[] points = dataSeries.getData();

                    DoubleGeneralPath generalPath = new DoubleGeneralPath();
                    for (int i = 0; i < points.length; i++) {
                        Point2D mapped = map(points[i]);
//                        System.out.println("mapped from " + mapped);
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

        private Point2D map(Point2D point) {
            ModelViewTransform2D modelViewTransform2D = new ModelViewTransform2D(dataModelBounds, new Rectangle2D.Double(0, 0, dataAreaWidth, dataAreaHeight));
            return modelViewTransform2D.modelToViewDouble(point);
        }
    }
}