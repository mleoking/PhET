// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.common;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.Clock;

/**
 * Module for fractions sims.
 *
 * @author Sam Reid
 */
public class AbstractFractionsModule extends Module {
    public AbstractFractionsModule( String name, Clock clock ) {
        super( name, clock );
        getModulePanel().setLogoPanel( null );
        setClockControlPanel( null );
    }
}