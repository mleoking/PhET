package edu.colorado.phet.common.motion.charts;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class MinimizableControlChart extends PNode {
    private ControlChart movingManChart;
    private MinimizeMaximizeButton minimizeMaximizeButton;

    public MinimizableControlChart(final ControlChart movingManChart) {
        this.movingManChart = movingManChart;
        addChild(movingManChart);

        this.minimizeMaximizeButton = new MinimizeMaximizeButton();
        addChild(minimizeMaximizeButton);

        final SimpleObserver updateVisibility = new SimpleObserver() {
            public void update() {
                movingManChart.setVisible(minimizeMaximizeButton.getMaximized().getValue());
            }
        };
        minimizeMaximizeButton.getMaximized().addObserver(updateVisibility);
        updateVisibility.update();
    }

    public MutableBoolean getMaximized() {
        return minimizeMaximizeButton.getMaximized();
    }

    public void setMinimizeMaximizeButtonOffset(double x, double y) {
        minimizeMaximizeButton.setOffset(x, y);
    }

    public PNode getControlPanel() {
        return movingManChart.getControlPanel();
    }

    public PNode getSliderNode() {
        return movingManChart.getSliderNode();
    }

    public PNode getZoomButtonNode() {
        return movingManChart.getZoomButtonNode();
    }

    public Double getDomainLabelHeight() {
        return movingManChart.getDomainLabelHeight();
    }

    public void setLayoutLocations(double controlPanelX, double sliderX, double chartX, double zoomControlX) {
        movingManChart.setLayoutLocations(controlPanelX, sliderX, chartX, zoomControlX);
    }

    public TemporalChart getChartNode() {
        return movingManChart.getChartNode();
    }

    public MinimizeMaximizeButton getMinimizeMaximizeButton() {
        return minimizeMaximizeButton;
    }

    public void setHorizontalZoomVisible(boolean b) {
        movingManChart.getZoomButtonNode().setHorizontalZoomButtonsVisible(b);
    }
}
