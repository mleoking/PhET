// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.intro;

import edu.colorado.phet.bendinglight.modules.BendingLightModule;
import edu.colorado.phet.common.phetcommon.util.Function3;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class IntroModule extends BendingLightModule<IntroModel> {
    public IntroCanvas canvas;

    public IntroModule() {
        super( "Intro", new IntroModel() );
        canvas = new IntroCanvas( getBendingLightModel(), moduleActive, resetAll, new Function3.Constant<IntroModel, Double, Double, PNode>( new PNode() ), 150 );
        setSimulationPanel( canvas );
    }

    @Override
    protected void resetAll() {
        super.resetAll();
        canvas.resetAll();
    }
}
