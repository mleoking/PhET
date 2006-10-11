/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.colorado.phet.molecularreactions.util.StripChart;
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.common.view.util.SimStrings;
import org.jfree.chart.plot.PlotOrientation;

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

    public MoleculePopulationsStripChart( MRModel model, double xAxisRange, double minY, double maxY ) {
        super( title, seriesNames, xAxisLabel, yAxisLabel, orienation, xAxisRange, minY, maxY );

        model.addListener( new MoleculeCountSpinner() );
    }
}
