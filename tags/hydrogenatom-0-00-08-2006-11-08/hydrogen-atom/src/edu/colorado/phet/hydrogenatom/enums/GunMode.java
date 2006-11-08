/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.enums;

/**
 * GunMode is an enumeration of "modes" for the gun.
 * The gun can fire with photons or alpha particles.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GunMode extends AbstractEnum {

    /* This class is not intended for instantiation. */
    private GunMode( String name ) {
        super( name );
    }
    
    // Well type values
    public static final GunMode PHOTONS = new GunMode( "photons" );
    public static final GunMode ALPHA_PARTICLES = new GunMode( "alphaParticles" );
    
    /**
     * Retrieves a well type by name.
     * This is used primarily in XML encoding.
     * 
     * @param name
     * @return the well type that corresponds to name, possibly null
     */
    public static GunMode getByName( String name ) {
        GunMode gunMode = null;
        if ( PHOTONS.isNamed( name ) ) {
            gunMode = PHOTONS;
        }
        else if ( ALPHA_PARTICLES.isNamed( name ) ) {
            gunMode = ALPHA_PARTICLES;
        }
        return gunMode;
    }
}
