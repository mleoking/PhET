package edu.colorado.phet.common.motion.charts;

import edu.umd.cs.piccolo.PNode;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Layouts for MinimizableControlCharts
 *
 * @author Sam Reid
 */
public interface ControlChartLayout {
    void updateLayout(MinimizableControlChart[] charts, double width, double height);

    /**
     * This layout ensures that different components in the chart line up vertically,
     * and that all of the vertical space is used.
     */
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
            double controlPanelWidth = getMax(charts, new DoubleGetter() {
                public Double getValue(MinimizableControlChart chart) {
                    return chart.getControlPanel().getFullBounds().getWidth();
                }
            });
            double sliderWidth = getMax(charts, new DoubleGetter() {
                public Double getValue(MinimizableControlChart chart) {
                    return chart.getSliderNode().getFullBounds().getWidth();
                }
            });
            double sliderInternalInsetX = getMax(charts,new DoubleGetter() {
                public Double getValue(MinimizableControlChart chart) {
                    final PNode slider = chart.getSliderNode();
                    if (slider.getFullBounds().getWidth()==0){
                        return 0.0;
                    }
                    else{
                        return slider.getXOffset() - slider.getFullBounds().getX();
                    }
                }
            });
            double zoomControlWidth = getMax(charts, new DoubleGetter() {
                public Double getValue(MinimizableControlChart chart) {
                    return chart.getZoomButtonNode().getFullBounds().getWidth();
                }
            });
            double domainAxisLabelHeight = getMax(charts, new DoubleGetter() {
                public Double getValue(MinimizableControlChart chart) {
                    return chart.getDomainLabelHeight();
                }
            });
            
            double rangeAxisLabelWidth = getMax( charts, new DoubleGetter() {//TODO: need to observe when this changes and update layout
                public Double getValue( MinimizableControlChart chart ) {
                    return chart.getMaxRangeAxisLabelWidth();
                }
            } );

            //Figure out how many charts are visible and how much space the minimized charts will use.
            int numVisibleCharts = 0;
            double minimizedChartSpace = 0;
            for (MinimizableControlChart chart : charts) {
                if (chart.getMaximized().getValue()) {
                    numVisibleCharts++;
                } else {
                    minimizedChartSpace += chart.getMinimizedHeight();
                }
            }
            System.out.println("sliderInternalInsetX = " + sliderInternalInsetX);

            double chartWidth = width - controlPanelWidth - sliderWidth - rangeAxisLabelWidth - zoomControlWidth;
            
            //Determine the X coordinates of the different components
            final double controlPanelX = 0.0;
            final double sliderX = controlPanelX + controlPanelWidth+sliderInternalInsetX;
            final double zoomControlX = width-zoomControlWidth;
            final double chartX = zoomControlX - chartWidth;//This value accounts for the fact that the range axis labels are not exactly right justified against the chart//TODO: don't hard code this
            

            //Compute the vertical location and spacing
            final int paddingBetweenCharts = 8;
            final int totalPaddingBetweenCharts = paddingBetweenCharts * (charts.length - 1);
            final double maximizedChartHeight = numVisibleCharts == 0 ? 0 : 
                    (height  - minimizedChartSpace - totalPaddingBetweenCharts) / numVisibleCharts - domainAxisLabelHeight;//TODO: account for insets
            double chartY = 0.0;
            
            //Update the chart sizes and locations
            for (MinimizableControlChart chart : charts) {
                chart.setLayoutLocations(controlPanelX, sliderX, chartX, zoomControlX);
                chart.getChartNode().getViewDimension().setDimension(chartWidth, maximizedChartHeight);
                chart.setOffset(0, chartY);
                chart.setMinimizeMaximizeButtonOffset(chartX + chartWidth - chart.getMinimizeMaximizeButton().getFullBounds().getWidth() - 4, 0);

                //identify the location of the next chart
                double currentChartHeight = (chart.getMaximized().getValue() ? maximizedChartHeight + chart.getDomainLabelHeight() : chart.getMinimizedHeight());
                chartY += paddingBetweenCharts + currentChartHeight;
            }
        }
    }
}
