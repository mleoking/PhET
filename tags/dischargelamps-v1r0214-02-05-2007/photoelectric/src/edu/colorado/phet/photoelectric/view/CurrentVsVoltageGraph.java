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
import edu.colorado.phet.common.math.MathUtil;
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
public class CurrentVsVoltageGraph extends PhotoelectricGraph {

    //-----------------------------------------------------------------
    // Class data
    //-----------------------------------------------------------------
    static private Range2D range = new Range2D( PhotoelectricModel.MIN_VOLTAGE,
                                                0,
                                                PhotoelectricModel.MAX_VOLTAGE,
                                                PhotoelectricModel.MAX_CURRENT );
    static private Dimension chartSize = PhotoelectricConfig.CHART_SIZE;
    private static final double PLOT_LAYER = 1E15;

    //-----------------------------------------------------------------
    // Instance data
    //-----------------------------------------------------------------

    private double lastVoltageRecorded;

    //-----------------------------------------------------------------
    // Instance methods
    //-----------------------------------------------------------------

    public CurrentVsVoltageGraph( Component component, final PhotoelectricModel model ) {
        super( component, range, chartSize, 2, 2, PhotoelectricModel.MAX_CURRENT / 6,
                PhotoelectricModel.MAX_CURRENT / 6 );

        GridLineSet horizontalGls = this.getHorizonalGridlines();
        horizontalGls.setMajorGridlinesColor( new Color( 200, 200, 200 ) );

        GridLineSet verticalGls = this.getVerticalGridlines();
        verticalGls.setMajorGridlinesColor( new Color( 200, 200, 200 ) );

        // Turn off axis labels
        getVerticalTicks().setMajorTickLabelsVisible( false );
        getVerticalTicks().setMinorTickLabelsVisible( false );
        getYAxis().setMajorTickLabelsVisible( false );

        Color color = Color.red;
        Color lineColor = new Color( color.getRed(), color.getGreen(), color.getBlue(), 80 );
        LinePlot lines = new LinePlot( getComponent(), this, getLineDataSet(), new BasicStroke( 3f ), lineColor );
        lines.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        this.addDataSetGraphic( lines, PLOT_LAYER );
        ScatterPlot points = new ScatterPlot( getComponent(), this, getDotDataSet(), color, PhotoelectricConfig.GRAPH_DOT_RADIUS );
        this.addDataSetGraphic( points, PLOT_LAYER );

        model.addChangeListener( new PhotoelectricModel.ChangeListenerAdapter() {
            public void currentChanged( PhotoelectricModel.ChangeEvent event ) {
                setDotDataPoint( model.getVoltage(), model.getCurrent() );
            }

            public void voltageChanged( PhotoelectricModel.ChangeEvent event ) {
                setDotDataPoint( model.getVoltage(), model.getCurrent() );
                addLineDataPoint( model.getVoltage(), model.getCurrent(), model );
            }

            public void wavelengthChanged( PhotoelectricModel.ChangeEvent event ) {
                clearLinePlot();
                setDotDataPoint( model.getVoltage(), model.getCurrent() );
            }

            public void beamIntensityChanged( PhotoelectricModel.ChangeEvent event ) {
                clearLinePlot();
            }

            public void targetMaterialChanged( PhotoelectricModel.ChangeEvent event ) {
                clearLinePlot();
            }
        } );
    }

    /**
     * Adds a data point for a specified wavelength
     *
     * @param voltage
     * @param current
     */
    private void addLineDataPoint( double voltage, double current, PhotoelectricModel model ) {
        // Do some shenanigans to handle moving too quickly through the stopping voltage
        double dv = 0.1 * MathUtil.getSign( voltage - lastVoltageRecorded );
        for( double v = lastVoltageRecorded + dv; Math.abs( v - voltage ) > Math.abs( dv ); v += dv ) {
            getLineDataSet().addPoint( v, model.getCurrentForVoltage( v ) );
        }
        getLineDataSet().addPoint( voltage, current );
        lastVoltageRecorded = voltage;
    }
}
