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

    public static final String AMMONIA = getString( "ammonia" );
    public static final String BALANCE_EQUATION = getString( "balanceEquation" );
    public static final String BALANCE_SCALE = getString( "balanceScale" );
    public static final String BALANCE_THE_EQUATION = getString( "balanceTheEquation" );
    public static final String BAR_CHART = getString( "barChart" );
    public static final String GAME = getString( "game" );
    public static final String METHANE = getString( "methane" );
    public static final String WATER = getString( "water" );

    private static final String getString( String key ) {
        return BCEResources.getString( key );
    }
}
