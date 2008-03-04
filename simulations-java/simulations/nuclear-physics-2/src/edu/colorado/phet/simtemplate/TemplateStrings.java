/* Copyright 2008, University of Colorado */

package edu.colorado.phet.simtemplate;

/**
 * TemplateStrings is the collection of localized strings used by this simulations.
 * We load all strings as statics so that we will be warned at startup time of any missing strings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TemplateStrings {
    
    /* not intended for instantiation */
    private TemplateStrings() {}
    
    public static final String LABEL_POSITION = TemplateResources.getString( "label.position");
    public static final String LABEL_ORIENTATION = TemplateResources.getString( "label.orientation");
    
    public static final String TITLE_EXAMPLE_CONTROL_PANEL = TemplateResources.getString( "title.exampleControlPanel");
    public static final String TITLE_EXAMPLE_MODULE = TemplateResources.getString( "title.exampleModule" );

    public static final String UNITS_DISTANCE = TemplateResources.getString( "units.distance");
    public static final String UNITS_ORIENTATION = TemplateResources.getString( "units.orientation");
    public static final String UNITS_TIME = TemplateResources.getString( "units.time" );

}
