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
 * IRView indicates how the "incident and reflected" waves 
 * will be viewed.  They can be viewed either separately or
 * as a sum.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class IRView extends AbstractEnum {

    /* This class is not intended for instantiation. */
    private IRView( String name ) {
        super( name );
    }
    
    // IR View values
    public static final IRView SUM = new IRView( "sum" );
    public static final IRView SEPARATE = new IRView( "separate" );
    
    /**
     * Retrieves a wave type by name.
     * This is used primarily in XML encoding.
     * 
     * @param name
     * @return
     */
    public static IRView getByName( String name ) {
        IRView irView = null;
        if ( SUM.isNamed( name ) ) {
            irView = SUM;
        }
        else if ( SEPARATE.isNamed( name ) ) {
            irView = SEPARATE;
        }
        return irView;
    }
}
