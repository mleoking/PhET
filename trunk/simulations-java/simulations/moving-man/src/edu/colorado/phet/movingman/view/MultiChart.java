package edu.colorado.phet.movingman.view;

import edu.colorado.phet.common.motion.charts.TemporalChart;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.PNode;

import java.util.ArrayList;
import java.util.Arrays;

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
        double[] chartWidths = new double[charts.size()];
        for (int i = 0; i < chartWidths.length; i++) {
            chartWidths[i] = charts.get(i).getControlNode().getFullBounds().getWidth();
        }
        Arrays.sort(chartWidths);
        double maxChartControlWidth = chartWidths[chartWidths.length - 1];
        double padding = 75;//width of slider plus graph range labels
        final double chartX = maxChartControlWidth + padding;
        final double chartsY = 0;
        double availableHeightForCharts = height - chartsY;
        double sizePerChart = availableHeightForCharts / 3;
        double roomForZoomButtons = charts.get(0).getZoomControlWidth();//todo: assumes all the same width
        double chartWidth = width - chartX - roomForZoomButtons;
        double chartHeight = sizePerChart - 24;//for inset

        double dy = 0;
        for (TemporalChart chart : charts) {
            chart.setOffset(chartX, chartsY + dy);
            chart.setViewDimension(chartWidth, chartHeight);
            dy = dy + sizePerChart;
        }
    }
}
