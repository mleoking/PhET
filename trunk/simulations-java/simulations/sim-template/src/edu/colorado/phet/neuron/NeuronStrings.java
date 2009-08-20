/* Copyright 2008, University of Colorado */

package edu.colorado.phet.neuron;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * TemplateStrings is the collection of localized strings used by this simulations.
 * We load all strings as statics so that we will be warned at startup time of any missing strings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NeuronStrings {
    
    /* not intended for instantiation */
    private NeuronStrings() {}
    
    public static final String LABEL_POSITION = NeuronResources.getString( "label.position");
    public static final String LABEL_ORIENTATION = NeuronResources.getString( "label.orientation");
    
    public static final String TITLE_EXAMPLE_CONTROL_PANEL = NeuronResources.getString( "title.exampleControlPanel");
    public static final String TITLE_EXAMPLE_MODULE = NeuronResources.getString( "title.exampleModule" );
    public static final String TITLE_ERROR = PhetCommonResources.getString( "Common.title.error" );

    public static final String UNITS_DISTANCE = NeuronResources.getString( "units.distance");
    public static final String UNITS_ORIENTATION = NeuronResources.getString( "units.orientation");
    public static final String UNITS_TIME = NeuronResources.getString( "units.time" );
    
    public static final String MESSAGE_NOT_A_CONFIG = PhetCommonResources.getString( "XMLPersistenceManager.message.notAConfigFile" );
}
