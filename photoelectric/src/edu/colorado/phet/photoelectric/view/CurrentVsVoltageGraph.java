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
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CurrentVsVoltageGraph extends Chart {

    static private Range2D range = new Range2D( PhotoelectricModel.MIN_VOLTAGE * 1.2, 0,
                                                PhotoelectricModel.MAX_VOLTAGE * 1.2, 0.2 );
    static Dimension chartSize = new Dimension( 200, 150 );

    // A map of data sets, keyed by the wavelength each corresponds to
    private HashMap wavelengthToDataSetMap = new HashMap();

    public CurrentVsVoltageGraph( Component component ) {
        super( component, range, chartSize );
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
            Color color = VisibleColor.wavelengthToColor( wavelength );
            // For some reason, [r,g,b] = [0,0,0] doesn't work for the Charts plot
            if( color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0 ) {
                color = new Color( 1, 1, 11);
            }
            LinePlot plot = new LinePlot( getComponent(), this, dataSet );
            ScatterPlot points = new ScatterPlot( getComponent(), this, dataSet, color, 2 );

            plot.setBorderColor( color );
            plot.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
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

    /**
     * Removes all the data from the graph
     */
    public void clearData() {
        Collection dataSets = wavelengthToDataSetMap.values();
        for( Iterator iterator = dataSets.iterator(); iterator.hasNext(); ) {
            DataSet dataSet = (DataSet)iterator.next();
            dataSet.clear();
        }
        wavelengthToDataSetMap.clear();
        repaint();
    }
}
