package edu.colorado.phet.common.motion.charts;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Sam Reid
 */
public interface ControlChartLayout {
    void updateLayout(MinimizableControlChart[] charts, double width, double height);

    public class FlowLayout implements ControlChartLayout {
        public void updateLayout(MinimizableControlChart[] charts, double width, double height) {
            new AlignedLayout().updateLayout(charts, width, height);
        }
    }

    public class AlignedLayout implements ControlChartLayout {
        public interface DoubleGetter {
            Double getValue(MinimizableControlChart chart);
        }

        public double getSum(MinimizableControlChart[] charts, DoubleGetter doubleGetter) {
            ArrayList<Double> values = getValues(charts, doubleGetter);
            double sum = 0.0;
            for (Double value : values) {
                sum = sum + value;
            }
            return sum;
        }

        private ArrayList<Double> getValues(MinimizableControlChart[] charts, DoubleGetter doubleGetter) {
            ArrayList<Double> values = new ArrayList<Double>();
            for (MinimizableControlChart chart : charts) {
                values.add(doubleGetter.getValue(chart));
            }
            return values;
        }

        public double getMax(MinimizableControlChart[] charts, DoubleGetter doubleGetter) {
            ArrayList<Double> values = getValues(charts, doubleGetter);
            Collections.sort(values);
            return values.get(values.size() - 1);
        }

        public void updateLayout(MinimizableControlChart[] charts, double width, double height) {
            double maxControlPanelWidth = getMax(charts, new DoubleGetter() {
                public Double getValue(MinimizableControlChart chart) {
                    return chart.getControlPanel().getFullBounds().getWidth();
                }
            });
            double maxSliderWidth = getMax(charts, new DoubleGetter() {
                public Double getValue(MinimizableControlChart chart) {
                    return chart.getSliderNode().getFullBounds().getWidth();
                }
            });
            double maxZoomControlWidth = getMax(charts, new DoubleGetter() {
                public Double getValue(MinimizableControlChart chart) {
                    return chart.getZoomButtonNode().getFullBounds().getWidth();
                }
            });
            double extraLayoutHeightForDomainAxisLabels = getSum(charts, new DoubleGetter() {
                public Double getValue(MinimizableControlChart chart) {
                    return chart.getDomainLabelHeight();
                }
            });
            double chartRangeLabelWidth = 20;//TODO: don't hard code this.
            double chartWidth = width - maxControlPanelWidth - maxSliderWidth - maxZoomControlWidth - maxSliderWidth / 2.0 - chartRangeLabelWidth;
            double chartHeight = (height - extraLayoutHeightForDomainAxisLabels) / charts.length;//TODO: account for insets
            double chartY = 0.0;

            double controlPanelX = 0.0;
            double sliderX = maxControlPanelWidth + controlPanelX + maxSliderWidth / 2.0;
            double chartX = sliderX + maxSliderWidth + chartRangeLabelWidth;
            double zoomControlX = chartX + chartWidth;

            for (MinimizableControlChart chart : charts) {
                chart.setLayoutLocations(controlPanelX, sliderX, chartX, zoomControlX);
                chart.getChartNode().getViewDimension().setDimension(chartWidth, chartHeight);
                chart.setOffset(0, chartY);
                chartY = chartY + chartHeight + chart.getDomainLabelHeight();
                chart.setMinimizeMaximizeButtonOffset(chartX + chartWidth - chart.getMinimizeMaximizeButton().getFullBounds().getWidth() - 4, 4);
            }
        }
    }
}
