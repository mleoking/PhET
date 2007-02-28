/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.plots;

import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.ec3.EnergySkateParkModule;
import edu.colorado.phet.ec3.model.Body;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.timeseries.TimeSeriesModelListenerAdapter;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Oct 23, 2005
 * Time: 3:12:06 PM
 * Copyright (c) Oct 23, 2005 by Sam Reid
 */

public class EnergyTimePlotCanvas extends PhetPCanvas {
    private EnergySkateParkModule ec3Module;
    private ArrayList units = new ArrayList();
    private EnergySkaterTimePlotNode plot;
    private TimeSeriesPNode keSeries;
    private TimeSeriesPNode peSeries;
    private TimeSeriesPNode heatSeries;
    private PSwing clearButtonNode;

    public EnergyTimePlotCanvas( final EnergySkateParkModule ec3Module ) {
        this.ec3Module = ec3Module;
        plot = new EnergySkaterTimePlotNode( this,
                                             new Range2D( 0, -7000 / 10.0, 40, 7000 ), "Energy",
                                             "Joules", ec3Module.getTimeSeriesModel(),
                                             150, false );
        addScreenChild( plot );

        heatSeries = new TimeSeriesPNode( plot, new ValueAccessor( "Thermal", "Thermal", "Joules", "J", ec3Module.getEnergyLookAndFeel().getThermalEnergyColor(), "Thermal Energy" ) {
            public double getValue( Object model ) {
//                return ec3Module.getEnergyConservationModel().getThermalEnergy();
                if( ec3Module.getEnergySkateParkModel().numBodies() > 0 ) {
                    Body body = ec3Module.getEnergySkateParkModel().bodyAt( 0 );
                    return body.getThermalEnergy();
                }
                else {
                    return 0;
                }
            }
        }, "", ec3Module.getTimeSeriesModel() );
        plot.addTimeSeries( heatSeries );
        units.add( new DataUnit( heatSeries ) );

        keSeries = new TimeSeriesPNode( plot, new ValueAccessor( "KE", "KE", "Joules", "J", ec3Module.getEnergyLookAndFeel().getKEColor(), "Kinetic Energy" ) {
            public double getValue( Object model ) {
                if( ec3Module.getEnergySkateParkModel().numBodies() > 0 ) {
                    Body body = ec3Module.getEnergySkateParkModel().bodyAt( 0 );
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
                if( ec3Module.getEnergySkateParkModel().numBodies() > 0 ) {
                    Body body = ec3Module.getEnergySkateParkModel().bodyAt( 0 );
                    return body.getPotentialEnergy();
                }
                else {
                    return 0;
                }
            }
        }, "", ec3Module.getTimeSeriesModel() );
        plot.addTimeSeries( peSeries );
        units.add( new DataUnit( peSeries ) );

        ec3Module.getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
                if( ec3Module.getTimeSeriesModel().isRecording() ) {
                    for( int i = 0; i < units.size(); i++ ) {
                        DataUnit dataUnit = (DataUnit)units.get( i );
                        dataUnit.updatePlot( ec3Module.getEnergySkateParkModel(), ec3Module.getTimeSeriesModel().getRecordTime() );
                    }
                }
                plot.updateReadouts();
            }
        } );
        ec3Module.getTimeSeriesModel().addListener( new TimeSeriesModelListenerAdapter() {
            public void reset() {
                EnergyTimePlotCanvas.this.reset();
            }
        } );

        JButton clear = new JButton( "Clear" );
        clear.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ec3Module.getTimeSeriesModel().reset();
            }
        } );
        clearButtonNode = new PSwing( this, clear );
        addScreenChild( clearButtonNode );
        layoutNodes();
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                layoutNodes();
            }
        } );
    }

    private void layoutNodes() {
        clearButtonNode.setOffset( getWidth() - clearButtonNode.getFullBounds().width - 2, 2 );
    }

    public void reset() {
        plot.reset();
        for( int i = 0; i < units.size(); i++ ) {
            DataUnit dataUnit = (DataUnit)units.get( i );
            dataUnit.reset();
        }
    }
}
