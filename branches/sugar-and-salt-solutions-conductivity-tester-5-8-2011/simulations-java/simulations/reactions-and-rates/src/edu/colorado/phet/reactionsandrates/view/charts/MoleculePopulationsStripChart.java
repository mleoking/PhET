// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.view.charts;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.reactionsandrates.MRConfig;
import edu.colorado.phet.reactionsandrates.model.*;
import edu.colorado.phet.reactionsandrates.view.MoleculePaints;

import org.jfree.chart.plot.PlotOrientation;

import java.awt.*;

/**
 * MoleculePopulationsStripChart
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculePopulationsStripChart extends StripChart implements Rescaleable {
    static String title = MRConfig.RESOURCES.getLocalizedString( "StripChart.title" );
    static String[] seriesNames = new String[]{"A", "BC", "AB", "C"};
    static String xAxisLabel = MRConfig.RESOURCES.getLocalizedString( "StripChart.time" );
    static String yAxisLabel = MRConfig.RESOURCES.getLocalizedString( "StripChart.num" );
    static PlotOrientation orienation = PlotOrientation.VERTICAL;

    private MoleculeCounter counterA;
    private MoleculeCounter counterAB;
    private MoleculeCounter counterBC;
    private MoleculeCounter counterC;
    private double updateInterval;
    private double timeSinceLastUpdate;
    private MoleculePopulationsStripChart.Updater updater;
    private IClock clock;

    /**
     * @param model
     * @param clock
     * @param xAxisRange
     * @param minY
     * @param maxY
     * @param updateInterval
     * @param buffSize
     */
    public MoleculePopulationsStripChart( MRModel model,
                                          IClock clock,
                                          double xAxisRange,
                                          double minY,
                                          double maxY,
                                          double updateInterval,
                                          int buffSize ) {
        super( title, seriesNames, xAxisLabel, yAxisLabel, orienation, xAxisRange, minY, maxY, buffSize );
        this.clock = clock;

        getChart().setBackgroundPaint( MRConfig.MOLECULE_PANE_BACKGROUND );
        this.updateInterval = updateInterval;

        // Create counters for each of the molecule types
        counterA = new MoleculeCounter( MoleculeA.class, model );
        counterAB = new MoleculeCounter( MoleculeAB.class, model );
        counterBC = new MoleculeCounter( MoleculeBC.class, model );
        counterC = new MoleculeCounter( MoleculeC.class, model );

        // Set graphic attributes

        setStroke( new BasicStroke( 2.0f ) );

        EnergyProfile profile = model.getEnergyProfile();

        setPaintOfMolecules( profile );

        model.addListener( new MRModel.ModelListenerAdapter() {
            public void notifyEnergyProfileChanged( EnergyProfile profile ) {
                setPaintOfMolecules( profile );
            }
        } );

        // Hook up to the clock
        updater = new Updater();

        clock.addClockListener( updater );
    }

    private void setPaintOfMolecules( EnergyProfile profile ) {
        setSeriesPaint( 0, MoleculePaints.getPaint( MoleculeA.class, profile ) );
        setSeriesPaint( 1, MoleculePaints.getPaint( MoleculeBC.class, profile ) );
        setSeriesPaint( 2, MoleculePaints.getPaint( MoleculeAB.class, profile ) );
        setSeriesPaint( 3, MoleculePaints.getPaint( MoleculeC.class, profile ) );
    }

    public void rescale() {
        // Find largest molecule population
        int maxCnt = MRConfig.STRIP_CHART_MIN_RANGE_Y;
        maxCnt = Math.max( maxCnt, counterA.getCnt() );
        maxCnt = Math.max( maxCnt, counterBC.getCnt() );
        maxCnt = Math.max( maxCnt, counterAB.getCnt() );
        maxCnt = Math.max( maxCnt, counterC.getCnt() );

        // Make vertical scale 1.5x the max count
        super.setYRange( 0, (int)( maxCnt * 1.5 ) );
    }

    public void terminate() {
        clock.removeClockListener( updater );
    }

    /**
     * Updates the chart when the clock ticks
     */
    private class Updater extends ClockAdapter {

        public void clockTicked( ClockEvent clockEvent ) {
            timeSinceLastUpdate += clockEvent.getSimulationTimeChange();
            if( timeSinceLastUpdate > updateInterval ) {
                timeSinceLastUpdate = 0;

                //System.out.println("Updating data at time = " + clockEvent.getSimulationTime());

                addData( clockEvent.getSimulationTime(),
                         new double[]{counterA.getCnt(),
                                 counterBC.getCnt(),
                                 counterAB.getCnt(),
                                 counterC.getCnt(),
                                 counterA.getCnt()
                         } );
            }
        }
    }
}