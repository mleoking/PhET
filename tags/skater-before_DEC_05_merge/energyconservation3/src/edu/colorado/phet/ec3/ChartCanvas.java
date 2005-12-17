/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.ec3.model.Body;
import edu.colorado.phet.ec3.plots.*;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.timeseries.TimeSeriesModelListenerAdapter;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Oct 23, 2005
 * Time: 3:12:06 PM
 * Copyright (c) Oct 23, 2005 by Sam Reid
 */

public class ChartCanvas extends PhetPCanvas {
    private EC3Module ec3Module;
    private ArrayList units = new ArrayList();
    private TimePlotSuitePNode plot;
    private TimeSeriesPNode keSeries;
    private TimeSeriesPNode peSeries;
    private TimeSeriesPNode heatSeries;

    public ChartCanvas( final EC3Module ec3Module ) {
        this.ec3Module = ec3Module;
        plot = new TimePlotSuitePNode( this,
                                       new Range2D( 0, -7000 / 10.0, 40, 7000 ), "Energy",
                                       "Joules", ec3Module.getTimeSeriesModel(),
                                       150, false );
        addScreenChild( plot );

        heatSeries = new TimeSeriesPNode( plot, new ValueAccessor( "Thermal", "Thermal", "Joules", "J", ec3Module.getEnergyLookAndFeel().getThermalEnergyColor(), "Thermal Energy" ) {
            public double getValue( Object model ) {
                return ec3Module.getEnergyConservationModel().getThermalEnergy();
            }
        }, "", ec3Module.getTimeSeriesModel() );
        plot.addTimeSeries( heatSeries );
        units.add( new DataUnit( heatSeries ) );

        keSeries = new TimeSeriesPNode( plot, new ValueAccessor( "KE", "KE", "Joules", "J", ec3Module.getEnergyLookAndFeel().getKEColor(), "Kinetic Energy" ) {
            public double getValue( Object model ) {
                if( ec3Module.getEnergyConservationModel().numBodies() > 0 ) {
                    Body body = ec3Module.getEnergyConservationModel().bodyAt( 0 );
                    return body.getKineticEnergy();
                }
                else {
                    return 0;
                }
            }
        }, "", ec3Module.getTimeSeriesModel() );
        plot.addTimeSeries( keSeries );
        units.add( new DataUnit( keSeries ) );

        peSeries = new TimeSeriesPNode( plot, new ValueAccessor( "PE", "PE", "Joules", "J", ec3Module.getEnergyLookAndFeel().getPEColor(), "Potential Energy" ) {
            public double getValue( Object model ) {
                if( ec3Module.getEnergyConservationModel().numBodies() > 0 ) {
                    Body body = ec3Module.getEnergyConservationModel().bodyAt( 0 );
                    return ec3Module.getEnergyConservationModel().getPotentialEnergy( body );
                }
                else {
                    return 0;
                }
            }
        }, "", ec3Module.getTimeSeriesModel() );
        plot.addTimeSeries( peSeries );
        units.add( new DataUnit( peSeries ) );


        ec3Module.getClock().addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                if( ec3Module.getTimeSeriesModel().isRecording() ) {
                    for( int i = 0; i < units.size(); i++ ) {
                        DataUnit dataUnit = (DataUnit)units.get( i );
                        dataUnit.updatePlot( ec3Module.getEnergyConservationModel(), ec3Module.getTimeSeriesModel().getRecordTime() );
                    }
                }
                plot.updateReadouts();
            }
        } );
        ec3Module.getTimeSeriesModel().addListener( new TimeSeriesModelListenerAdapter() {
            public void reset() {
                ChartCanvas.this.reset();
            }
        } );
    }

    public void reset() {
        plot.reset();
        for( int i = 0; i < units.size(); i++ ) {
            DataUnit dataUnit = (DataUnit)units.get( i );
            dataUnit.reset();
        }
    }
}
