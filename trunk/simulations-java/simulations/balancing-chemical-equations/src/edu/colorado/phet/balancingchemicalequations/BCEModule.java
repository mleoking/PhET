// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balancingchemicalequations;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;


/**
 * Base class for all modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BCEModule extends PiccoloModule {

    private final Frame parentFrame;

    public BCEModule( Frame parentFrame, String title, IClock clock, boolean startsPaused ) {
        super( title, clock, startsPaused );
        this.parentFrame = parentFrame;
        setLogoPanel( null ); // workaround for #2015
        setControlPanel( null );
        setClockControlPanel( null );
    }

    protected Frame getParentFrame() {
        return parentFrame;
    }
}
