/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view.charts;

import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.util.StripChart;
import edu.colorado.phet.molecularreactions.util.Resetable;
import edu.colorado.phet.molecularreactions.view.MoleculePaints;
import edu.colorado.phet.molecularreactions.MRConfig;
import org.jfree.chart.plot.PlotOrientation;

import java.awt.*;

/**
 * MoleculePopulationsStripChart
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculePopulationsStripChart extends StripChart {
    static String title = SimStrings.get( "StripChart.title" );
    static String[] seriesNames = new String[]{"A", "BC", "AB", "C"};
    static String xAxisLabel = SimStrings.get( "StripChart.time" );
    static String yAxisLabel = SimStrings.get( "StripChart.num" );
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

        getChart().setBackgroundPaint( MRConfig.MOLECULE_PANE_BACKGROUND );
        this.updateInterval = updateInterval;

        // Create counters for each of the molecule types
        counterA = new MoleculeCounter( MoleculeA.class, model );
        counterAB = new MoleculeCounter( MoleculeAB.class, model );
        counterBC = new MoleculeCounter( MoleculeBC.class, model );
        counterC = new MoleculeCounter( MoleculeC.class, model );

        // Set graphic attributes
        setStroke( new BasicStroke( 2.0f ) );
        setSeriesPaint( 0, MoleculePaints.getPaint( MoleculeA.class ) );
        setSeriesPaint( 1, MoleculePaints.getPaint( MoleculeBC.class ) );
        setSeriesPaint( 2, MoleculePaints.getPaint( MoleculeAB.class ) );
        setSeriesPaint( 3, MoleculePaints.getPaint( MoleculeC.class ) );

        // Hook up to the clock
        clock.addClockListener( new Updater() );
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

    private class Updater extends ClockAdapter {

        public void clockTicked( ClockEvent clockEvent ) {
            timeSinceLastUpdate += clockEvent.getSimulationTimeChange();
            if( timeSinceLastUpdate > updateInterval ) {
                timeSinceLastUpdate = 0;

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