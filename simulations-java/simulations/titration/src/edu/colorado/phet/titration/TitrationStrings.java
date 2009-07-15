/* Copyright 2009, University of Colorado */

package edu.colorado.phet.titration;

/**
 * TemplateStrings is the collection of localized strings used by this simulations.
 * We load all strings as statics so that we will be warned at startup time of any missing strings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TitrationStrings {
    
    /* not intended for instantiation */
    private TitrationStrings() {}
    
    public static final String TITLE_TITRATE = TitrationResources.getString( "title.titrate" );
    public static final String TITLE_ADVANCED_TITRATION = TitrationResources.getString( "title.advancedTitration" );
    public static final String TITLE_POLYPROTIC_ACIDS = TitrationResources.getString( "title.polyproticAcids" );
    public static final String TITLE_COMPARE_TITRATIONS = TitrationResources.getString( "title.compareTitrations" );
}
