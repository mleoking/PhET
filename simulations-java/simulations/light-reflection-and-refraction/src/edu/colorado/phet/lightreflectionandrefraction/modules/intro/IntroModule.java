// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.lightreflectionandrefraction.model.LRRModel;

/**
 * @author Sam Reid
 */
public class IntroModule extends Module {
    public IntroModule() {
        super( "Intro", new ConstantDtClock( 30.0 ) );
        LRRModel model = new LRRModel( new ConstantDtClock( 20, 1e-15 ) );
        setSimulationPanel( new LightReflectionAndRefractionCanvas( model ) );
        getModulePanel().setLogoPanel( null );
        setClockControlPanel( null );
    }
}
