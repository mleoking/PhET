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
    
    /**
     * Retrieves a domain by name.
     * This is used primarily in XML encoding.
     * 
     * @param name
     * @return
     */
    public static Domain getByName( String name ) {
        Domain domain = null;
        if ( SPACE.isNamed( name ) ) {
            domain = SPACE;
        }
        else if ( TIME.isNamed( name ) ) {
            domain = TIME;
        }
        else if ( SPACE_AND_TIME.isNamed( name ) ) {
            domain = SPACE_AND_TIME;
        }
        else if ( UNDEFINED.isNamed( name ) ) {
            domain = UNDEFINED;
        }
        return domain;
    }
}