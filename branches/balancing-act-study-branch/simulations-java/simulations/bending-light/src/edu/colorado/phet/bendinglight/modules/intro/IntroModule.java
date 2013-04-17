// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.intro;

import fj.F;
import fj.F3;

import edu.colorado.phet.bendinglight.BendingLightStrings;
import edu.colorado.phet.bendinglight.modules.BendingLightModule;
import edu.colorado.phet.bendinglight.view.LaserView;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ValueEquals;
import edu.colorado.phet.common.phetcommon.util.function.Function3;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.bendinglight.model.BendingLightModel.DEFAULT_DT;
import static edu.colorado.phet.bendinglight.model.BendingLightModel.WATER;

/**
 * Module for the "intro" tab.
 *
 * @author Sam Reid
 */
public class IntroModule extends BendingLightModule<IntroModel> {

    public IntroModule() {
        super( BendingLightStrings.INTRO, new ConstantDtClock( 20, DEFAULT_DT ), new F<ConstantDtClock, IntroModel>() {
                   @Override public IntroModel f( final ConstantDtClock constantDtClock ) {
                       return new IntroModel( constantDtClock, WATER );
                   }
               }, new F3<IntroModel, BooleanProperty, Resettable, PCanvas>() {
                   @Override public PCanvas f( final IntroModel introModel, final BooleanProperty booleanProperty, final Resettable resettable ) {
                       return new IntroCanvas<IntroModel>( introModel, booleanProperty, resettable, new Function3.Constant<IntroModel, Double, Double, PNode>( new PNode() ), 150,
                                                           new ValueEquals<LaserView>( introModel.laserView, LaserView.WAVE ), introModel, "0.00", 4 );
                   }
               }
        );
    }
}
