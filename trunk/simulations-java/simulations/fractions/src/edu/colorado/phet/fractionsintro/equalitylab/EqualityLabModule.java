// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.equalitylab;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.fractionsintro.common.AbstractFractionsModule;

/**
 * Equality lab module.
 *
 * @author Sam Reid
 */
public class EqualityLabModule extends AbstractFractionsModule {
    public EqualityLabModule() {
        super( "Equality Lab", new ConstantDtClock() );
        final EqualityLabModel model = new EqualityLabModel();
        setSimulationPanel( new EqualityLabCanvas( model ) );
    }
}