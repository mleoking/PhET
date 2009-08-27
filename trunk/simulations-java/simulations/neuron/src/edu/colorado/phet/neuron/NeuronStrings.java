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
    
    public static final String TITLE_MEMBRANE_DIFFUSION_MODULE = NeuronResources.getString( "ModuleTitle.MembraneDiffusionModule" );
    public static final String TITLE_GATED_CHANNELS_MODULE = NeuronResources.getString( "ModuleTitle.GatedChannels" );
}
