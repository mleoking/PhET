package edu.colorado.phet.common.motion.charts;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.movingman.view.MovingManSliderNode;
import edu.colorado.phet.movingman.view.Range;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Sam Reid
 */
public class MovingManChartSliderNode extends MovingManSliderNode.Vertical {
    private final MovingManChart movingManChart;

    public MovingManChartSliderNode(final MovingManChart movingManChart, final PNode sliderThumb, Color highlightColor) {
        super(new Range(movingManChart.getMinRangeValue(), movingManChart.getMaxRangeValue()), 0.0, new Range(0, 100), sliderThumb, highlightColor);
        this.movingManChart = movingManChart;
        movingManChart.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                updateLayoutBasedOnChart();
            }
        });
        updateLayoutBasedOnChart();
        movingManChart.getDataModelBounds().addObserver(new SimpleObserver() {
            public void update() {
                setModelRange(movingManChart.getMinRangeValue(), movingManChart.getMaxRangeValue());
            }
        });
    }

    public void updateLayoutBasedOnChart() {
        setViewRange(0, movingManChart.getViewDimension().getHeight());
    }
}