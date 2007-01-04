/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.view;

import edu.colorado.phet.chart.*;
import edu.colorado.phet.photoelectric.PhotoelectricConfig;
import edu.colorado.phet.photoelectric.model.PhotoelectricModel;

import java.awt.*;

/**
 * CurrentVsVoltageGraph
 * <p/>
 * A Chart that shows plots of current against voltage, parameterized by wavelength.
 * A separated data set is maintained for each wavelength
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CurrentVsIntensityGraph extends PhotoelectricGraph {

    //-----------------------------------------------------------------
    // Class data
    //-----------------------------------------------------------------
    static private Range2D range = new Range2D(0, 0,
            PhotoelectricModel.MAX_PHOTONS_PER_SECOND,
            PhotoelectricModel.MAX_CURRENT);
    static private Dimension chartSize = PhotoelectricConfig.CHART_SIZE;
    static private double PLOT_LAYER = 1E9;

    //-----------------------------------------------------------------
    // Instance data
    //-----------------------------------------------------------------

    private DataSet dotDataSet = new DataSet();
    private DataSet lineDataSet = new DataSet();

    //-----------------------------------------------------------------
    // Instance methods
    //-----------------------------------------------------------------

    public CurrentVsIntensityGraph(Component component, final PhotoelectricModel model) {
        super(component, range, chartSize, 50, 100, PhotoelectricModel.MAX_CURRENT / 6,
                PhotoelectricModel.MAX_CURRENT / 6);

        GridLineSet horizontalGls = this.getHorizonalGridlines();
        horizontalGls.setMajorGridlinesColor(new Color(200, 200, 200));

        GridLineSet verticalGls = this.getVerticalGridlines();
        verticalGls.setMajorGridlinesColor(new Color(200, 200, 200));

        Color color = new Color(0, 180, 0);
        Color lineColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 80);
        LinePlot lines = new LinePlot(getComponent(), this, lineDataSet, new BasicStroke(3f), lineColor);
        lines.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        this.addDataSetGraphic(lines, PLOT_LAYER);

        // Turn off axis labels
        getXAxis().setMajorTickLabelsVisible(false);
        getVerticalTicks().setMajorTickLabelsVisible(false);
        getVerticalTicks().setMinorTickLabelsVisible(false);
        getYAxis().setMajorTickLabelsVisible(false);
        getHorizontalTicks().setMajorTickLabelsVisible(false);
        getHorizontalTicks().setMinorTickLabelsVisible(false);

        ScatterPlot points = new ScatterPlot(getComponent(), this, dotDataSet, color, PhotoelectricConfig.GRAPH_DOT_RADIUS);
        this.addDataSetGraphic(points, PLOT_LAYER + 1);

        model.addChangeListener(new PhotoelectricModel.ChangeListenerAdapter() {
            public void currentChanged(PhotoelectricModel.ChangeEvent event) {
                addDataPoint(model.getBeam().getPhotonsPerSecond(), model.getCurrent());
            }

            public void voltageChanged(PhotoelectricModel.ChangeEvent event) {
                lineDataSet.clear();
                addDataPoint(model.getBeam().getPhotonsPerSecond(), model.getCurrent());
            }

            public void wavelengthChanged(PhotoelectricModel.ChangeEvent event) {
                lineDataSet.clear();
                addDataPoint(model.getBeam().getPhotonsPerSecond(), model.getCurrent());
            }

            public void targetMaterialChanged(PhotoelectricModel.ChangeEvent event) {
                lineDataSet.clear();
            }

            public void beamIntensityChanged(PhotoelectricModel.ChangeEvent event) {
                addDataPoint(model.getBeam().getPhotonsPerSecond(), model.getCurrent());
            }
        });
    }

    /**
     * Adds a data point for a specified wavelength
     *
     * @param intensity
     * @param current
     */
    public void addDataPoint(double intensity, double current) {
        dotDataSet.clear();
        dotDataSet.addPoint(intensity, current);
        lineDataSet.addPoint(intensity, current);
    }

    /**
     * Removes the line plot from the graph
     */
    public void clearLinePlot() {
        lineDataSet.clear();
    }

    public void clearData() {
        clearLinePlot();
    }
}
