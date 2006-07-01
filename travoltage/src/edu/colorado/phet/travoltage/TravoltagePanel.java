/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.piccolo.PhetPCanvas;

/**
 * User: Sam Reid
 * Date: Jun 30, 2006
 * Time: 11:24:07 PM
 * Copyright (c) Jun 30, 2006 by Sam Reid
 */

public class TravoltagePanel extends PhetPCanvas {
    public TravoltagePanel() {
        addScreenChild( new TravoltageRootNode() );
    }
}
