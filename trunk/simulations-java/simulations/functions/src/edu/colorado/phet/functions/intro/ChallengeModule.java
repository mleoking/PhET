package edu.colorado.phet.functions.intro;

import fj.F;
import fj.data.List;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * @author Sam Reid
 */
public class ChallengeModule extends PiccoloModule {
    public ChallengeModule( String title, List<F<IntroCanvas, Scene>> scenes ) {
        super( title, new ConstantDtClock() );
        setSimulationPanel( new IntroCanvas( scenes ) );
        setClockControlPanel( null );
    }
}