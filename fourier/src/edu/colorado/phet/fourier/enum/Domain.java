/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.enum;


/**
 * Domain is a typesafe enumueration of "domain" values.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Domain extends FourierEnum {
    
    /* This class is not intended for instantiation. */
    private Domain( String name ) {
        super( name );
    }
    
    // Domain values
    public static final Domain UNDEFINED = new Domain( "undefined" );
    public static final Domain SPACE = new Domain( "space" );
    public static final Domain TIME = new Domain( "time" );
    public static final Domain SPACE_AND_TIME = new Domain( "spaceAndTime" );
}