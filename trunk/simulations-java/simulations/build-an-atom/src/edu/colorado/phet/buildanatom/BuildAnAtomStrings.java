/* Copyright 2008, University of Colorado */

package edu.colorado.phet.buildanatom;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * A collection of localized strings used by this simulations.
 * We load all strings as statics so that we will be warned at startup time of any missing strings.
 */
public class BuildAnAtomStrings {
    
    /* not intended for instantiation */
    private BuildAnAtomStrings() {}
    
    public static final String TITLE_BUILD_ATOM_MODULE = BuildAnAtomResources.getString( "title.buildAtomModule" );
    public static final String TITLE_GAME_MODULE = BuildAnAtomResources.getString( "title.gameModule" );
    public static final String TITLE_ERROR = PhetCommonResources.getString( "Common.title.error" );
}
