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
import edu.colorado.phet.lasers.model.photon.PhotonSource;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;

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
public class CurrentVsIntensityGraph extends Chart {

    //-----------------------------------------------------------------
    // Class data
    //-----------------------------------------------------------------
    static private Range2D range = new Range2D( 0, 0,
                                                PhotoelectricModel.MAX_PHOTONS_PER_SECOND * 1.1,
                                                PhotoelectricModel.MAX_CURRENT * 1.1 );
//                                                PhotoelectricModel.MAX_PHOTONS_PER_SECOND * 1.1 );
    static private Dimension chartSize = new Dimension( 200, 150 );
    static private Font titleFont = new Font( "Lucide Sans", Font.PLAIN, 12 );

    //-----------------------------------------------------------------
    // Instance data
    //-----------------------------------------------------------------

    private DataSet dataSet = new DataSet();

    //-----------------------------------------------------------------
    // Instance methods
    //-----------------------------------------------------------------

    public CurrentVsIntensityGraph( Component component, final PhotoelectricModel model ) {
        super( component, range, chartSize, 50, 100, 1, 1  );

        GridLineSet horizontalGls = this.getHorizonalGridlines();
        horizontalGls.setMajorGridlinesColor( new Color( 200, 200, 200 ));

        GridLineSet verticalGls = this.getVerticalGridlines();
        verticalGls.setMajorGridlinesColor( new Color( 200, 200, 200 ));

        Color color = new Color( 0, 180, 0 );
        ScatterPlot points = new ScatterPlot( getComponent(), this, dataSet, color, PhotoelectricConfig.GRAPH_DOT_RADIUS );
        this.addDataSetGraphic( points );

        model.getBeam().addRateChangeListener( new PhotonSource.RateChangeListener() {
            public void rateChangeOccurred( CollimatedBeam.RateChangeEvent event ) {
                addDataPoint( event.getRate(), model.getCurrent() );
            }
        } );

        model.addChangeListener( new PhotoelectricModel.ChangeListenerAdapter() {
            public void currentChanged( PhotoelectricModel.ChangeEvent event ) {
                addDataPoint( model.getBeam().getPhotonsPerSecond(),
                              model.getCurrent() );
            }
        } );
    }

    /**
     * Adds a data point for a specified wavelength
     *
     * @param intensity
     * @param current
     */
    public void addDataPoint( double intensity, double current ) {
        dataSet.clear();
        dataSet.addPoint( intensity, current );
    }

    /**
     * Removes all the data from the graph
     */
    public void clearData() {
        dataSet.clear();
    }
}
