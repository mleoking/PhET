package edu.colorado.phet.movingman.view;

import edu.colorado.phet.common.motion.charts.TemporalChart;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.PNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * This node handles layout of a list of TemporalCharts
 *
 * @author Sam Reid
 */
public class MultiChart extends PNode {
    private ArrayList<TemporalChart> charts = new ArrayList<TemporalChart>();

    public MultiChart(TemporalChart... temporalCharts) {
        charts.addAll(Arrays.asList(temporalCharts));
        for (TemporalChart temporalChart : temporalCharts) {
            addChild(temporalChart);
        }

        /**
         * Synchronize the chart domains.
         */
        for (final TemporalChart chart : charts) {
            chart.getDataModelBounds().addObserver(new SimpleObserver() {
                public void update() {
                    for (TemporalChart temporalChart : charts) {
                        if (temporalChart != chart) {
                            temporalChart.getDataModelBounds().setHorizontalRange(chart.getDataModelBounds().getMinX(), chart.getDataModelBounds().getMaxX());
                        }
                    }
                }
            });
        }

        //Only show the topmost horizontal zoom button
        SimpleObserver updateHorizontalZoomVisibility = new SimpleObserver() {
            public void update() {
                for (int i = 0; i < charts.size(); i++) {
                    if (charts.get(i).getMaximized().getValue()) {
                        showHorizontalZoomButtonsInChart(i);
                        break;
                    }
                }
            }
        };
        for (TemporalChart chart : charts) {
            chart.getMaximized().addObserver(updateHorizontalZoomVisibility);
        }
        updateHorizontalZoomVisibility.update();
    }

    private void showHorizontalZoomButtonsInChart(int i) {
        for (int k = 0; k < charts.size(); k++) {
            charts.get(k).setHorizontalZoomButtonsVisible(i == k);
        }
    }

    public void setSize(double width, double height) {
        //charts in a multichart should have the same number of control components for alignment
        final int numControlComponents = charts.get(0).getControlPanel().getChildrenCount();
        for (int i = 0; i < numControlComponents; i++) {
            setControlComponentX(i, i == 0 ? 0.0 : getMaxControlComponentWidth(i - 1));
        }

        double maxChartControlWidth = getMaxChartControlWidth();
        final double chartX = maxChartControlWidth + 30;
        final double chartsY = 0;
        double availableHeightForCharts = height - chartsY;
        double verticalSpacePerChart = availableHeightForCharts / charts.size();
        double roomForZoomButtons = charts.get(0).getZoomControlWidth();//todo: assumes all the same width
        double chartWidth = width - chartX - roomForZoomButtons;
        double chartHeight = verticalSpacePerChart - 24;//for inset

        double dy = 0;
        for (TemporalChart chart : charts) {
            chart.setOffset(0, chartsY + dy);
            chart.setViewDimension(chartWidth, chartHeight);
            dy = dy + verticalSpacePerChart;
        }
    }

    private void setControlComponentX(int component, double x) {
        for (TemporalChart chart : charts) {
            chart.getControlNode(component).setOffset(x, 0);
        }
    }

    private double getMaxControlComponentWidth(int i) {
        ArrayList<Double> sizes = new ArrayList<Double>();
        for (TemporalChart chart : charts) {
            double component = chart.getControlPanel().getChild(i).getFullBounds().getWidth();
            sizes.add(component);
        }

        Collections.sort(sizes);
        return sizes.get(sizes.size() - 1).doubleValue();
    }

    private double getMaxChartControlWidth() {
        double[] chartControlWidths = new double[charts.size()];
        for (int i = 0; i < chartControlWidths.length; i++) {
            chartControlWidths[i] = charts.get(i).getControlPanel().getFullBounds().getWidth();
        }
        Arrays.sort(chartControlWidths);
        double maxChartControlWidth = chartControlWidths[chartControlWidths.length - 1];
        return maxChartControlWidth;
    }
}
