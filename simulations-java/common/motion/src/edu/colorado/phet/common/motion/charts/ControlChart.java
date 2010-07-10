package edu.colorado.phet.common.motion.charts;

import edu.umd.cs.piccolo.PNode;

/**
 * This contains a control panel, slider, chart and zoom controls.
 *
 * @author Sam Reid
 */
public class ControlChart extends PNode {
    private PNode controlPanel;
    private PNode sliderNode;
    private TemporalChart chartNode;
    private ChartZoomControlNode zoomButtonNode;

    public ControlChart(PNode controlPanel, PNode sliderNode, TemporalChart chartNode, ChartZoomControlNode zoomButtonNode) {
        this.controlPanel = controlPanel;
        this.sliderNode = sliderNode;
        this.chartNode = chartNode;
        this.zoomButtonNode = zoomButtonNode;

        addChild(controlPanel);
        addChild(sliderNode);
        addChild(chartNode);
        addChild(zoomButtonNode);

        controlPanel.setOffset(0, 0);
        sliderNode.setOffset(controlPanel.getFullBounds().getMaxX(), 0);
        chartNode.setOffset(sliderNode.getFullBounds().getMaxX(), 0);
        zoomButtonNode.setOffset(chartNode.getFullBounds().getMaxX(), 0);
    }

    public PNode getControlPanel() {
        return controlPanel;
    }

    public PNode getSliderNode() {
        return sliderNode;
    }

    public TemporalChart getChartNode() {
        return chartNode;
    }

    public ChartZoomControlNode getZoomButtonNode() {
        return zoomButtonNode;
    }

    public void setLayoutLocations(double controlPanelX, double sliderX, double chartX, double zoomControlX) {
        controlPanel.setOffset(controlPanelX, 0);
        sliderNode.setOffset(sliderX, 0);
        chartNode.setOffset(chartX, 0);
        zoomButtonNode.setOffset(zoomControlX, 0);
    }

    public double getDomainLabelHeight() {
        return chartNode.getDomainLabelHeight();
    }

    public void setDomainAxisLabelsVisible(boolean b) {
        chartNode.setDomainAxisLabelsVisible(b);
    }

    public void reset() {
        chartNode.reset();
    }
}