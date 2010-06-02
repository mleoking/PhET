/* Copyright 2008, University of Colorado */

package edu.colorado.phet.membranediffusion;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * A collection of localized strings used by this simulations.
 * We load all strings as statics so that we will be warned at startup time of any missing strings.
 */
public class SimTemplateStrings {
    
    /* not intended for instantiation */
    private SimTemplateStrings() {}
    
    public static final String TITLE_EXAMPLE_CONTROL_PANEL = SimTemplateResources.getString( "title.exampleControlPanel");
    public static final String TITLE_EXAMPLE_MODULE = SimTemplateResources.getString( "title.exampleModule" );
    public static final String TITLE_ERROR = PhetCommonResources.getString( "Common.title.error" );

    public static final String UNITS_TIME = SimTemplateResources.getString( "units.time" );
    
    public static final String MESSAGE_NOT_A_CONFIG = PhetCommonResources.getString( "XMLPersistenceManager.message.notAConfigFile" );
}
