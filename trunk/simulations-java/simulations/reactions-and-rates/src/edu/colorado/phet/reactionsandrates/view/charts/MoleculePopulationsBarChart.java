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
public class MoleculePopulationsBarChart extends BarChart implements Rescaleable {
    static String title = MRConfig.RESOURCES.getLocalizedString( "StripChart.title" );
    static String[] seriesNames = new String[]{"A", "BC", "AB", "C"};
    static String xAxisLabel = "";
    static String yAxisLabel = MRConfig.RESOURCES.getLocalizedString( "StripChart.num" );
    static PlotOrientation orienation = PlotOrientation.VERTICAL;
    private MoleculeCounter counterA;
    private MoleculeCounter counterAB;
    private MoleculeCounter counterBC;
    private MoleculeCounter counterC;
    private double updateInterval;
    private double timeSinceLastUpdate;

    /**
     * @param model
     * @param clock
     * @param minY
     * @param maxY
     * @param updateInterval
     */
    public MoleculePopulationsBarChart( MRModel model, IClock clock, int minY, int maxY,
                                        double updateInterval ) {
        super( title, seriesNames, xAxisLabel, yAxisLabel, orienation, minY, maxY, false );

        this.updateInterval = updateInterval;

        // Create counters for each of the molecule types
        counterA = new MoleculeCounter( MoleculeA.class, model );
        counterAB = new MoleculeCounter( MoleculeAB.class, model );
        counterBC = new MoleculeCounter( MoleculeBC.class, model );
        counterC = new MoleculeCounter( MoleculeC.class, model );

        setChartColors( model.getEnergyProfile() );

        // Hook up to the clock
        clock.addClockListener( new Updater() );
        updateChart();

        model.addListener( new MRModel.ModelListenerAdapter() {
            public void notifyEnergyProfileChanged( EnergyProfile profile ) {
                setChartColors( profile );
            }
        }
        );
    }

    private void setChartColors( EnergyProfile profile ) {
        // Set graphic attributes
        setSeriesPaint( 0, MoleculePaints.getPaint( MoleculeA.class, profile ) );
        setSeriesPaint( 1, MoleculePaints.getPaint( MoleculeBC.class, profile ) );
        setSeriesPaint( 2, MoleculePaints.getPaint( MoleculeAB.class, profile ) );
        setSeriesPaint( 3, MoleculePaints.getPaint( MoleculeC.class, profile ) );

        getChart().setBackgroundPaint( MRConfig.MOLECULE_PANE_BACKGROUND );
        getChart().setBorderStroke( new BasicStroke( 1 ) );
        getChart().setBorderPaint( Color.black );
        getChart().getTitle().setFont( MRConfig.CHART_TITLE_FONT );
    }

    private void updateChart() {
        addData( counterA.getCnt(), seriesNames[0], "" );
        addData( counterBC.getCnt(), seriesNames[1], "" );
        addData( counterAB.getCnt(), seriesNames[2], "" );
        addData( counterC.getCnt(), seriesNames[3], "" );
    }

    public void rescale() {

        // Find largest molecule population
        int maxCnt = 0;
        maxCnt = Math.max( maxCnt, counterA.getCnt() );
        maxCnt = Math.max( maxCnt, counterBC.getCnt() );
        maxCnt = Math.max( maxCnt, counterAB.getCnt() );
        maxCnt = Math.max( maxCnt, counterC.getCnt() );

        // Make vertical scale 1.5x the max count
        double min = MRConfig.STRIP_CHART_MIN_RANGE_Y * 1.5;
        setYRange( 0, (int)Math.max( min, ( maxCnt * 1.5 ) ) );
    }

    /**
     * Updates the strip chart at specified intervals
     */
    private class Updater extends ClockAdapter {

        public void clockTicked( ClockEvent clockEvent ) {
            timeSinceLastUpdate += clockEvent.getSimulationTimeChange();
            if( timeSinceLastUpdate >= updateInterval ) {
                timeSinceLastUpdate = 0;
                updateChart();
            }
        }
    }
}
