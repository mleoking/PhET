/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.charts;

import java.awt.Component;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.chart.LinePlot;
import edu.colorado.phet.fourier.model.Harmonic;


/**
 * HarmonicDataSetGraphic is the graphical representation, provided
 * to a Chart, of a HarmonicDataSet.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HarmonicDataSetGraphic extends LinePlot {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component
     * @param chart
     * @param dataSet
     */
    public HarmonicDataSetGraphic( Component component, Chart chart, HarmonicDataSet dataSet ) {
        super( component, chart, dataSet );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the data set associated with this graphic.
     * 
     * @return HarmonicDataSet
     */
    public HarmonicDataSet getHarmonicDataSet() {
        return (HarmonicDataSet) getDataSet();
    }

    /**
     * Gets the harmonic associated with this graphic's data set.
     * 
     * @return Harmonic
     */
    public Harmonic getHarmonic() {
        return getHarmonicDataSet().getHarmonic();
    }
}
