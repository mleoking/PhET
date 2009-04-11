/* Copyright 2008, University of Colorado */

package edu.colorado.phet.naturalselection;

/**
 * TemplateStrings is the collection of localized strings used by this simulations.
 * We load all strings as statics so that we will be warned at startup time of any missing strings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NaturalSelectionStrings {
    
    /* not intended for instantiation */
    private NaturalSelectionStrings() {}
    
    public static final String LABEL_POSITION = NaturalSelectionResources.getString( "label.position");
    public static final String LABEL_ORIENTATION = NaturalSelectionResources.getString( "label.orientation");
    
    public static final String TITLE_EXAMPLE_CONTROL_PANEL = NaturalSelectionResources.getString( "title.exampleControlPanel");
    public static final String TITLE_EXAMPLE_MODULE = NaturalSelectionResources.getString( "title.exampleModule" );

    public static final String UNITS_DISTANCE = NaturalSelectionResources.getString( "units.distance");
    public static final String UNITS_ORIENTATION = NaturalSelectionResources.getString( "units.orientation");
    public static final String UNITS_TIME = NaturalSelectionResources.getString( "units.time" );

}
