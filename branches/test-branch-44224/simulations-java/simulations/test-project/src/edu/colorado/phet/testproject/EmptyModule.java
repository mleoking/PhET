package edu.colorado.phet.testproject;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * An empty module, mainly used for testing tabs.
 */
public class EmptyModule extends PiccoloModule {
    public EmptyModule( String s ) {
        super( s, new ConstantDtClock( 30, 1 ) );
        setSimulationPanel( new PhetPCanvas() );
    }
}