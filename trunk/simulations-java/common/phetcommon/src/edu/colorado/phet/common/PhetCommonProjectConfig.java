/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common;

import edu.colorado.phet.common.view.util.PhetProjectConfig;

public class PhetCommonProjectConfig {
    private static PhetProjectConfig INSTANCE = PhetProjectConfig.forProject( "phetcommon" );

    public static PhetProjectConfig getInstance() {
        return INSTANCE;
    }
}
