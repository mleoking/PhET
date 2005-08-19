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
import edu.colorado.phet.common.view.ApparatusPanel2;

import java.awt.*;
import java.util.HashMap;

/**
 * CurrentVsVoltageGraph
 * <p/>
 * A Chart that shows plots of current against voltage, parameterized by wavelength.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CurrentVsVoltageGraph extends Chart {

    static private Range2D range = new Range2D( -0.07, 0, 0.07, 0.2 );
    static Dimension chartSize = new Dimension( 200, 150 );

    // A map of data sets, keyed by the wavelength each corresponds to
    private HashMap wavelengthToDataSetMap = new HashMap();

    public CurrentVsVoltageGraph( Component component ) {
        super( component, range, chartSize );

        // Test data
//        DataSet data = new DataSet();
//        data.addPoint( .01, .02 );
//        data.addPoint( .03, .03 );
//        LinePlot plot = new LinePlot( component, this, data );
//        this.addDataSetGraphic( plot );
    }

    /**
     * Returns the dataset for a specified wavelength. If there is no dataset exists,
     * one is created.
     *
     * @param wavelength
     * @return
     */
    public DataSet getWavelengthDataset( double wavelength ) {
        Double wavelengthObj = new Double( wavelength );
        SortedDataSet dataSet = (SortedDataSet)wavelengthToDataSetMap.get( wavelengthObj );

        // If we don't already have a plot for this wavelength, create one, and
        // give it a color corresponding to its wavelength
        if( wavelengthToDataSetMap.get( wavelengthObj ) == null ) {
            dataSet = new SortedDataSet();
            wavelengthToDataSetMap.put( wavelengthObj, dataSet );
            LinePlot plot = new LinePlot( getComponent(), this, dataSet );
            ScatterPlot points = new ScatterPlot( getComponent(), this, dataSet );
            Color color = VisibleColor.wavelengthToColor( wavelength );
            color = Color.red;
            plot.setBorderColor( color);
            this.addDataSetGraphic( plot );
            this.addDataSetGraphic( points );
        }
        return dataSet;
    }

    /**
     * Adds a data point for a specified wavelength
     *
     * @param voltage
     * @param current
     * @param wavelength
     */
    public void addDataPoint( double voltage, double current, double wavelength ) {
        DataSet dataSet = getWavelengthDataset( wavelength );
        dataSet.addPoint( voltage, current );
    }
}
