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

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * CurrentVsVoltageGraph
 * <p/>
 * A Chart that shows plots of current against voltage, parameterized by wavelength.
 * A separated data set is maintained for each wavelength
 * <p/>
 * The graph operates in two modes. In the EMPIRICAL_LINE mode, the graph displays line plots
 * In the ANALYTICAL_SPOT mode, the graph displays a single spot a a specified point.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CurrentVsVoltageGraph2 extends Chart {

    //-----------------------------------------------------------------
    // Class data
    //-----------------------------------------------------------------
    static private Range2D range = new Range2D( PhotoelectricModel.MIN_VOLTAGE * 1.2, 0,
                                                PhotoelectricModel.MAX_VOLTAGE * 1.2, 0.2 );
    static private Dimension chartSize = new Dimension( 200, 150 );
    static private Font titleFont = new Font( "Lucide Sans", Font.PLAIN, 12 );

    //-----------------------------------------------------------------
    // Instance data
    //-----------------------------------------------------------------

    private DataSet dataSet = new DataSet();

    //-----------------------------------------------------------------
    // Instance methods
    //-----------------------------------------------------------------

    public CurrentVsVoltageGraph2( Component component ) {
        super( component, range, chartSize );

        GridLineSet horizontalGls = this.getHorizonalGridlines();
        horizontalGls.setMajorTickSpacing( 0.025 );
        horizontalGls.setMajorGridlinesColor( new Color( 200, 200, 200 ));

        GridLineSet verticalGls = this.getVerticalGridlines();
        verticalGls.setMinorTickSpacing( 2.0 );
        verticalGls.setMajorGridlinesColor( new Color( 200, 200, 200 ));

//        super.setVerticalTitle( "Current", Color.black, titleFont );
//        super.setXAxisTitle(  new PhetTextGraphic( component, titleFont, "Voltage", Color.black ));
        Color color = Color.red;
        ScatterPlot points = new ScatterPlot( getComponent(), this, dataSet, color, 4 );
        this.addDataSetGraphic( points );
    }

    /**
     * Adds a data point for a specified wavelength
     *
     * @param voltage
     * @param current
     * @param wavelength
     */
    public void addDataPoint( double voltage, double current, double wavelength ) {
        dataSet.clear();
        dataSet.addPoint( voltage, current );
    }

    /**
     * Removes all the data from the graph
     */
    public void clearData() {
        dataSet.clear();
    }
}
