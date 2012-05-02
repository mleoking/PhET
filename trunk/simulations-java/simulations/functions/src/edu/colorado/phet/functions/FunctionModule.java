// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * @author Sam Reid
 */
public class FunctionModule extends PiccoloModule {
    public FunctionModule( final String name ) {
        super( name, new ConstantDtClock() );
        setSimulationPanel( new PhetPCanvas() );
        setClockControlPanel( null );
    }
}