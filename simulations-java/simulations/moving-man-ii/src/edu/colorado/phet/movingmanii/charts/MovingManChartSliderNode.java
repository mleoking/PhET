package edu.colorado.phet.movingmanii.charts;

import edu.colorado.phet.common.motion.model.TimeData;
import edu.colorado.phet.movingmanii.view.MovingManSliderNode;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Sam Reid
 */
public class MovingManChartSliderNode extends MovingManSliderNode {
    private final MovingManChart movingManChart;

    public MovingManChartSliderNode(MovingManChart movingManChart, final PNode sliderThumb, Color highlightColor) {
        super(sliderThumb, highlightColor);
        this.movingManChart = movingManChart;
        movingManChart.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                updateLayout();
            }
        });
        updateLayout();
    }

    protected double getMaxRangeValue() {
        return movingManChart.getMaxRangeValue();
    }

    protected double getMinRangeValue() {
        return movingManChart.getMinRangeValue();
    }

    protected Rectangle2D getDataArea() {
        return new Rectangle2D.Double(0, 0, movingManChart.dataAreaWidth, movingManChart.dataAreaHeight);
    }

    protected Point2D plotToNode(Point2D.Double aDouble) {
        return movingManChart.modelToView(new TimeData(aDouble.getY(), aDouble.getX()));
    }

    protected void updateLayout() {
        Rectangle2D dataArea = getDataArea();
        trackPPath.setPathTo(new Rectangle2D.Double(0, dataArea.getY(), 5, dataArea.getHeight()));
        updateThumbLocation();
    }

    protected void updateThumbLocation() {
        Point2D nodeLocation = plotToNode(new Point2D.Double(0, clamp(value)));
        sliderThumb.setOffset(trackPPath.getFullBounds().getCenterX() - sliderThumb.getFullBounds().getWidth() / 2.0,
                nodeLocation.getY() - sliderThumb.getFullBounds().getHeight() / 2.0);
    }
}
