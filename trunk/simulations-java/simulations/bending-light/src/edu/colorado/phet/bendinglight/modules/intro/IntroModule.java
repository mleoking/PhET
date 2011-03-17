// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.intro;

import edu.colorado.phet.bendinglight.modules.BendingLightModule;
import edu.colorado.phet.bendinglight.view.LaserView;
import edu.colorado.phet.common.phetcommon.model.property.ValueEquals;
import edu.colorado.phet.common.phetcommon.util.function.Function3;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class IntroModule extends BendingLightModule<IntroModel> {
    public IntroCanvas canvas;

    public IntroModule() {
        super( "Intro", new IntroModel() );
        canvas = new IntroCanvas<IntroModel>( getBendingLightModel(), moduleActive, resetAll, new Function3.Constant<IntroModel, Double, Double, PNode>( new PNode() ), 150,
                                              new ValueEquals<LaserView>( getBendingLightModel().laserView, LaserView.WAVE ) );
        setSimulationPanel( canvas );
    }

    @Override
    protected void resetAll() {
        super.resetAll();
        canvas.resetAll();
    }
}
