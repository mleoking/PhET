// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations;

/**
 * Collection of localized strings used by this project.
 * Statically loaded so we can easily see if any are missing.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BCEStrings {

    /* not intended for instantiation */
    private BCEStrings() {}

    public static final String TITLE_BALANCE_EQUATION = getString( "title.balanceEquation" );
    public static final String TITLE_GAME = getString( "title.game" );

    private static final String getString( String key ) {
        return BCEResources.getString( key );
    }
}
