/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.colormaps;

import edu.colorado.phet.qm.view.SchrodingerPanel;


/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 2:08:59 PM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class DefaultWhiteColorMap extends DefaultColorMap {

    public DefaultWhiteColorMap( SchrodingerPanel schrodingerPanel ) {
        super( schrodingerPanel );
    }

    protected double getBrightness( double x ) {
        double b = super.getBrightness( x );
        return 1 - b;
    }
}
