// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.view;

import edu.colorado.phet.common.charts.GridLineSet;
import edu.colorado.phet.common.charts.LinePlot;
import edu.colorado.phet.common.charts.Range2D;
import edu.colorado.phet.common.charts.ScatterPlot;
import edu.colorado.phet.common.phetcommon.util.PhysicsUtil;
import edu.colorado.phet.photoelectric.PhotoelectricConfig;
import edu.colorado.phet.photoelectric.model.PhotoelectricModel;

import java.awt.*;
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
public class EnergyVsFrequencyGraph extends PhotoelectricGraph {

    //-----------------------------------------------------------------
    // Class data
    //-----------------------------------------------------------------
    static private double PLOT_LAYER = 1E9;
    static private double freqMultiplier = 1E-15;
    static private Range2D range = new Range2D( 0, 0,
                                                PhysicsUtil.wavelengthToFrequency( PhotoelectricModel.MIN_WAVELENGTH ) * freqMultiplier,
                                                PhysicsUtil.wavelengthToEnergy( PhotoelectricModel.MIN_WAVELENGTH ) );
    static private double xSpacing = ( range.getMaxX() - range.getMinX() ) / 4;
    static private Dimension chartSize = PhotoelectricConfig.CHART_SIZE;

    //-----------------------------------------------------------------
    // Instance data
    //-----------------------------------------------------------------

    private double kneeFrequency;
    private double lastFrequencyRecorded;
    private boolean beamOn;

    //-----------------------------------------------------------------
    // Instance methods
    //-----------------------------------------------------------------

    public EnergyVsFrequencyGraph( Component component, final PhotoelectricModel model ) {
        super( component, range, chartSize, xSpacing, xSpacing, 2, 2 );

        GridLineSet horizontalGls = this.getHorizonalGridlines();
        horizontalGls.setMajorGridlinesColor( new Color( 200, 200, 200 ) );

        GridLineSet verticalGls = this.getVerticalGridlines();
        verticalGls.setMajorGridlinesColor( new Color( 200, 200, 200 ) );

        getHorizontalTicks().setMajorNumberFormat( new DecimalFormat( "0.00" ) );

        this.getXAxis().setNumberFormat( new DecimalFormat( "0.00" ) );

        Color color = Color.blue;
        Color lineColor = new Color( color.getRed(), color.getGreen(), color.getBlue(), 80 );
        LinePlot lines = new LinePlot( getComponent(), this, getLineDataSet(), new BasicStroke( 3f ), lineColor );
        lines.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        this.addDataSetGraphic( lines, PLOT_LAYER );

        ScatterPlot points = new ScatterPlot( getComponent(), this, getDotDataSet(), color, PhotoelectricConfig.GRAPH_DOT_RADIUS );
        this.addDataSetGraphic( points, PLOT_LAYER + 1 );

        model.addChangeListener( new PhotoelectricModel.ChangeListenerAdapter() {
            public void targetMaterialChanged( PhotoelectricModel.ChangeEvent event ) {
                kneeFrequency = determineKneeFrequency( model ) * freqMultiplier;
                clearLinePlot();
                updateGraph( model );
            }

            public void voltageChanged( PhotoelectricModel.ChangeEvent event ) {
                kneeFrequency = determineKneeFrequency( model ) * freqMultiplier;
                updateGraph( model );
            }

            public void wavelengthChanged( PhotoelectricModel.ChangeEvent event ) {
                updateGraph( model );
            }

            public void beamIntensityChanged( PhotoelectricModel.ChangeEvent event ) {
                if( event.getPhotoelectricModel().getBeam().getPhotonsPerSecond() == 0 ) {
                    beamOn = false;
                    getDotDataSet().clear();
                    updateGraph( model );
                }
                else {
                    beamOn = true;
                    updateGraph( model );
                }
            }
        } );
    }

    private double determineKneeFrequency( PhotoelectricModel model ) {
        return PhysicsUtil.wavelengthToFrequency( PhysicsUtil.energyToWavelength( model.getWorkFunction() ) );
    }

    private void updateGraph( PhotoelectricModel model ) {
        if( beamOn ) {
            double frequency = PhysicsUtil.wavelengthToFrequency( model.getWavelength() );
            double workFunction = model.getTarget().getMaterial().getWorkFunction();
            double energy = Math.max( 0, PhysicsUtil.wavelengthToEnergy( model.getWavelength() ) - workFunction );
            addDataPoint( frequency, energy );
        }
    }

    /**
     * Adds a data point for a specified wavelength
     *
     * @param frequency
     * @param energy
     */
    private void addDataPoint( double frequency, double energy ) {

        frequency *= freqMultiplier;
        setDotDataPoint( frequency, energy );

        if( lastFrequencyRecorded < kneeFrequency && frequency > kneeFrequency ) {
            getLineDataSet().addPoint( kneeFrequency, 0 );
        }
        else if( lastFrequencyRecorded > kneeFrequency && frequency < kneeFrequency ) {
            getLineDataSet().addPoint( kneeFrequency, 0 );
        }

        getLineDataSet().addPoint( frequency, energy );
        lastFrequencyRecorded = frequency;
    }
}
