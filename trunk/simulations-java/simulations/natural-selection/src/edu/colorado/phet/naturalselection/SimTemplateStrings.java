/* Copyright 2008, University of Colorado */

package edu.colorado.phet.naturalselection;

/**
 * TemplateStrings is the collection of localized strings used by this simulations.
 * We load all strings as statics so that we will be warned at startup time of any missing strings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimTemplateStrings {
    
    /* not intended for instantiation */
    private SimTemplateStrings() {}
    
    public static final String LABEL_POSITION = SimTemplateResources.getString( "label.position");
    public static final String LABEL_ORIENTATION = SimTemplateResources.getString( "label.orientation");
    
    public static final String TITLE_EXAMPLE_CONTROL_PANEL = SimTemplateResources.getString( "title.exampleControlPanel");
    public static final String TITLE_EXAMPLE_MODULE = SimTemplateResources.getString( "title.exampleModule" );

    public static final String UNITS_DISTANCE = SimTemplateResources.getString( "units.distance");
    public static final String UNITS_ORIENTATION = SimTemplateResources.getString( "units.orientation");
    public static final String UNITS_TIME = SimTemplateResources.getString( "units.time" );

}
