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
 * WaveType is a typesafe enumueration of "wave type" values.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WaveType extends FourierEnum {

    /* This class is not intended for instantiation. */
    private WaveType( String name ) {
        super( name );
    }
    
    // Wave Type values
    public static final WaveType UNDEFINED = new WaveType( "undefined" );
    public static final WaveType SINES = new WaveType( "sines" );
    public static final WaveType COSINES = new WaveType( "cosines" );
    
    /**
     * Retrieves a wave type by name.
     * This is used primarily in XML encoding.
     * 
     * @param name
     * @return
     */
    public static WaveType getByName( String name ) {
        WaveType waveType = null;
        if ( SINES.isNamed( name ) ) {
            waveType = SINES;
        }
        else if ( COSINES.isNamed( name ) ) {
            waveType = COSINES;
        }
        else if ( UNDEFINED.isNamed( name ) ) {
            waveType = UNDEFINED;
        }
        return waveType;
    }
}