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
    public static final String BALANCED = getString( "balanced" );
    public static final String BALANCE_EQUATION = getString( "balanceEquation" );
    public static final String BALANCE_SCALES = getString( "balanceScales" );
    public static final String BAR_CHARTS = getString( "barCharts" );
    public static final String CHECK = getString( "check" );
    public static final String EQUATION_0_OF_1 = getString( "equation0of1" );
    public static final String GAME = getString( "game" );
    public static final String HIDE_MOLECULES = getString( "hideMolecules" );
    public static final String METHANE = getString( "methane" );
    public static final String MOLECULES_ARE_HIDDEN = getString( "moleculesAreHidden" );
    public static final String NEXT = getString( "next" );
    public static final String NONE = getString( "none" );
    public static final String NOT_BALANCED = getString( "notBalanced" );
    public static final String NOT_SIMPLIFIED = getString( "notSimplified" );
    public static final String SHOW_ANSWER = getString( "showAnswer" );
    public static final String TRY_AGAIN = getString( "tryAgain" );
    public static final String WATER = getString( "water" );

    private static final String getString( String key ) {
        return BCEResources.getString( key );
    }
}
