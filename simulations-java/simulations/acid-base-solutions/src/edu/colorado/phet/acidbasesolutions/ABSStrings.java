/* Copyright 2008, University of Colorado */

package edu.colorado.phet.acidbasesolutions;

/**
 * TemplateStrings is the collection of localized strings used by this simulations.
 * We load all strings as statics so that we will be warned at startup time of any missing strings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSStrings {
    
    /* not intended for instantiation */
    private ABSStrings() {}
    
    public static final String LABEL_POSITION = ABSResources.getString( "label.position");
    public static final String LABEL_ORIENTATION = ABSResources.getString( "label.orientation");
    
    public static final String TITLE_SOLUTIONS_MODULE = ABSResources.getString( "title.solutionsModule" );
    public static final String TITLE_COMPARING_MODULE = ABSResources.getString( "title.comparingModule" );

    public static final String TITLE_SOLUTIONS_CONTROL_PANEL = ABSResources.getString( "title.solutionsControlPanel");
    
    public static final String UNITS_DISTANCE = ABSResources.getString( "units.distance");
    public static final String UNITS_ORIENTATION = ABSResources.getString( "units.orientation");
    public static final String UNITS_TIME = ABSResources.getString( "units.time" );

}
