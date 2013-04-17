// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.prisms;

import fj.F;
import fj.F3;

import edu.colorado.phet.bendinglight.BendingLightStrings;
import edu.colorado.phet.bendinglight.modules.BendingLightModule;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.umd.cs.piccolo.PCanvas;

/**
 * Module for the "prism break" tab.
 *
 * @author Sam Reid
 */
public class PrismBreakModule extends BendingLightModule<PrismBreakModel> {
    public PrismBreakCanvas canvas;

    public PrismBreakModule() {
        super( BendingLightStrings.PRISM_BREAK, new ConstantDtClock( 20, PrismBreakModel.DEFAULT_DT ), new F<ConstantDtClock, PrismBreakModel>() {
                   @Override public PrismBreakModel f( final ConstantDtClock iClock ) {
                       return new PrismBreakModel( iClock );
                   }
               }, new F3<PrismBreakModel, BooleanProperty, Resettable, PCanvas>() {
                   @Override public PCanvas f( final PrismBreakModel prismBreakModel, final BooleanProperty moduleActive, final Resettable resettable ) {
                       return new PrismBreakCanvas( prismBreakModel, moduleActive, resettable );
                   }
               }
        );

        //Create and add the PrismsCanvas
        canvas = new PrismBreakCanvas( getBendingLightModel(), moduleActive, resetAll );
        setSimulationPanel( canvas );
    }
}
