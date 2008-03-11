/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2;

/**
 * TemplateStrings is the collection of localized strings used by this simulations.
 * We load all strings as statics so that we will be warned at startup time of any missing strings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NuclearPhysics2Strings {
    
    /* not intended for instantiation */
    private NuclearPhysics2Strings() {}
    
    public static final String LABEL_POSITION = NuclearPhysics2Resources.getString( "label.position");
    public static final String LABEL_ORIENTATION = NuclearPhysics2Resources.getString( "label.orientation");
    
    public static final String TITLE_EXAMPLE_MODULE = NuclearPhysics2Resources.getString( "title.exampleModule" );
    
    public static final String TITLE_ALPHA_RADIATION_MODULE = NuclearPhysics2Resources.getString( "ModuleTitle.AlphaDecayModule" );

    public static final String UNITS_ORIENTATION = NuclearPhysics2Resources.getString( "units.orientation");
    public static final String UNITS_TIME = NuclearPhysics2Resources.getString( "units.time" );

}
