// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.moretools;

import fj.F;
import fj.F3;

import edu.colorado.phet.bendinglight.BendingLightStrings;
import edu.colorado.phet.bendinglight.modules.BendingLightModule;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.umd.cs.piccolo.PCanvas;

import static edu.colorado.phet.bendinglight.model.BendingLightModel.DEFAULT_DT;

/**
 * Module for the "more tools" tab.
 *
 * @author Sam Reid
 */
public class MoreToolsModule extends BendingLightModule<MoreToolsModel> {

    public MoreToolsModule() {
        super( BendingLightStrings.MORE_TOOLS, new ConstantDtClock( 20, DEFAULT_DT ), new F<ConstantDtClock, MoreToolsModel>() {
                   @Override public MoreToolsModel f( final ConstantDtClock constantDtClock ) {
                       return new MoreToolsModel( constantDtClock );
                   }
               }, new F3<MoreToolsModel, BooleanProperty, Resettable, PCanvas>() {
                   @Override public PCanvas f( final MoreToolsModel moreToolsModel, final BooleanProperty booleanProperty, final Resettable resettable ) {
                       return new MoreToolsCanvas( moreToolsModel, booleanProperty, resettable );
                   }
               }
        );
    }
}