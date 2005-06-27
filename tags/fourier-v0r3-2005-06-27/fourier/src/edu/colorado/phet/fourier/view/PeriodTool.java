/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view;

import java.awt.Component;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.fourier.MathStrings;
import edu.colorado.phet.fourier.model.Harmonic;


/**
 * PeriodTool
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PeriodTool extends WaveMeasurementTool {

    private static final String SYMBOL = "" + MathStrings.C_PERIOD;
    
    public PeriodTool( Component component, Harmonic harmonic, Chart chart ) {
        super( component, SYMBOL, harmonic, chart );
    }
}
