/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.enums;


/**
 * WaveType is an enumeration of wave types.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WaveType extends AbstractEnum {

    /* This class is not intended for instantiation. */
    private WaveType( String name ) {
        super( name );
    }
    
    // Wave Type values
    public static final WaveType PLANE = new WaveType( "plane" );
    public static final WaveType PACKET = new WaveType( "packet" );
    
    /**
     * Retrieves a wave type by name.
     * This is used primarily in XML encoding.
     * 
     * @param name
     * @return
     */
    public static WaveType getByName( String name ) {
        WaveType waveType = null;
        if ( PLANE.isNamed( name ) ) {
            waveType = PLANE;
        }
        else if ( PACKET.isNamed( name ) ) {
            waveType = PACKET;
        }
        return waveType;
    }
}
