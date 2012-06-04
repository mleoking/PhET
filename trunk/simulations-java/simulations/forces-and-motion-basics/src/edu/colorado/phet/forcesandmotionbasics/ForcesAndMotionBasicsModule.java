// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * @author Sam Reid
 */
public class ForcesAndMotionBasicsModule extends Module {

    private final DefaultForcesAndMotionBasicsCanvas canvas;

    public ForcesAndMotionBasicsModule( PositionMap map ) {
        super( map.name, new ConstantDtClock() );
        ForcesAndMotionBasicsModel model = new ForcesAndMotionBasicsModel( getClock() );
        canvas = new DefaultForcesAndMotionBasicsCanvas( model, map );
        canvas.requestFocusInWindow();
        setSimulationPanel( canvas );
        setClockControlPanel( null );
        getModulePanel().setLogoPanel( null );
    }

    @Override public void activate() {
        super.activate();
        canvas.requestFocusInWindow();
        canvas.requestFocus();
        System.out.println( "ForcesAndMotionBasicsModule.activate" );
    }
}