package edu.colorado.phet.common.motion.charts;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class MinimizableControlChart extends PNode {
    private ControlChart controlChart;
    private MinimizeMaximizeButton minimizeMaximizeButton;

    public MinimizableControlChart(String title, final ControlChart controlChart) {
        this.controlChart = controlChart;
        addChild(controlChart);

        this.minimizeMaximizeButton = new MinimizeMaximizeButton(title);
        addChild(minimizeMaximizeButton);

        final SimpleObserver updateVisibility = new SimpleObserver() {
            public void update() {
                controlChart.setVisible(minimizeMaximizeButton.getMaximized().getValue());
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
        return controlChart.getControlPanel();
    }

    public PNode getSliderNode() {
        return controlChart.getSliderNode();
    }

    public PNode getZoomButtonNode() {
        return controlChart.getZoomButtonNode();
    }

    public Double getDomainLabelHeight() {
        return controlChart.getDomainLabelHeight();
    }

    public void setLayoutLocations(double controlPanelX, double sliderX, double chartX, double zoomControlX) {
        controlChart.setLayoutLocations(controlPanelX, sliderX, chartX, zoomControlX);
    }

    public TemporalChart getChartNode() {
        return controlChart.getChartNode();
    }

    public MinimizeMaximizeButton getMinimizeMaximizeButton() {
        return minimizeMaximizeButton;
    }

    public void setHorizontalZoomVisible(boolean b) {
        controlChart.getZoomButtonNode().setHorizontalZoomButtonsVisible(b);
    }

    public void centerVerticalZoomButtons() {
        controlChart.centerVerticalZoomButtons();
    }

    public double getMinimizedHeight() {
        return minimizeMaximizeButton.getFullBounds().getHeight();
    }

    public void setDomainAxisLabelsVisible(boolean b) {
        controlChart.setDomainAxisLabelsVisible(b);
    }
}
