// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functionaltest.functional;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.functionaltest.functional.model.FunctionalModel;

/**
 * @author Sam Reid
 */
public class FunctionalModule extends PiccoloModule {
    public FunctionalModule() {
        super( "Functional module", new ConstantDtClock() );
        final FunctionalModel model = new FunctionalModel( getClock() );
        setSimulationPanel( new PhetPCanvas() {{
            addScreenChild( new FunctionalModelNode( model ) );
        }} );
    }
}