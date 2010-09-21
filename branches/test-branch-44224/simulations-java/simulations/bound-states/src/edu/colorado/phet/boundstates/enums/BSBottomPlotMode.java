/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.enums;

/**
 * BSBottomPlotMode is an enumeration of modes for BSBottomPlot.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSBottomPlotMode extends AbstractEnum {

    /* This class is not intended for instantiation. */
    private BSBottomPlotMode( String name ) {
        super( name );
    }
    
    // Well type values
    public static final BSBottomPlotMode WAVE_FUNCTION = new BSBottomPlotMode( "waveFunction" );
    public static final BSBottomPlotMode PROBABILITY_DENSITY = new BSBottomPlotMode( "probabilityDensity" );
    public static final BSBottomPlotMode AVERAGE_PROBABILITY_DENSITY = new BSBottomPlotMode( "averageProbabilityDensity" );
    
    /**
     * Retrieves a well type by name.
     * This is used primarily in XML encoding.
     * 
     * @param name
     * @return the mode that corresponds to name, possibly null
     */
    public static BSBottomPlotMode getByName( String name ) {
        BSBottomPlotMode mode = null;
        if ( WAVE_FUNCTION.isNamed( name ) ) {
            mode = WAVE_FUNCTION;
        }
        else if ( PROBABILITY_DENSITY.isNamed( name ) ) {
            mode = PROBABILITY_DENSITY;
        }
        else if ( AVERAGE_PROBABILITY_DENSITY.isNamed( name ) ) {
            mode = AVERAGE_PROBABILITY_DENSITY;
        }
        return mode;
    }
}
