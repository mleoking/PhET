package edu.colorado.phet.functions.intro;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * @author Sam Reid
 */
public class IntroModule extends PiccoloModule {
    public IntroModule() {
        super( "Intro", new ConstantDtClock() );
        setSimulationPanel( new IntroCanvas() );
        setClockControlPanel( null );
    }
}