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
import edu.colorado.phet.photoelectric.PhotoelectricConfig;
import edu.colorado.phet.lasers.model.PhysicsUtil;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.text.DecimalFormat;

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
    static private Range2D range = new Range2D( 0,
//    static private Range2D range = new Range2D( PhysicsUtil.wavelengthToFrequency( PhotoelectricModel.MAX_WAVELENGTH ),
                                                0,
                                                PhysicsUtil.wavelengthToFrequency( PhotoelectricModel.MIN_WAVELENGTH ),
                                                PhysicsUtil.wavelengthToEnergy( PhotoelectricModel.MIN_WAVELENGTH));
    static private double xSpacing = ( range.getMaxX() - range.getMinX() ) / 4;
    static private Dimension chartSize = new Dimension( 200, 150 );

    //-----------------------------------------------------------------
    // Instance data
    //-----------------------------------------------------------------

    private DataSet dotDataSet = new DataSet();
    private DataSet lineDataSet = new DataSet();
    private double kneeFrequency;
    private double lastFrequencyRecorded;

    //-----------------------------------------------------------------
    // Instance methods
    //-----------------------------------------------------------------

    public EnergyVsFrequencyGraph( Component component, final PhotoelectricModel model ) {
        super( component, range, chartSize, xSpacing, xSpacing, 2, 2 );

        GridLineSet horizontalGls = this.getHorizonalGridlines();
        horizontalGls.setMajorGridlinesColor( new Color( 200, 200, 200 ));

        GridLineSet verticalGls = this.getVerticalGridlines();
        verticalGls.setMajorGridlinesColor( new Color( 200, 200, 200 ));

        getHorizontalTicks().setMajorNumberFormat( new DecimalFormat( "0.#E0" ));

        this.getXAxis().setNumberFormat( new DecimalFormat( "0.#E0" ));

        Color color = Color.blue;
        Color lineColor = new Color( color.getRed(), color.getGreen(), color.getBlue(), 80 );
        LinePlot lines = new LinePlot( getComponent(), this, lineDataSet, new BasicStroke( 3f ), lineColor );
        lines.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        this.addDataSetGraphic( lines );

        ScatterPlot points = new ScatterPlot( getComponent(), this, dotDataSet, color, PhotoelectricConfig.GRAPH_DOT_RADIUS );
        this.addDataSetGraphic( points );

        model.addChangeListener( new PhotoelectricModel.ChangeListenerAdapter() {
            public void targetMaterialChanged( PhotoelectricModel.ChangeEvent event ) {
                kneeFrequency = determineKneeFrequency( model );
                lineDataSet.clear();
                updateGraph( model );
            }

            public void voltageChanged( PhotoelectricModel.ChangeEvent event ) {
                kneeFrequency = determineKneeFrequency( model );
                updateGraph( model);
            }

            public void wavelengthChanged( PhotoelectricModel.ChangeEvent event ) {
                updateGraph( model);
            }
        } );
    }

    private double determineKneeFrequency( PhotoelectricModel model ) {
        return PhysicsUtil.wavelengthToFrequency( PhysicsUtil.energyToWavelength( model.getWorkFunction() ));
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
        dotDataSet.clear();
        dotDataSet.addPoint( frequency, energy );

        if( lastFrequencyRecorded < kneeFrequency && frequency > kneeFrequency ) {
            lineDataSet.addPoint( kneeFrequency, 0 );
        }
        else if(lastFrequencyRecorded > kneeFrequency && frequency < kneeFrequency ) {
            lineDataSet.addPoint( kneeFrequency, 0 );
        }

        lineDataSet.addPoint( frequency, energy );
        lastFrequencyRecorded = frequency;
    }

    /**
     * Removes line plot from the graph
     */
    public void clearLinePlot() {
        lineDataSet.clear();
    }
}
