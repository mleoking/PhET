/* Copyright 2008, University of Colorado */

package edu.colorado.phet.simexample;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * TemplateStrings is the collection of localized strings used by this simulations.
 * We load all strings as statics so that we will be warned at startup time of any missing strings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimExampleStrings {
    
    /* not intended for instantiation */
    private SimExampleStrings() {}
    
    public static final String LABEL_POSITION = SimExampleResources.getString( "label.position");
    public static final String LABEL_ORIENTATION = SimExampleResources.getString( "label.orientation");
    
    public static final String TITLE_EXAMPLE_CONTROL_PANEL = SimExampleResources.getString( "title.exampleControlPanel");
    public static final String TITLE_EXAMPLE_MODULE = SimExampleResources.getString( "title.exampleModule" );
    public static final String TITLE_ERROR = PhetCommonResources.getString( "Common.title.error" );

    public static final String UNITS_DISTANCE = SimExampleResources.getString( "units.distance");
    public static final String UNITS_ORIENTATION = SimExampleResources.getString( "units.orientation");
    public static final String UNITS_TIME = SimExampleResources.getString( "units.time" );
    
    public static final String MESSAGE_NOT_A_CONFIG = PhetCommonResources.getString( "XMLPersistenceManager.message.notAConfigFile" );
}
