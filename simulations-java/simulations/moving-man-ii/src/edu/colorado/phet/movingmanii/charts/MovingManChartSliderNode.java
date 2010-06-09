package edu.colorado.phet.movingmanii.charts;

import edu.colorado.phet.movingmanii.view.MovingManSliderNode;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Sam Reid
 */
public class MovingManChartSliderNode extends MovingManSliderNode.Vertical {
    private final MovingManChart movingManChart;

    public MovingManChartSliderNode(MovingManChart movingManChart, final PNode sliderThumb, Color highlightColor) {
        super(new Range(movingManChart.getMinRangeValue(), movingManChart.getMaxRangeValue()), 0.0, new Range(0, 100), sliderThumb, highlightColor);
        this.movingManChart = movingManChart;
        movingManChart.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                updateLayoutBasedOnChart();
            }
        });
        updateLayoutBasedOnChart();
    }

    public void updateLayoutBasedOnChart() {
        setViewRange(0, movingManChart.dataAreaHeight);
    }
}