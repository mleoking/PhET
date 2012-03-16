// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.enums;


/**
 * MathForm is a typesafe enumueration of "math form" values.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class MathForm extends FourierEnum {

    /* This class is not intended for instantiation. */
    private MathForm( String name ) {
        super(name);
    }
    
    // Math Form values
    public static final MathForm UNDEFINED = new MathForm( "undefined" );
    public static final MathForm WAVE_NUMBER = new MathForm( "waveNumber" );
    public static final MathForm WAVELENGTH = new MathForm( "wavelength" );
    public static final MathForm MODE = new MathForm( "mode" );
    public static final MathForm ANGULAR_FREQUENCY = new MathForm( "angularFrequency" );
    public static final MathForm FREQUENCY = new MathForm( "frequency" );
    public static final MathForm PERIOD = new MathForm( "period" );
    public static final MathForm WAVE_NUMBER_AND_ANGULAR_FREQUENCY = new MathForm( "waveNumberAndAngularFrequency" );
    public static final MathForm WAVELENGTH_AND_PERIOD = new MathForm( "wavelengthAndPeriod" );
    
    /**
     * Retrieves a domain by name.
     * This is used primarily in XML encoding.
     * 
     * @param name
     * @return
     */
    public static MathForm getByName( String name ) {
        MathForm mathForm = null;
        if ( WAVE_NUMBER.isNamed( name ) ) {
            mathForm = WAVE_NUMBER;
        }
        else if ( WAVELENGTH.isNamed( name ) ) {
            mathForm = WAVELENGTH;
        }
        else if ( MODE.isNamed( name ) ) {
            mathForm = MODE;
        }
        else if ( ANGULAR_FREQUENCY.isNamed( name ) ) {
            mathForm = ANGULAR_FREQUENCY;
        }
        else if ( FREQUENCY.isNamed( name ) ) {
            mathForm = FREQUENCY;
        }
        else if ( PERIOD.isNamed( name ) ) {
            mathForm = PERIOD;
        }
        else if ( WAVE_NUMBER_AND_ANGULAR_FREQUENCY.isNamed( name ) ) {
            mathForm = WAVE_NUMBER_AND_ANGULAR_FREQUENCY;
        }
        else if ( WAVELENGTH_AND_PERIOD.isNamed( name ) ) {
            mathForm = WAVELENGTH_AND_PERIOD;
        }
        else if ( UNDEFINED.isNamed( name ) ) {
            mathForm = UNDEFINED;
        }
        return mathForm;
    }
}
