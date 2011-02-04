// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * @author Sam Reid
 */
public class IntroModule extends Module {
    public IntroModule() {
        super( "Intro", new ConstantDtClock( 30.0 ) );
        setSimulationPanel( new LightReflectionAndRefractionCanvas() );
        getModulePanel().setLogoPanel( null );
        setClockControlPanel( null );
    }
}
