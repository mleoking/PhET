// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * @author Sam Reid
 */
public class ForcesAndMotionBasicsModule extends Module {
    public ForcesAndMotionBasicsModule() {
        super( "forces-and-motion-basics", new ConstantDtClock() );
        setSimulationPanel( new DefaultForcesAndMotionBasicsCanvas() );
        setClockControlPanel( null );
        getModulePanel().setLogoPanel( null );
    }
}