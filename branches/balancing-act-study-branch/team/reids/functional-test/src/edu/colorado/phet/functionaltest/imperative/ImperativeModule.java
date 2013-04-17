// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functionaltest.imperative;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;

/**
 * @author Sam Reid
 */
public class ImperativeModule extends Module {
    public ImperativeModule() {
        super( "Imperative module", new ConstantDtClock() );
        final ImperativeModel model = new ImperativeModel( getClock() );
        setSimulationPanel( new PhetPCanvas() {{
            addScreenChild( new ImperativeModelNode( model ) );
        }} );
    }
}