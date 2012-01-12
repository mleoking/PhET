// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.reactantsproductsandleftovers;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;


public class RPALModule extends PiccoloModule {

    public RPALModule( String title, IClock clock, boolean startsPaused ) {
        super( title, clock, startsPaused );
        setLogoPanel( null ); // workaround for #2015
        setControlPanel( null );
        setClockControlPanel( null );
    }
}
