/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d.common;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel2;

/**
 * User: Sam Reid
 * Date: Feb 1, 2005
 * Time: 11:15:58 PM
 * Copyright (c) Feb 1, 2005 by Sam Reid
 */

public class ApparatusPanel3 extends ApparatusPanel2 {
    /**
     * This constructor adds a feature that allows PhetGraphics to get mouse events
     * when the model clock is paused.
     *
     * @param model
     * @param clock
     */
    public ApparatusPanel3( BaseModel model, AbstractClock clock ) {
        super( model, clock );
        clock.removeClockTickListener( paintTickListener );
    }

    protected void init( BaseModel model ) {
        super.init( model );
        model.removeModelElement( paintModelElement );
    }
}
