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
import edu.colorado.phet.photoelectric.model.PhotoelectricTarget;
import edu.colorado.phet.lasers.model.PhysicsUtil;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * CurrentVsVoltageGraph
 * <p/>
 * A Chart that shows plots of initial kinetic energy of electrons against frequency of the
 * light coming from the beam.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class EnergyVsFrequencyGraph extends Chart {

    //-----------------------------------------------------------------
    // Class data
    //-----------------------------------------------------------------
    static private Range2D range = new Range2D( PhysicsUtil.wavelengthToFrequency( PhotoelectricModel.MAX_WAVELENGTH ) * 1.2,
                                                0,
                                                PhysicsUtil.wavelengthToFrequency( PhotoelectricModel.MIN_WAVELENGTH ) * 1.2,
                                                PhysicsUtil.wavelengthToEnergy( PhotoelectricModel.MIN_WAVELENGTH));
    static private Dimension chartSize = new Dimension( 200, 150 );

    //-----------------------------------------------------------------
    // Instance data
    //-----------------------------------------------------------------

    private DataSet dataSet = new DataSet();

    //-----------------------------------------------------------------
    // Instance methods
    //-----------------------------------------------------------------

    public EnergyVsFrequencyGraph( Component component, final PhotoelectricModel model ) {
        super( component, range, chartSize );

        GridLineSet horizontalGls = this.getHorizonalGridlines();
        horizontalGls.setMajorTickSpacing( 1E14 );
//        horizontalGls.setMajorTickSpacing( 0.25 );
        horizontalGls.setMajorGridlinesColor( new Color( 200, 200, 200 ));
//        Axis xAxis = new Axis( this);


        GridLineSet verticalGls = this.getVerticalGridlines();
        verticalGls.setMajorTickSpacing( 1E14 );
        verticalGls.setMajorGridlinesColor( new Color( 200, 200, 200 ));

        Color color = Color.blue;
        ScatterPlot points = new ScatterPlot( getComponent(), this, dataSet, color, 4 );
        this.addDataSetGraphic( points );

        model.addChangeListener( new PhotoelectricModel.ChangeListener() {
            public void currentChanged( PhotoelectricModel.ChangeEvent event ) {
                // noop
            }

            public void voltageChanged( PhotoelectricModel.ChangeEvent event ) {
                updateGraph( model);
            }

            public void wavelengthChanged( PhotoelectricModel.ChangeEvent event ) {
                updateGraph( model);
            }
        } );
    }

    private void updateGraph( PhotoelectricModel model ) {
        double frequency = PhysicsUtil.wavelengthToFrequency( model.getWavelength() );
        double workFunction = ((Double)PhotoelectricTarget.WORK_FUNCTIONS.get( model.getTarget().getMaterial() )).doubleValue();
        double energy = Math.max( 0, PhysicsUtil.wavelengthToEnergy( model.getWavelength() ) - workFunction );
        addDataPoint( frequency, energy );
    }

    /**
     * Adds a data point for a specified wavelength
     *
     * @param frequency
     * @param energy
     */
    public void addDataPoint( double frequency, double energy ) {
        dataSet.clear();
        dataSet.addPoint( frequency, energy );
    }

    /**
     * Removes all the data from the graph
     */
    public void clearData() {
        dataSet.clear();
    }
}
