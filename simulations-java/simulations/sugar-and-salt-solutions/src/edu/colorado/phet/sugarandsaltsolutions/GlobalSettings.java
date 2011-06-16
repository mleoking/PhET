// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;

/**
 * Global (application-wide) settings for Sugar and Salt Solutions, such as color scheme and developer options.
 *
 * @author Sam Reid
 */
public class GlobalSettings {
    public final SugarAndSaltSolutionsColorScheme colorScheme;
    public final PhetApplicationConfig config;

    public GlobalSettings( SugarAndSaltSolutionsColorScheme colorScheme, PhetApplicationConfig config ) {
        this.colorScheme = colorScheme;
        this.config = config;
    }
}
