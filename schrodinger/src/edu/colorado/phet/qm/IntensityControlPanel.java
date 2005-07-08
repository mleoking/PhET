/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:23:25 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class IntensityControlPanel extends SchrodingerControlPanel {
    public IntensityControlPanel( IntensityModule intensityModule ) {
        super( intensityModule );
        SlitControlPanel slitControlPanel = new SlitControlPanel( intensityModule );
        addControl( slitControlPanel );
    }
}
