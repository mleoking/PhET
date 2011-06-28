// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;

/**
 * Global (application-wide) settings for Sugar and Salt Solutions, such as color scheme and developer options.
 *
 * @author Sam Reid
 */
public class GlobalState {
    public final SugarAndSaltSolutionsColorScheme colorScheme;
    public final PhetApplicationConfig config;
    public final PhetFrame frame;

    public GlobalState( SugarAndSaltSolutionsColorScheme colorScheme, PhetApplicationConfig config, PhetFrame frame ) {
        this.colorScheme = colorScheme;
        this.config = config;
        this.frame = frame;
    }
}
