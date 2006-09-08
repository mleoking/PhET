/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.theramp.view.RampPanel;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 9:57:09 AM
 * Copyright (c) Feb 11, 2005 by Sam Reid
 */

public class SimpleRampModule extends RampModule {
    public SimpleRampModule( PhetFrame phetFrame, IClock clock ) {
        super( TheRampStrings.getString( "introduction" ), phetFrame, clock );
    }

    protected RampControlPanel createRampControlPanel() {
        return new SimpleRampControlPanel( this );
    }

    protected RampPanel createRampPanel() {
        return new SimpleRampPanel( this );
    }

}
