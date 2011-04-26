// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.intro;

import edu.colorado.phet.bendinglight.BendingLightStrings;
import edu.colorado.phet.bendinglight.modules.BendingLightModule;
import edu.colorado.phet.bendinglight.view.LaserView;
import edu.colorado.phet.common.phetcommon.model.property.ValueEquals;
import edu.colorado.phet.common.phetcommon.util.function.Function3;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.bendinglight.model.BendingLightModel.WATER;

/**
 * Module for the "intro" tab.
 *
 * @author Sam Reid
 */
public class IntroModule extends BendingLightModule<IntroModel> {
    public IntroCanvas<IntroModel> canvas;

    public IntroModule() {
        super( BendingLightStrings.INTRO, new IntroModel( WATER ) );
        canvas = new IntroCanvas<IntroModel>( getBendingLightModel(), moduleActive, resetAll, new Function3.Constant<IntroModel, Double, Double, PNode>( new PNode() ), 150,
                                              new ValueEquals<LaserView>( getBendingLightModel().laserView, LaserView.WAVE ), getBendingLightModel(), "0.00", 4 );
        setSimulationPanel( canvas );
    }

    @Override
    protected void resetAll() {

        super.resetAll();
        canvas.resetAll();
    }
}
