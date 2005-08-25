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
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.photoelectric.model.PhotoelectricModel;
import edu.colorado.phet.photoelectric.PhotoelectricConfig;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * CurrentVsVoltageGraph
 * <p/>
 * A Chart that shows plots of current against voltage, parameterized by wavelength.
 * A separated data set is maintained for each wavelength
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CurrentVsVoltageGraph2 extends Chart {

    //-----------------------------------------------------------------
    // Class data
    //-----------------------------------------------------------------
    static private Range2D range = new Range2D( PhotoelectricModel.MIN_VOLTAGE,
                                                0,
                                                PhotoelectricModel.MAX_VOLTAGE,
                                                PhotoelectricModel.MAX_CURRENT );
    static private Dimension chartSize = new Dimension( 200, 150 );
    static private Font titleFont = new Font( "Lucide Sans", Font.BOLD, 14 );
    private static final double PLOT_LAYER = 1E15;

    //-----------------------------------------------------------------
    // Instance data
    //-----------------------------------------------------------------

    private DataSet dotDataSet = new DataSet();
    private DataSet lineDataSet = new DataSet();
    private double stoppingVoltage;
    private double lastVoltageRecorded;
    private double lastCurrentRecorded;

    //-----------------------------------------------------------------
    // Instance methods
    //-----------------------------------------------------------------

    public CurrentVsVoltageGraph2( Component component, final PhotoelectricModel model ) {
        super( component, range, chartSize, 2, 2, 2, 2 );

        GridLineSet horizontalGls = this.getHorizonalGridlines();
        horizontalGls.setMajorGridlinesColor( new Color( 200, 200, 200 ) );

        GridLineSet verticalGls = this.getVerticalGridlines();
        verticalGls.setMajorGridlinesColor( new Color( 200, 200, 200 ) );

        getYAxis().setMajorTickLabelsVisible( false );

        Color color = Color.red;
        Color lineColor = new Color( color.getRed(), color.getGreen(), color.getBlue(), 80 );
        LinePlot lines = new LinePlot( getComponent(), this, lineDataSet, new BasicStroke( 3f ), lineColor );
        lines.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        this.addDataSetGraphic( lines, PLOT_LAYER );
        ScatterPlot points = new ScatterPlot( getComponent(), this, dotDataSet, color, PhotoelectricConfig.GRAPH_DOT_RADIUS );
        this.addDataSetGraphic( points, PLOT_LAYER );

        model.addChangeListener( new PhotoelectricModel.ChangeListenerAdapter() {
            public void currentChanged( PhotoelectricModel.ChangeEvent event ) {
                addDotDataPoint( model.getVoltage(), model.getCurrent() );
            }

            public void voltageChanged( PhotoelectricModel.ChangeEvent event ) {
                stoppingVoltage = model.getStoppingVoltage();
                addDotDataPoint( model.getVoltage(), model.getCurrent() );
                addLineDataPoint( model.getVoltage(), model.getCurrent() );
            }

            public void wavelengthChanged( PhotoelectricModel.ChangeEvent event ) {
                stoppingVoltage = model.getStoppingVoltage();
                lineDataSet.clear();
                addDotDataPoint( model.getVoltage(), model.getCurrent() );
            }

            public void beamIntensityChanged( PhotoelectricModel.ChangeEvent event ) {
                lineDataSet.clear();
            }

            public void targetMaterialChanged( PhotoelectricModel.ChangeEvent event ) {
                stoppingVoltage = model.getStoppingVoltage();
                lineDataSet.clear();
            }
        } );
    }

    /**
     * Adds a data point for a specified wavelength
     *
     * @param voltage
     * @param current
     */
    public void addDotDataPoint( double voltage, double current ) {
        dotDataSet.clear();
        dotDataSet.addPoint( voltage, current );
    }

    /**
     * Adds a data point for a specified wavelength
     *
     * @param voltage
     * @param current
     */
    public void addLineDataPoint( double voltage, double current ) {
        // Have to do som efancy steppin' here to keep the crossover across the
        // stopping voltage from looking bad
        if(( lastVoltageRecorded < stoppingVoltage) && (voltage > stoppingVoltage )) {
            lineDataSet.addPoint( stoppingVoltage, 0 );
            lineDataSet.addPoint( stoppingVoltage, current );
        }
        else if(( lastVoltageRecorded > stoppingVoltage) && (voltage < stoppingVoltage )) {
            lineDataSet.addPoint( stoppingVoltage, lastCurrentRecorded );
            lineDataSet.addPoint( stoppingVoltage, current );
        }
        lineDataSet.addPoint( voltage, current );
        lastVoltageRecorded = voltage;
        lastCurrentRecorded = current;
    }

    /**
     * Removes the line plot from the graph
     */
    public void clearLinePlot() {
        lineDataSet.clear();
    }
}
